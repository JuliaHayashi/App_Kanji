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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.app_kanji.databinding.FragmentFotoBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions

class Foto : Fragment() {

    private lateinit var binding: FragmentFotoBinding
    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

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
                        } else {
                            binding.resultTextView.text = "Nenhum Kanji encontrado"
                        }
                    } else {
                        binding.resultTextView.text = "Nenhum texto reconhecido"
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("FotoFragment", "Erro ao processar OCR: ${e.message}")
                    binding.resultTextView.text = "Erro ao processar a imagem"
                }
        } catch (e: Exception) {
            Log.e("FotoFragment", "Erro ao preparar imagem para OCR: ${e.message}")
        }
    }

    // Função de extensão para verificar se um caractere é um Kanji
    private fun Char.isKanji(): Boolean {
        return this in '\u4E00'..'\u9FAF'
    }
}
