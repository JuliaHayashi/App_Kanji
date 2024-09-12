package com.example.app_kanji

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.app_kanji.databinding.FragmentFotoBinding
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class Foto : Fragment() {

    private lateinit var binding: FragmentFotoBinding
    private var interpreter: Interpreter? = null
    private val labels = mutableListOf<String>()
    private val REQUEST_CAMERA_PERMISSION = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFotoBinding.inflate(inflater, container, false)

        // Check camera permissions
        checkPermissions()

        // Setup TensorFlow Lite interpreter and load labels
        setupInterpreter()

        // Handle camera button click
        binding.btnTirarFoto.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureLauncher.launch(takePictureIntent)
        }

        // Handle gallery button click
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

    private fun setupInterpreter() {
        try {
            // Load the model and initialize the interpreter
            val model = FileUtil.loadMappedFile(requireContext(), "best (1)_float32.tflite")
            interpreter = Interpreter(model)

            // Load labels from the file
            val inputStream = requireContext().assets.open("labels.txt")
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String? = reader.readLine()
            while (line != null) {
                labels.add(line)
                line = reader.readLine()
            }
            reader.close()

            Log.d("Foto", "Labels loaded: $labels")
        } catch (e: IOException) {
            Log.e("Foto", "Error setting up interpreter: ${e.message}")
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            imageBitmap?.let {
                binding.imageView.setImageBitmap(it)
                processImage(it)
            }
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val imageBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, it)
            binding.imageView.setImageBitmap(imageBitmap)
            processImage(imageBitmap)
        }
    }

    private fun processImage(image: Bitmap) {
        try {
            val resizedBitmap = Bitmap.createScaledBitmap(image, 224, 224, true) // Assuming model input size is 224x224
            val tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(resizedBitmap)

            val inputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
            inputBuffer.loadBuffer(tensorImage.buffer)

            val outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, labels.size), DataType.FLOAT32)

            interpreter?.run(inputBuffer.buffer, outputBuffer.buffer)

            val outputArray = outputBuffer.floatArray
            val maxIndex = outputArray.indices.maxByOrNull { outputArray[it] } ?: -1
            val identifiedLabel = if (maxIndex != -1) labels[maxIndex] else "Nenhuma identificação"

            // Update the UI with the identified label
            binding.resultTextView.text = "Resultado: $identifiedLabel"

        } catch (e: Exception) {
            Log.e("Foto", "Error processing image: ${e.message}")
            binding.resultTextView.text = "Erro durante o processamento"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        interpreter?.close()
    }
}
