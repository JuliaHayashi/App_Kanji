import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_kanji.GridView.CardAdapter
import com.example.app_kanji.GridView.InfoActivity
import com.example.app_kanji.GridView.KANJI_ID_EXTRA
import com.example.app_kanji.GridView.Kanji
import com.example.app_kanji.GridView.KanjiClickListener
import com.example.app_kanji.R
import com.example.app_kanji.GridView.kanjiList

class Pesquisar : Fragment(), KanjiClickListener {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pesquisar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)

        populateKanjis()

        val mainFragment = this
        recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = CardAdapter(kanjiList, mainFragment)
        }
    }

    override fun onClick(kanji: Kanji) {
        val intent = Intent(requireContext(), InfoActivity::class.java)
        intent.putExtra(KANJI_ID_EXTRA, kanji.id)
        startActivity(intent)
    }

    private fun populateKanjis() {

        kanjiList.clear()

        val kanji1 = Kanji(
            R.drawable.hi,
            "The definitive text on the healing powers of the mind/body connection. In Ageless Body, Timeless Mind, world-renowned pioneer of integrative medicine Deepak Chopra goes beyond ancient mind/body wisdom and current anti-ageing research to show that you do not have to grow old. With the passage of time, you can retain your physical vitality, creativity, memory and self-esteem. Based on the theories of Ayurveda and groundbreaking research, Chopra reveals how we can use our innate capacity for balance to direct the way our bodies metabolize time and achieve our unbounded potential."
        )
        kanjiList.add(kanji1)

        val kanji2 = Kanji(
            R.drawable.hi1,
            "This is the definitive book on mindfulness from the beloved Zen master and Nobel Peace Prize nominee Thich Nhat Hanh. With his signature clarity and warmth, he shares practical exercises and anecdotes to help us arrive at greater self-understanding and peacefulness, whether we are beginners or advanced students.\n" + "\n" + "Beautifully written, The Miracle of Mindfulness is the essential guide to welcoming presence in your life and truly living in the moment from the father of mindfulness.\n"
        )
        kanjiList.add(kanji2)

        val kanji3 = Kanji(
            R.drawable.ichi1,
            "A timeless classic in personal development, The Road Less Travelled is a landmark work that has inspired millions. Drawing on the experiences of his career as a psychiatrist, Scott Peck combines scientific and spiritual views to guide us through the difficult, painful times in life by showing us how to confront our problems through the key principles of discipline, love and grace.Teaching us how to distinguish dependency from love, how to become a more sensitive parent and how to connect with your true self, this incredible book is the key to accepting and overcoming life's challenges and achieving a higher level of self-understanding."
        )
        kanjiList.add(kanji3)

        val kanji4 = Kanji(
            R.drawable.hi3,
            "'A brave and heartbreaking novel that digs its claws into you and doesn't let go, long after you've finished it' Anna Todd, author of the After series\n" + "\n" + "'A glorious and touching read, a forever keeper' USA Today\n" + "\n" + "'Will break your heart while filling you with hope' Sarah Pekkanen, Perfect Neighbors\n"
        )
        kanjiList.add(kanji4)

        val kanji5 = Kanji(
            R.drawable.ichi4,
            "Investigative journalist Ross Coulthart has been intrigued by UFOs since mysterious glowing lights were reported near New Zealand's Kaikoura mountains when he was a teenager. The 1978 sighting is just one of thousands since the 1940s, and yet research into UFOs is still seen as the realm of crackpots and conspiracy theorists."
        )
        kanjiList.add(kanji5)

        kanjiList.add(kanji1)
        kanjiList.add(kanji2)
        kanjiList.add(kanji3)
        kanjiList.add(kanji4)
        kanjiList.add(kanji5)
    }

    companion object {
        // Companion object implementation (if needed)
    }
}
