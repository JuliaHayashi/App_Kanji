package com.example.app_kanji.Desenhar

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.app_kanji.R
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class DesenhoActivity : AppCompatActivity() {

    private lateinit var desenhoView: DesenhoClass
    private lateinit var backButton: ImageView
    private lateinit var restartButton: ImageView
    private lateinit var kanjiImageView: ImageView
    private lateinit var toggleImageIcon: ImageView
    private lateinit var interpreter: Interpreter
    private lateinit var labels: List<String>  // Declarando labels como uma propriedade da classe
    private var isImageVisible: Boolean = true
    private var expectedKanji: String = ""  // O kanji esperado será passado via Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desenho)

        // Inicializa os rótulos carregando o arquivo labels.txt
        labels = loadLabels()

        desenhoView = findViewById(R.id.myDrawingView)
        backButton = findViewById(R.id.backButton)
        restartButton = findViewById(R.id.restartButton)
        kanjiImageView = findViewById(R.id.kanjiImage)
        toggleImageIcon = findViewById(R.id.toggleImageIcon)

        // Carregar o modelo TFLite
        interpreter = Interpreter(loadModelFile())

        // Ler o kanji esperado da intenção
        expectedKanji = intent.getStringExtra("KANJI_ID") ?: ""

        backButton.setOnClickListener {
            finish()
        }

        restartButton.setOnClickListener {
            desenhoView.clear() // Método que deve ser implementado em `DesenhoClass` para limpar o desenho
        }



        val animationButton = findViewById<ImageView>(R.id.animationButton)
        animationButton.setOnClickListener {
            val kanjiHexCode = expectedKanji.codePointAt(0).toString(16)  // Converte o kanji para seu código hexadecimal
            val resourceId = resources.getIdentifier("u$kanjiHexCode", "raw", packageName)  // Encontra o ID do recurso SVG correspondente

            if (resourceId != 0) {
                val intent = Intent(this, AnimationActivity::class.java)
                intent.putExtra("KANJI_SVG_RESOURCE_ID", resourceId)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Kanji SVG não encontrado", Toast.LENGTH_SHORT).show()
            }
        }


        // Recebe a URL da imagem do kanji da atividade anterior
        val kanjiImageUrl = intent.getStringExtra("KANJI_IMAGE_URL")
        if (kanjiImageUrl != null) {
            // Usa Glide para carregar a imagem do kanji no ImageView
            Glide.with(this)
                .load(kanjiImageUrl)
                .placeholder(R.drawable.baseline_info_24)
                .error(R.drawable.baseline_info_24)
                .into(kanjiImageView)
        }

        // Configuração do botão de alternância (mostrar/ocultar imagem)
        toggleImageIcon.setOnClickListener {
            if (isImageVisible) {
                kanjiImageView.visibility = View.GONE  // Esconder a imagem
                toggleImageIcon.setImageResource(R.drawable.baseline_disabled_visible_24)  // Alterar ícone
            } else {
                kanjiImageView.visibility = View.VISIBLE  // Mostrar a imagem
                toggleImageIcon.setImageResource(R.drawable.baseline_remove_red_eye_24)  // Alterar ícone
            }
            isImageVisible = !isImageVisible  // Alternar o estado de visibilidade
        }

        // Botão para verificar o kanji desenhado
        val checkKanjiButton: Button = findViewById(R.id.checkKanjiButton)
        checkKanjiButton.setOnClickListener {
            checkKanji()
        }
    }

    // Função para carregar o modelo .tflite
    private fun loadModelFile(): ByteBuffer {
        val assetFileDescriptor = assets.openFd("best (1)_float32.tflite")
        val fileInputStream = assetFileDescriptor.createInputStream()
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(java.nio.channels.FileChannel.MapMode.READ_ONLY, startOffset, declaredLength).apply {
            order(ByteOrder.nativeOrder())
        }
    }

    // Função para carregar o arquivo labels.txt
    private fun loadLabels(): List<String> {
        val labels = mutableListOf<String>()
        val reader = assets.open("labels.txt").bufferedReader()
        reader.useLines { lines ->
            lines.forEach { line ->
                labels.add(line)
            }
        }
        return labels
    }

    // Função que processa o desenho atual e verifica o kanji
    private fun checkKanji() {
        val bitmap = captureBitmapFromView(desenhoView)  // Captura o bitmap da view de desenho

        // Processar a imagem
        processImage(bitmap)
    }

    private fun processImage(image: Bitmap) {
        try {
            val inputSize = 640  // O modelo espera 640x640 pixels
            val numChannels = 3  // Número de canais (RGB)

            // Redimensionar a imagem para 640x640 (o tamanho esperado pelo modelo)
            val resizedImage = Bitmap.createScaledBitmap(image, inputSize, inputSize, true)

            // Converter o Bitmap em TensorImage
            val tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(resizedImage)

            // Criar o buffer de entrada para o modelo (1, 640, 640, 3)
            val inputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, inputSize, inputSize, numChannels), DataType.FLOAT32)
            inputBuffer.loadBuffer(tensorImage.buffer)

            // Criar o buffer de saída com as dimensões esperadas pelo modelo
            val outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 9, 8400), DataType.FLOAT32)

            // Executar a inferência
            interpreter.run(inputBuffer.buffer, outputBuffer.buffer)

            // Processar o resultado da inferência
            val outputArray = outputBuffer.floatArray
            val maxIndex = outputArray.indices.maxByOrNull { outputArray[it] } ?: -1
            val maxScore = outputArray[maxIndex]

            // Mapear o índice para o label (kanji) identificado
            val identifiedLabel = if (maxIndex != -1) labels[maxIndex % labels.size] else "Nenhuma identificação"
            Log.d("Foto", "Kanji identificado: $identifiedLabel (pontuação: $maxScore)")

            // Comparar o kanji identificado com o esperado
            if (identifiedLabel == expectedKanji) {
                Toast.makeText(this, "Kanji está correto!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Kanji está incorreto!", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Log.e("Foto", "Error processing image: ${e.message}")
        }
    }

    // Função para capturar o bitmap da view de desenho
    private fun captureBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }
}
