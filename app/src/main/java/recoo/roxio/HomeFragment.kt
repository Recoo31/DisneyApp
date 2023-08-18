package recoo.roxio

import android.content.Intent
import android.media.MediaDrm
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import khttp.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import reco.roxio.RecyclerAdapter
import reco.roxio.SliderAdapter
import recoo.roxio.databinding.FragmentHomeBinding
import kotlin.math.abs

class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    private lateinit var handler : Handler
    val token = MainActivity().token
    var PagerImages = ArrayList<Int>()

    var head = mapOf(
        "accept-encoding" to "deflate, gzip",
        "accept" to "application/json",
        "Authorization" to "Bearer $token",
        "content-type" to "application/json",
        "user-agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        init()

        setUpTransformer()

//        binding.imageSlider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
//            override fun onPageSelected(position: Int) {
//                super.onPageSelected(position)
//                handler.removeCallbacks(runnable)
//                handler.postDelayed(runnable, 4000)
//            }
//        })

        return binding.root
    }

    private fun init(){
        handler = Handler(Looper.myLooper()!!)

        try{
            GlobalScope.launch(Dispatchers.IO) {
                var images: MutableList<String>
                var mediaIds: MutableList<String>

                try {
                    val newDisney = newDisney()

                    images = newDisney.first
                    mediaIds = newDisney.second

                    withContext(Dispatchers.Main) {
                        binding.newDisneY.adapter = RecyclerAdapter(binding.root.context, images, mediaIds) { mediaId ->
                            (requireActivity() as MainActivity).startPlayer(mediaId)
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main){
                        Toast.makeText(requireContext(),"Hata! $e",Toast.LENGTH_LONG).show()
                    }
                }
            }

            GlobalScope.launch(Dispatchers.IO) {
                var images: MutableList<String>
                var mediaIds: MutableList<String>

                try {
                    val recommened = getRecommend()

                    images = recommened.first
                    mediaIds = recommened.second

                    withContext(Dispatchers.Main) {
                        binding.recommeD.adapter = RecyclerAdapter(binding.root.context, images, mediaIds) { mediaId ->
                            (requireActivity() as MainActivity).startPlayer(mediaId)
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main){
                        Toast.makeText(requireContext(),"Hata! $e",Toast.LENGTH_LONG).show()
                    }
                }
            }

            GlobalScope.launch(Dispatchers.IO) {
                var images: MutableList<String>
                var mediaIds: MutableList<String>

                try {
                    val getSlider = getSlider()

                    images = getSlider.first
                    mediaIds = getSlider.second

                    withContext(Dispatchers.Main) {
                        binding.imageSlider.adapter = SliderAdapter(binding.root.context, images, mediaIds) { mediaId ->
                            (requireActivity() as MainActivity).startPlayer(mediaId)
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main){
                        Toast.makeText(requireContext(),"Hata! $e",Toast.LENGTH_LONG).show()
                    }
                }
            }
        } catch (e: Exception){
            Toast.makeText(requireContext(),"Hata! $e",Toast.LENGTH_LONG).show()
        }
        binding.imageSlider.clipToPadding = false
        binding.imageSlider.clipChildren = false
        binding.imageSlider.offscreenPageLimit = 3
        binding.imageSlider.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

    }


    fun getSlider(): Pair<MutableList<String>, MutableList<String>> {
        val pagerImages = mutableListOf<String>()
        val mediaIds = mutableListOf<String>()

        val uri = "https://disney.content.edge.bamgrid.com/svc/content/CuratedSet/version/6.0/region/TR/audience/k-false,l-true/maturity/1850/language/tr/setId/0347bc66-1fd2-452e-853b-06fef650087b/pageSize/15/page/1\n"

        val response = get(uri, headers = head).jsonObject
        val recommendationSet = response.getJSONObject("data").getJSONObject("CuratedSet")
        val itemsArray = recommendationSet.getJSONArray("items")

        for (j in 0 until itemsArray.length()) {
            val jsonObj = itemsArray.getJSONObject(j)
            println(jsonObj)
            val internalTitle = jsonObj.optString("internalTitle", "")

            val imageObj = jsonObj.getJSONObject("image").getJSONObject("tile").getJSONObject("1.78")
            val imageObjKey = imageObj.keys().iterator().next()
            val imageObjData = imageObj.getJSONObject(imageObjKey).getJSONObject("default")
            val image = if (internalTitle.isNotEmpty()) imageObjData.getString("url") else null

            val mediaId = if (internalTitle.isNotEmpty()) jsonObj.getJSONObject("mediaMetadata").getString("mediaId") else null

            mediaId?.let {
                mediaIds.add(it)
            }
            image?.let {
                println(it)
                pagerImages.add(it)
            }
        }

        return Pair(pagerImages, mediaIds)
    }


    fun newDisney(): Pair<MutableList<String>, MutableList<String>> {
        val pagerImages = mutableListOf<String>()
        val mediaIds = mutableListOf<String>()

        for (i in 1..2) {
            val uri = "https://disney.content.edge.bamgrid.com/svc/content/PersonalizedCuratedSet/version/6.0/region/TR/audience/k-false,l-true/maturity/1350/language/tr/setId/413211c8-5c50-4c8e-b557-d7cba378c1ff/pageSize/15/page/$i"

            val response = get(uri, headers = head).jsonObject
            val recommendationSet = response.getJSONObject("data").getJSONObject("PersonalizedCuratedSet")
            val itemsArray = recommendationSet.getJSONArray("items")

            for (j in 0 until itemsArray.length()) {
                val jsonObj = itemsArray.getJSONObject(j)
                val internalTitle = jsonObj.optString("internalTitle", "")

                val imageObj = jsonObj.getJSONObject("image").getJSONObject("tile").getJSONObject("0.67")
                val image = if (internalTitle.isNotEmpty()) imageObj.getJSONObject("program").getJSONObject("default").getString("url") else null
                val mediaId = if (internalTitle.isNotEmpty()) jsonObj.getJSONObject("mediaMetadata").getString("mediaId") else null

                mediaId?.let { mediaIds.add(it) }
                image?.let { pagerImages.add(it) }
            }
        }

        return Pair(pagerImages, mediaIds)
    }


    fun getRecommend(): Pair<MutableList<String>, MutableList<String>> {
        val pagerImages = mutableListOf<String>()
        val mediaIds = mutableListOf<String>()

        for (i in 1..2) {
            val uri = "https://disney.content.edge.bamgrid.com/svc/content/RecommendationSet/version/6.0/region/TR/audience/k-false,l-true/maturity/1850/language/tr/setId/7894d9c6-43ab-4691-b349-cf72362095dd/pageSize/15/page/$i"

            val response = get(uri, headers = head).jsonObject
            val recommendationSet = response.getJSONObject("data").getJSONObject("RecommendationSet")
            val itemsArray = recommendationSet.getJSONArray("items")

            for (j in 0 until itemsArray.length()) {
                val jsonObj = itemsArray.getJSONObject(j)
                val internalTitle = jsonObj.optString("internalTitle", "")

                val imageObj = jsonObj.getJSONObject("image").getJSONObject("tile").getJSONObject("0.67")
                val image = if (internalTitle.isNotEmpty()) imageObj.getJSONObject("program").getJSONObject("default").getString("url") else null
                val mediaId = if (internalTitle.isNotEmpty()) jsonObj.getJSONObject("mediaMetadata").getString("mediaId") else null

                mediaId?.let { mediaIds.add(it) }
                image?.let { pagerImages.add(it) }
            }
        }
        return Pair(pagerImages, mediaIds)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, 4000)
    }

    private val runnable = Runnable{
        binding.imageSlider.currentItem +=1
        if(binding.imageSlider.currentItem == PagerImages.size){
            binding.imageSlider.currentItem -= PagerImages.size
        }
    }

    private fun setUpTransformer(){
        var compositePT = CompositePageTransformer()
        compositePT.addTransformer(MarginPageTransformer(0))
        compositePT.addTransformer { page, position ->
            val r = 1- abs(position)
            page.scaleY = 0.85f + r * 0.14f
        }
        binding.imageSlider.setPageTransformer(compositePT)
    }

}