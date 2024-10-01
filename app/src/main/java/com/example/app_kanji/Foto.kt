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
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class Foto : Fragment(), KanjiClickListener {

    private lateinit var binding: FragmentFotoBinding
    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CardAdapter
    private val kanjis = mutableListOf<Kanji>()
    private val kanjiList = mutableListOf<String>() // Lista para armazenar os Kanjis reconhecidos
    private var interpreter: Interpreter? = null
    private val labels = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFotoBinding.inflate(inflater, container, false)
        setupActivityResultLaunchers()
        setupInterpreter()

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

        return binding.root
    }

    private fun setupActivityResultLaunchers() {
        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { it.value }
            if (granted) {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePictureLauncher.launch(takePictureIntent)
            } else {
                Log.e("FotoFragment", "Permissão de câmera ou armazenamento negada")
            }
        }

        takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                processImageForOCR(imageBitmap)
            } else {
                Log.e("FotoFragment", "Falha ao capturar a imagem")
            }
        }

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
            databaseReference = FirebaseDatabase.getInstance().reference.child("Ideogramas")

            val image = InputImage.fromBitmap(bitmap, 0)
            val recognizer = TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    kanjiList.clear()

                    if (visionText.text.isNotEmpty()) {
                        for (block in visionText.textBlocks) {
                            for (line in block.lines) {
                                for (element in line.elements) {
                                    element.text.forEach { char ->
                                        if (char.isKanji()) {
                                            kanjiList.add(char.toString())
                                        }
                                    }
                                }
                            }
                        }

                        if (kanjiList.isNotEmpty()) {
                            binding.resultTextView.text = "Kanjis encontrados: ${kanjiList.joinToString(", ")}"
                            populateKanjis()
                        } else {
                            binding.resultTextView.text = "Nenhum Kanji encontrado"
                            adapter.updateList(emptyList())
                        }
                    } else {
                        binding.resultTextView.text = "Nenhum texto reconhecido"
                        adapter.updateList(emptyList())
                        processImageWithModel(bitmap) // Se o OCR falhar, tenta o modelo .tflite
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("FotoFragment", "Erro ao processar OCR: ${e.message}")
                    binding.resultTextView.text = "Erro ao processar a imagem"
                    adapter.updateList(emptyList())
                    processImageWithModel(bitmap) // Se o OCR falhar, tenta o modelo .tflite
                }
        } catch (e: Exception) {
            Log.e("FotoFragment", "Erro ao preparar imagem para OCR: ${e.message}")
        }
    }

    private fun populateKanjis() {
        kanjis.clear()
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ideogramSnapshot in dataSnapshot.children) {
                    val kanjiId = ideogramSnapshot.key ?: continue
                    val ideogram = ideogramSnapshot.getValue(Ideogramas::class.java) ?: continue

                    if (kanjiId in kanjiList) {
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
                        kanjis.add(kanji)
                        Log.d("KanjiList", "Kanji adicionado: ID = ${kanji.id}")
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseData", "Database error: ${databaseError.message}")
            }
        })
    }

    // Função para processar a imagem com o modelo .tflite se o OCR falhar
    private fun processImageWithModel(bitmap: Bitmap) {
        try {
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 640, 640, true)
            val tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(resizedBitmap)

            val inputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 640, 640, 3), DataType.FLOAT32)
            inputBuffer.loadBuffer(tensorImage.buffer)

            val outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, labels.size), DataType.FLOAT32)
            interpreter?.run(inputBuffer.buffer, outputBuffer.buffer)

            val outputArray = outputBuffer.floatArray
            val maxIndex = outputArray.indices.maxByOrNull { outputArray[it] } ?: -1
            val identifiedLabel = if (maxIndex != -1) labels[maxIndex] else "Nenhuma identificação"
            binding.resultTextView.text = "IA Resultado: $identifiedLabel"
        } catch (e: Exception) {
            Log.e("FotoFragment", "Erro ao processar a imagem com o modelo: ${e.message}")
        }
    }

    private fun setupInterpreter() {
        try {
            val model = FileUtil.loadMappedFile(requireContext(), "best (1)_float32.tflite")
            interpreter = Interpreter(model)

            val inputStream = requireContext().assets.open("labels.txt")
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String? = reader.readLine()
            while (line != null) {
                labels.add(line)
                line = reader.readLine()
            }
            reader.close()

            Log.d("FotoFragment", "Labels carregadas: $labels")
        } catch (e: IOException) {
            Log.e("FotoFragment", "Erro ao configurar o interpreter: ${e.message}")
        }
    }

    private fun Char.isKanji(): Boolean {
        return this in '\u4E00'..'\u9FAF'
    }

    override fun onClick(kanji: Kanji) {
        val intent = Intent(requireContext(), Kanji_InfoActivity::class.java)
        intent.putExtra(KANJI_ID_EXTRA, kanji.id)
        Log.e("FotoFragment", "Id achado: ${kanji.id}")
        startActivity(intent)
    }
}
