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
    private val labels = mutableListOf<String>()
    private var inputSize: Int = 0
    private var numChannels: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
                Log.e("Foto", "Failed to retrieve image from the camera")
            }
        } else {
            Log.e("Foto", "Failed to capture image: ${result.resultCode}")
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            try {
                val imageBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, it)
                binding.imageView.setImageBitmap(imageBitmap)
                processImage(imageBitmap)
            } catch (e: Exception) {
                Log.e("Foto", "Error retrieving image from gallery: ${e.message}")
            }
        } ?: run {
            Log.e("Foto", "Failed to retrieve URI of the image")
        }
    }

    private fun setupInterpreter() {
        try {
            val model = FileUtil.loadMappedFile(requireContext(), "best (1)_float32.tflite")
            val options = Interpreter.Options()
            interpreter = Interpreter(model, options)

            val inputShape = interpreter?.getInputTensor(0)?.shape() ?: return
            inputSize = inputShape[1] // Height and width of the image
            numChannels = inputShape[3] // Number of image channels

            // Load labels
            val inputStream: InputStream = requireContext().assets.open("labels.txt")
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String? = reader.readLine()
            while (line != null) {
                labels.add(line)
                line = reader.readLine()
            }
            reader.close()

            Log.d("Foto", "Labels loaded: $labels")

        } catch (e: IOException) {
            Log.e("Foto", "Error setting up the interpreter: ${e.message}")
        }
    }

    private fun processImage(bitmap: Bitmap) {
        // Store the interpreter in a local variable
        val interpreterLocal = interpreter
        if (interpreterLocal == null) {
            Log.e("Foto", "Interpreter is not initialized.")
            binding.resultTextView.text = "Error"
            return
        }

        try {
            // Convert bitmap to TensorImage
            val tensorImage = TensorImage.fromBitmap(bitmap)

            // Prepare input tensor
            val inputTensor = tensorImage.buffer

            // Create output tensor
            val outputShape = intArrayOf(1, 9, 8400) // Model's output shape
            val outputBuffer = FloatArray(9 * 8400) // Adjust size based on output shape
            val outputTensor = TensorBuffer.createFixedSize(outputShape, DataType.FLOAT32)

            // Run inference
            interpreterLocal.run(inputTensor, outputTensor.buffer.rewind())

            // Get output array
            val outputArray = outputTensor.floatArray

            // Initialize an array to store aggregated scores for each class
            val classScores = FloatArray(9) { Float.MIN_VALUE }

            // Aggregate scores for each category
            for (category in 0 until 9) {
                var maxScore = Float.MIN_VALUE
                for (i in 0 until 8400) {
                    val score = outputArray[category * 8400 + i]
                    if (score > maxScore) {
                        maxScore = score
                    }
                }
                classScores[category] = maxScore
            }

            // Find the index of the maximum score among the 9 categories
            val predictedClassIndex = classScores.indexOfMax()

            // Map the index to the corresponding label if within range
            val resultLabel = if (predictedClassIndex < labels.size) {
                labels[predictedClassIndex]
            } else {
                "Unknown"
            }

            // Display the result
            binding.resultTextView.text = resultLabel

        } catch (e: Exception) {
            Log.e("Foto", "Error during image processing: ${e.message}")
            binding.resultTextView.text = "Error"
        }
    }
    
    // Utility function to find the index of the maximum value in an array
    private fun FloatArray.indexOfMax(): Int {
        var maxIndex = 0
        for (i in this.indices) {
            if (this[i] > this[maxIndex]) {
                maxIndex = i
            }
        }
        return maxIndex
    }

    override fun onDestroyView() {
        super.onDestroyView()
        interpreter?.close()
    }
}
