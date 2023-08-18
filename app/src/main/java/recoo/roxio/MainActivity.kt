package recoo.roxio

import android.content.Intent
import android.media.MediaDrm
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.C
import com.google.android.material.bottomnavigation.BottomNavigationView
import khttp.get
import khttp.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import recoo.roxio.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    var token: String = ""


    init {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val tok = get("https://reco31.vercel.app/logindisney").text

        val headers = mapOf(
            "authority" to "disney.api.edge.bamgrid.com",
            "accept" to "application/json",
            "accept-language" to "tr-TR,tr;q=0.6",
            "authorization" to "ZGlzbmV5JmJyb3dzZXImMS4wLjA.Cu56AgSfBTDag5NiRA81oLHkDZfu5L3CKadnefEAY84",
            "content-type" to "application/json",
            "origin" to "https://www.disneyplus.com",
            "referer" to "https://www.disneyplus.com/",
            "user-agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36"
        )
        val requestBody = mapOf(
            "query" to "mutation refreshToken(\$input: RefreshTokenInput!) { refreshToken(refreshToken: \$input) { activeSession { sessionId } } }",
            "variables" to mapOf(
                "input" to mapOf(
                    "refreshToken" to tok
                )
            ),
            "operationName" to "refreshToken"
        )

        val accessTok = post("https://disney.api.edge.bamgrid.com/graph/v1/device/graphql", headers = headers, json = requestBody).jsonObject
        val accessToken = accessTok.getJSONObject("extensions")
            .getJSONObject("sdk")
            .getJSONObject("token")
            .getString("accessToken")

        token = accessToken

    }

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_action -> {
                    loadFragment(HomeFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.search_action -> {
                    loadFragment(SearchFragment())
                    return@OnNavigationItemSelectedListener true
                }
//                R.id.navigation_notifications -> {
//                    // Notifications fragmentını aç
//                    loadFragment(NotificationsFragment())
//                    return@OnNavigationItemSelectedListener true
//                }
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //binding.bottomNavigationView.itemIconTintList = null

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    private fun loadFragment(fragment: Fragment) {
        val bundle = Bundle()
        bundle.putString("token", token)
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.layoutt, fragment)
            .commit()
    }


    fun startPlayer(id: String){

        var jsonData = ""
        val head2 = mapOf(
            "host" to "disney.playback.edge.bamgrid.com",
            "accept-encoding" to "deflate, gzip",
            "authority" to "disney.playback.edge.bamgrid.com",
            "accept" to "application/vnd.media-service+json; version=6",
            "authorization" to token,
            "content-type" to "application/json",
            "user-agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36",
            "x-dss-feature-filtering" to "true"
        )
        val mediaDrm = MediaDrm(C.WIDEVINE_UUID)
        val drmLevel = mediaDrm.getPropertyString("securityLevel")
        println(drmLevel)
        if (drmLevel !=null && drmLevel=="L1"){
            Toast.makeText(binding.root.context,"DRM: L1 (FHD MOVIE)", Toast.LENGTH_LONG).show()
            jsonData = "{\"playback\":{\"attributes\":{\"resolution\":{\"max\":[\"4096X2160\"]},\"protocol\":\"HTTPS\",\"assetInsertionStrategy\":\"SGAI\",\"playbackInitiationContext\":\"ONLINE\",\"frameRates\":[60],\"slugDuration\":\"SLUG_500_MS\"},\"adTracking\":{\"limitAdTrackingEnabled\":\"YES\",\"deviceAdId\":\"00000000-0000-0000-0000-000000000000\"},\"tracking\":{\"playbackSessionId\":\"\"}}}"
        }else if (drmLevel !=null && drmLevel=="L3"){
            Toast.makeText(binding.root.context,"DRM: L3 (SD MOVIE)", Toast.LENGTH_LONG).show()
            jsonData = "{\"playback\":{\"attributes\":{\"resolution\":{\"max\":[\"1280x720\"]},\"protocol\":\"HTTPS\",\"assetInsertionStrategy\":\"SGAI\",\"playbackInitiationContext\":\"ONLINE\",\"frameRates\":[60],\"slugDuration\":\"SLUG_500_MS\"},\"adTracking\":{\"limitAdTrackingEnabled\":\"YES\",\"deviceAdId\":\"00000000-0000-0000-0000-000000000000\"},\"tracking\":{\"playbackSessionId\":\"\"}}}"
        } else{
            Toast.makeText(binding.root.context,"Phone Doesnt Support DRM", Toast.LENGTH_LONG).show()
        }

        GlobalScope.launch {
            try {
                val postId = post("https://disney.playback.edge.bamgrid.com/media/$id/scenarios/ctr-regular", data = jsonData, headers = head2).jsonObject
                withContext(Dispatchers.Main){
                    val url = postId.getJSONObject("stream").getJSONArray("sources").getJSONObject(0).getJSONObject("complete").getString("url")
                    val intent = Intent(binding.root.context, PlayerActivity::class.java)
                    intent.putExtra("url", url)
                    intent.putExtra("token", token)
                    startActivity(intent)
                }
            }catch (e: Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(binding.root.context, "Hata $e", Toast.LENGTH_LONG).show()
                }
            }

        }
    }

}