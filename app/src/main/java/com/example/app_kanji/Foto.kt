package com.example.app_kanji

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.app_kanji.Pesquisar.CardAdapter
import com.example.app_kanji.Pesquisar.Ideogramas
import com.example.app_kanji.Pesquisar.KANJI_ID_EXTRA
import com.example.app_kanji.Pesquisar.Kanji
import com.example.app_kanji.Pesquisar.KanjiClickListener
import com.example.app_kanji.Pesquisar.Kanji_InfoActivity
import com.example.app_kanji.databinding.FragmentFotoBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions

class Foto : Fragment(), KanjiClickListener {

    private lateinit var binding: FragmentFotoBinding
    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CardAdapter
    private val kanjis = mutableListOf<Kanji>()

    // Lista para armazenar os Kanjis reconhecidos
    private val kanjiList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFotoBinding.inflate(inflater, container, false)
        setupActivityResultLaunchers()

        // Botão para capturar uma foto pela câmera
        binding.btnTirarFoto.setOnClickListener {
            if (hasCameraPermission()) {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePictureLauncher.launch(takePictureIntent)
            } else {
                requestPermissionLauncher.launch(
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                )
            }
        }

        // Botão para selecionar uma imagem da galeria
        binding.btnFoto.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        adapter = CardAdapter(kanjis, this)

        recyclerView.adapter = adapter

        // Não chama `populateKanjis()` aqui para evitar a exibição inicial

        return binding.root
    }

    private fun setupActivityResultLaunchers() {
        // Solicitação de permissão para usar a câmera
        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { it.value }
            if (granted) {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePictureLauncher.launch(takePictureIntent)
            } else {
                Log.e("FotoFragment", "Permissão de câmera ou armazenamento negada")
            }
        }

        // Captura de imagem pela câmera
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                processImageForOCR(imageBitmap)
            } else {
                Log.e("FotoFragment", "Falha ao capturar a imagem")
            }
        }

        // Seleção de imagem da galeria
        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                Glide.with(this)
                    .asBitmap()
                    .load(it)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            processImageForOCR(resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            // Limpeza de recursos, se necessário
                        }
                    })
            }
        }
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Função para processar a imagem e realizar OCR japonês
    private fun processImageForOCR(bitmap: Bitmap) {
        try {
            // Inicializa o databaseReference aqui antes de chamar populateKanjis()
            databaseReference = FirebaseDatabase.getInstance().reference.child("Ideogramas")

            // Criação do objeto InputImage para OCR
            val image = InputImage.fromBitmap(bitmap, 0)
            val recognizer = TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    // Limpar a lista de Kanjis antes de adicionar os novos
                    kanjiList.clear()

                    // Verifica se algum texto foi reconhecido
                    if (visionText.text.isNotEmpty()) {
                        // Itera pelos blocos de texto e extrai Kanjis
                        for (block in visionText.textBlocks) {
                            for (line in block.lines) {
                                for (element in line.elements) {
                                    // Adiciona cada Kanji encontrado à lista
                                    element.text.forEach { char ->
                                        if (char.isKanji()) {
                                            kanjiList.add(char.toString())
                                        }
                                    }
                                }
                            }
                        }

                        // Atualiza o TextView com a lista de Kanjis encontrados
                        if (kanjiList.isNotEmpty()) {
                            binding.resultTextView.text = "Kanjis encontrados: ${kanjiList.joinToString(", ")}"
                            // Popula os Kanjis após o OCR ser bem-sucedido
                            populateKanjis()
                        } else {
                            binding.resultTextView.text = "Nenhum Kanji encontrado"
                            adapter.updateList(emptyList()) // Limpa a lista do adapter
                        }
                    } else {
                        binding.resultTextView.text = "Nenhum texto reconhecido"
                        adapter.updateList(emptyList()) // Limpa a lista do adapter
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("FotoFragment", "Erro ao processar OCR: ${e.message}")
                    binding.resultTextView.text = "Erro ao processar a imagem"
                    adapter.updateList(emptyList()) // Limpa a lista do adapter
                }
        } catch (e: Exception) {
            Log.e("FotoFragment", "Erro ao preparar imagem para OCR: ${e.message}")
        }
    }
    

    // Função de extensão para verificar se um caractere é um Kanji
    private fun Char.isKanji(): Boolean {
        return this in '\u4E00'..'\u9FAF'
    }

    private fun populateKanjis() {
        kanjis.clear() // Limpa a lista para evitar duplicatas
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ideogramSnapshot in dataSnapshot.children) {
                    val kanjiId = ideogramSnapshot.key ?: continue
                    val ideogram = ideogramSnapshot.getValue(Ideogramas::class.java) ?: continue

                    // Adiciona o Kanji apenas se ele for reconhecido pelo OCR
                    if (kanjiId in kanjiList) {
                        // Cria um objeto Kanji e o adiciona à lista
                        val kanji = Kanji(
                            id = kanjiId,
                            imageUrl = ideogram.imagem ?: "",
                            significado = ideogram.significado ?: "",
                            onyomi = ideogram.onyomi ?: "",
                            kunyomi = ideogram.kunyomi ?: "",
                            qtd_tracos = ideogram.qtd_tracos ?: 0,
                            frequencia = ideogram.frequencia ?: 0,
                            exemplo1 = ideogram.exemplo1 ?: "",
                            ex1_significado = ideogram.ex1_significado ?: "",
                            exemplo2 = ideogram.exemplo2 ?: "",
                            ex2_significado = ideogram.ex2_significado ?: "",
                            exemplo3 = ideogram.exemplo3 ?: "",
                            ex3_significado = ideogram.ex3_significado ?: "",
                            exemplo4 = ideogram.exemplo4 ?: "",
                            ex4_significado = ideogram.ex4_significado ?: ""
                        )
                        kanjis.add(kanji) // Adiciona o objeto Kanji à lista
                        Log.d("KanjiList", "Kanji adicionado: ID = ${kanji.id}")
                    }
                }
                Log.d("KanjiList", "Número de Kanjis encontrados: ${kanjis.size}")
                adapter.notifyDataSetChanged() // Notifica o adapter sobre a mudança de dados
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseData", "Database error: ${databaseError.message}")
            }
        })
    }

    override fun onClick(kanji: Kanji) {
        val intent = Intent(requireContext(), Kanji_InfoActivity::class.java)
        intent.putExtra(KANJI_ID_EXTRA, kanji.id)
        Log.e("FotoFragment", "Id achado: ${kanji.id}")
        startActivity(intent)
    }
}
