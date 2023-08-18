package reco.roxio

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.makeramen.roundedimageview.RoundedImageView
import recoo.roxio.databinding.RecyclerContentHolderBinding


class RecyclerAdapter(
    var mContext: Context,
    var imageList: List<String>,
    var mediaIds: List<String>,
    private val itemClickListener: (String) -> Unit
) : RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>() {

    inner class RecyclerViewHolder(binding: RecyclerContentHolderBinding) : RecyclerView.ViewHolder(binding.root)
    {
        var roundedImage : RoundedImageView = binding.RoundedItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder
    {
        val binding = RecyclerContentHolderBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val imageResId = imageList[position]
        val mediaId = mediaIds[position] // Get the corresponding mediaId

        Glide.with(holder.roundedImage.context).load("$imageResId/scale?width=460&format=jpeg").into(holder.roundedImage)

        holder.roundedImage.setOnClickListener {
            itemClickListener(mediaId)
        }
    }

    override fun getItemCount(): Int
    {
        return imageList.size
    }
}
