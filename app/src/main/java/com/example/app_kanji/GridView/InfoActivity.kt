package com.example.app_kanji.GridView

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.app_kanji.GridView.KANJI_ID_EXTRA
import com.example.app_kanji.GridView.Kanji
import com.example.app_kanji.GridView.kanjiList
import com.example.app_kanji.databinding.ActivityInfoBinding

class InfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val kanjiID = intent.getIntExtra(KANJI_ID_EXTRA, -1)
        val kanji = kanjiFromID(kanjiID)
        if(kanji != null)
        {
            binding.cover.setImageResource(kanji.cover)
            binding.description.text = kanji.description
        }
    }

    private fun kanjiFromID(kanjiID: Int): Kanji?
    {
        for(kanji in kanjiList)
        {
            if(kanji.id == kanjiID)
                return kanji
        }
        return null
    }
}