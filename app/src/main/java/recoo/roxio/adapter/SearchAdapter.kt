package recoo.roxio.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.makeramen.roundedimageview.RoundedImageView
import recoo.roxio.databinding.SearchItemBinding

class SearchAdapter(
    var mContext: Context,
    var imageList: List<String>,
    var mediaIds: List<String>,
    var titles: List<String>,
    private val itemClickListener: (String) -> Unit
) : RecyclerView.Adapter<SearchAdapter.RecyclerViewHolder>() {

    inner class RecyclerViewHolder(binding: SearchItemBinding) : RecyclerView.ViewHolder(binding.root) {
        var roundedImage: RoundedImageView = binding.RoundedSearchItem
        val textView: TextView = binding.textView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val binding = SearchItemBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val imageResId = imageList[position]
        val mediaId = mediaIds[position]
        val title = titles[position]

        Glide.with(holder.roundedImage.context).load("$imageResId/scale?width=460&format=jpeg").into(holder.roundedImage)
        holder.textView.text = title

        holder.roundedImage.setOnClickListener {
            itemClickListener(mediaId)
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}
