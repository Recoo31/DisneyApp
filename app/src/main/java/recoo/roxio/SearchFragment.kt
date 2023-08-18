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
            val head = mapOf("authorization" to "Bearer eyJ6aXAiOiJERUYiLCJraWQiOiJ0Vy10M2ZQUTJEN2Q0YlBWTU1rSkd4dkJlZ0ZXQkdXek5KcFFtOGRJMWYwIiwiY3R5IjoiSldUIiwiZW5jIjoiQzIwUCIsImFsZyI6ImRpciJ9..eyWNuJLv-sqmcGPv.m3gt8Lubnk9F9Fmy_P4LvrBb0AD4BNz5sOD2msVUC18DL3Rh8OJUuJX7wtKXuyexckYOfGhdrramM6YV3sGCHWdyeN_sr-lV9af-Fx9CTwrz4SjwL3uY8WuUlcodLIniMn5xGLT5j1bsgGMScMCuezDNB9raSi7oVhblQ9wlr6qZ7rXaK5_dTnWpcnKTejvjPEdN172omlOoWc7PwtY1ocBxOE8Nh3HzA5xUp8c14OrmO8pcZ2Ce6OevOyNA2iNwlm1qrZV_39SYUPnz8pu217odR2y8i_oNWVWlLAJFsdDsuu9ZQ--l9-LHhv6405ig_seOlRHoC1krPcIMdFbJWvivZawDvuWkql9gxPJ7inQbrl5AfbNoKYZGSk2c0mM94hj0cjzZbynLUmg-dttvgIOGKwKcb1abk094r9mOHFF-8QUE0TVULY6_wCi6AiMw2utG5ou6fVYtkvUkgp7MOScdUjttUaXZA7_-YFaJtG3Mcb6flsV-YoW8nM82Ecrkbv33vvNdSXFs_qCyydkEz1eGv4xr_gzm9O50P98_fQ11gun1luiw9veigcOz43bxM3rtyGiSfaJyIK5FXptZ4LIT5Z9hk8UnGDHKhy_io-qh_xbC19kWJ058EhCX0m5NmP71LEWHHmTBnX1ENmJJ0tOsqEAh5Yo0QbT23-c2bbb-clGJJhKjHzbbe06s4nwt4h-zluFCj58ZYdAuft0XmFnsYT9PEZheiguHgOSer9X6pWTVE0fzXbGPqaXBN5O3AL72-uChCsCmxHZS31Z3TqYHMPyWSRAhQuZyTe9u-9G5iXiWTWtPCQQvHm0lV9DATS9F3l43ErWxNjSp1YUgkZwJrRTQy5_TNVZAllxmY4_dyWFEY9ZBdMx00UtI05Zc5juYz_iFMH7dTtAoYbAwiv4MNwMrSbuhVCaAc_0BkEkOVqw1bZFRdukqUlNTXayw0UWh6GYMW8vflXC4uykU1PR8BVlZqMURP1_KyjM-qi76ykeucw8-mbhWRBvmswOcMmygxqATGWZNDqM5-tK01uc7R3PwjaNJwEk07H8CvYLGLVsWtqBzpyEo2qAiCCTy0kcJTNfpnYstGwWBao_S8YxAtKyG3JhNE07OehzDt_a56fIt2rNKDBKgp8rMAl-P7XW1W2iNgBa1QkZcgr_G1RtOQcse9ze4nnSrdUEZFaFx8GMfhDP5-NEirS8s6C5ZswQfVgEu3W42BoTzDL-cV4pC6rYP2xU141aSG75qyk1kI9jKtQX-TuCsggeNXWHOHtlUJNtEbuKywGQR084tUYp4gyc85KxNKxXiaPn89hAPH6vsWbGPqbZxSp00xpXKPjp_taCBJFOxHMlzwSbC9yVyuSQx-oUI4loeFXS3CIsX5JFuxrb8Tb_pqsXuUD8diaNo3RTzzPRqPCut4IKdB5uRsJSpwuqbuaHzlMzaS03P7xxi4co79_O0rP5Wx3ywKq7c-F4gLxdKOMb_gNWhxw9vve8_bBfiTnWy5IuDGIrk2JSTMdn01dsvlx4femALUUxEu7cwbT7lzPIL1uSdLhhpGPqrHMn6fSPljbYkSU2s72pDQsf5Xul2J3LvIVCocCrN1YAFBk8FXSkwuE92NT2z4coSJ7EZXukWc3yjfYBVH0CoFt5ZC4SG6KsijErr9wRX1ZmovrBm3LgW7rO6lWBuRXksys7ZLqQHIVK_kJMirD9xHcvewxjyzu-g46iUHZSR_yCEmry-ONdPEJUZeBTr6x8Td2Ew91zbbJA2ZRrCw4WSZLH6svkkRFXZIu5MSH-8hldlgkjArHKq7xgrKrh2YRN3baKGueenzLEfzqd1bJGmEuXmy9Knvd24iItdlDtyed-vjUZnx2IW_V0HCmTPdq_SRMwy9aAq_WtvKiPSX9y5mDU3oiO6JibHP8JiLbkEurfd6icCmAlOSVmiLFrm_IYghnASRMXmlZwWNzw4cXcZZMVMMM6adQQ474KRA77wYUBrYItz2hQ6GhrOFwJY24XBtnoT-vKUziyGSC9JPm0zuIfYFTKHK1OeQGJQzKYvURY-8Hti7bOO509sbc2TbX82iwB1AU31PnqR64_QYkwJ7Cmw-THu9OeeIjqm7oh3uqe0067iZkauZm3jcPvnrvjYB6wODqgI5yeeC1yIpZc6aXau-6mPl47haa5d5C6sQEp0iMMGXlHwhAsbTCTObc8JqjTJj9P2bMnNrAvzTmjpGCkMARYGpsytVhgHjdIeu1IqUC4E4GAh4b-e7Xx1VV7XtjodpRxz2kPEVDWhpBKrTynpjh-_MbQQdm0Bq5S5L_7n57jt8Q9I60c8JRNffVleq7rMpE44BvxmHFpCV2-EU88AAqkpjACWzFvjSMrDok8oBGtOcjJpxNWnFoeSu4Gk_w8i5_Bj5_eUHNAO_GwXaHH6XtVKiTtCVjjkSFN4i-5V8i9MwpE13kmQ1Ui69tvOZ2TqCbR_C_-KOG9QzlhDnTIUtVvtXj5bcP0Q9v1GEdY6Jui2gMhOaoIuzjrHirU8sbfdunEqYjUMJ_G6CkZfGwipBELgxPCV7ZpaQl0DkbPqWpNmCkmHiKxd3WuVcZ9TRw3Oa1rh9YfSEvEnF4wSAh07Fgj8ufIiQ-mXEq3Jhe0PFHiTPCHCDsyY9uCxTatmzNRIuKl4VoQXpORvG8tSf0UDQsshsbe0vqtAEIVr0w7CaFFnO3P17QEKlz3oCscTNcTZ-PeC6RRZS-Av7otMGTW7m40enzfCl3y-EMe_6lwORx75uq-ag1js8RYB5-N7RHOlwCaP1jAFVSODIKrHlWIRQW4Qcy-D-vMSSMbGjs6_ehQJ0wv24Ra2f2J8eg-b7twsHtquz24oCH5PIxB-gIz5nQuRQEg9H65aFwIlIw3sp_rRt0WC7DggeN8lsnjfr-AS-jX5Tv7vR-Yti8W1FtLRWEN8IWtRyqSG1WSkE4E3mDKrbpWOoWxwKOY9-RxC5raEtBgBZnaamM38aiyh547_bmhy1hKPyAVoRKMTQl8cMvhaDXU6uJsBnxVhBWDYUpj9X6LlWtv3dvxGFrTmhX9bdZUrX905JUYlvVZLwr4KsbImbNSSn2yqOR39EKAvNwbxuJb7vouKxlQvOQetZbusdkPWc9Hd362yVNGTUnaA_sb1DpO8MfwrEDPrzaOguzrn5VsMHDTcjMmQa2nzU5NF3uMVcqUYV-JxY0AvKn1_XiMyT9qPGI_VoBHZiNOZag.dkrkTRqcj40IDxKCJw17gg")

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