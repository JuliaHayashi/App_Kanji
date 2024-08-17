package com.example.app_kanji

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.example.app_kanji.databinding.FragmentFotoBinding
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class Foto : Fragment() {

    private lateinit var binding: FragmentFotoBinding
    private val REQUEST_CAMERA_PERMISSION = 1
    private var interpreter: Interpreter? = null
    private var labels = mutableListOf<String>()
    private var inputSize: Int = 0
    private var numChannels: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFotoBinding.inflate(inflater, container, false)
        checkPermissions()
        setupInterpreter()

        binding.btnTirarFoto.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureLauncher.launch(takePictureIntent)
        }

        binding.btnFoto.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        return binding.root
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            imageBitmap?.let {
                binding.imageView.setImageBitmap(it)
                processImage(it)
            } ?: run {
                Log.e("Foto", "Falha ao recuperar imagem da câmera")
            }
        } else {
            Log.e("Foto", "Falha ao capturar imagem: ${result.resultCode}")
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            try {
                val imageBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, it)
                binding.imageView.setImageBitmap(imageBitmap)
                processImage(imageBitmap)
            } catch (e: Exception) {
                Log.e("Foto", "Erro ao recuperar imagem da galeria: ${e.message}")
            }
        } ?: run {
            Log.e("Foto", "Falha ao recuperar URI da imagem")
        }
    }

    private fun setupInterpreter() {
        try {
            val model = FileUtil.loadMappedFile(requireContext(), "best (1)_float32.tflite")
            val options = Interpreter.Options()
            interpreter = Interpreter(model, options)

            val inputShape = interpreter?.getInputTensor(0)?.shape() ?: return
            inputSize = inputShape[1] // Altura e largura da imagem
            numChannels = inputShape[3] // Número de canais da imagem

            // Carrega os labels
            val inputStream: InputStream = requireContext().assets.open("labels.txt")
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String? = reader.readLine()
            while (line != null) {
                labels.add(line)
                line = reader.readLine()
            }
            reader.close()

            Log.d("Foto", "Labels carregadas: $labels")

        } catch (e: IOException) {
            Log.e("Foto", "Erro ao configurar o intérprete: ${e.message}")
        }
    }

    private fun processImage(image: Bitmap) {
        try {
            interpreter ?: return

            val imageProcessor = ImageProcessor.Builder()
                .add(NormalizeOp(0f, 255f))
                .add(CastOp(DataType.FLOAT32))
                .build()

            val resizedBitmap = Bitmap.createScaledBitmap(image, inputSize, inputSize, true)
            val tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(resizedBitmap)

            val processedImage = imageProcessor.process(tensorImage)
            val imageBuffer = processedImage.buffer

            val inputShape = interpreter?.getInputTensor(0)?.shape() ?: return
            val inputBuffer = TensorBuffer.createFixedSize(inputShape, DataType.FLOAT32)

            // Fill input buffer with image data
            inputBuffer.loadBuffer(imageBuffer)

            val outputShape = interpreter?.getOutputTensor(0)?.shape() ?: return
            val outputBuffer = TensorBuffer.createFixedSize(outputShape, DataType.FLOAT32)

            // Run inference
            interpreter?.run(inputBuffer.buffer.rewind(), outputBuffer.buffer.rewind())

            val outputArray = outputBuffer.floatArray

            // Print the shape of the output tensor for debugging
            Log.d("Foto", "Output Shape: ${outputShape.joinToString(", ")}")

            // Check if the length of outputArray matches the number of labels
            if (outputArray.size == labels.size) {
                val predictedIndex = outputArray.indices.maxByOrNull { outputArray[it] } ?: -1
                Log.d("Foto", "Predicted Index: $predictedIndex")

                val resultText = if (predictedIndex in labels.indices) {
                    labels[predictedIndex]
                } else {
                    "Unknown"
                }

                binding.resultTextView.text = resultText
            } else {
                Log.e("Foto", "Output array length does not match number of labels")
                binding.resultTextView.text = "Error"
            }

        } catch (e: Exception) {
            Log.e("Foto", "Erro durante o processamento da imagem: ${e.message}")
            binding.resultTextView.text = "Error"
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        interpreter?.close()
    }
}
