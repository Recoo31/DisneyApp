package recoo.roxio

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import khttp.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import reco.roxio.RecyclerAdapter
import reco.roxio.SliderAdapter
import recoo.roxio.adapter.SearchAdapter
import recoo.roxio.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {
    private lateinit var binding : FragmentSearchBinding
    private val searchHandler = Handler(Looper.getMainLooper())
    private var lastQuery = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSearchBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.mainSearch.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    search(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    lastQuery = newText
                    searchHandler.removeCallbacksAndMessages(null)
                    searchHandler.postDelayed({
                        if (lastQuery.length >= 3) {
                            search(lastQuery)
                        }
                    }, 500) // Adjust the delay as needed
                }
                return true
            }
        })
    }


    fun search(query: String){
        val TAG = "JSON_OBJ"

        GlobalScope.launch(Dispatchers.IO){
            val pagerImages = mutableListOf<String>()
            val mediaIds = mutableListOf<String>()
            val titles = mutableListOf<String>()
            val token = arguments?.getString("token")
            val head = mapOf("authorization" to token)
            
            val search = get("https://disney.content.edge.bamgrid.com/svc/search/disney/version/5.1/region/TR/audience/k-false,l-true/maturity/1850/language/tr/queryType/ge/pageSize/15/query/$query", headers = head).jsonObject
            val json = search.getJSONObject("data").getJSONObject("search").getJSONArray("hits")

            for (i in 0 until json.length()){
                val jsonObj = json.getJSONObject(i).getJSONObject("hit")

                val internalTitle = jsonObj.optString("internalTitle", "")

                val imageObj =
                    try {
                        jsonObj.getJSONObject("image").getJSONObject("tile").getJSONObject("0.67")
                    } catch (e: Exception){
                        jsonObj.getJSONObject("image").getJSONObject("tile").getJSONObject("0.75")
                    }
                val imageObjKey = imageObj.keys().iterator().next()
                val imageObjData = imageObj.getJSONObject(imageObjKey).getJSONObject("default")
                val image = if (internalTitle.isNotEmpty()) imageObjData.getString("url") else null

                val mediaId = if (internalTitle.isNotEmpty()) jsonObj.getJSONObject("mediaMetadata").getString("mediaId") else null

                val titlee = jsonObj.getJSONObject("text").getJSONObject("title").getJSONObject("full")
                val titlesObjKey = titlee.keys().iterator().next()
                Log.i(TAG, "titlee: $titlee  ||| objKey: $titlesObjKey")
                val title =  if (internalTitle.isNotEmpty()) titlee.getJSONObject(titlesObjKey).getJSONObject("default").getString("content") else null

                title?.let {
                    titles.add(it)
                }
                mediaId?.let {
                    mediaIds.add(it)
                }
                image?.let {
                    println(it)
                    pagerImages.add(it)
                }
                println(internalTitle)
            }
            withContext(Dispatchers.Main) {
                binding.searchRecycview.adapter = SearchAdapter(binding.root.context, pagerImages, mediaIds, titles) { mediaId ->
                    (requireActivity() as MainActivity).startPlayer(mediaId)
                }
            }
        }
    }

}
