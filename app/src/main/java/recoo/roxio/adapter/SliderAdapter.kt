package reco.roxio

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import recoo.roxio.databinding.SlideItemContainerBinding

class SliderAdapter(
    var mContext: Context,
    var imageList: List<String>,
    var mediaIds: List<String>,
    private val itemClickListener: (String) -> Unit
) : RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    inner class SliderViewHolder(binding: SlideItemContainerBinding) : RecyclerView.ViewHolder(binding.root) {

        var roundedImage : RoundedImageView = binding.imageSlideRounded

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val binding = SlideItemContainerBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return SliderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val imageUrl = imageList[position]
        val mediaId = mediaIds[position]
        Picasso.get().load("$imageUrl/scale?width=600&format=jpeg").into(holder.roundedImage)

        holder.roundedImage.setOnClickListener {
            itemClickListener(mediaId)
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}