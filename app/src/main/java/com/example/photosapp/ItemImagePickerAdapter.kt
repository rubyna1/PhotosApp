package com.example.photosapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ItemImagePickerAdapter(
    private val listOfImage: List<ImageModel>,
    private val listener: OnItemCallback
) :
    RecyclerView.Adapter<ItemImagePickerAdapter.ItemImagePickerViewHolder>() {
    private val listOfSelectedImages = mutableListOf<ImageModel>()

    class ItemImagePickerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.item_adapter_image_view)
        val textView: TextView = view.findViewById(R.id.item_adapter_index_text_view)
        val rootView: LinearLayout = view.findViewById(R.id.item_adapter_root_view)
        val cardView: CardView = view.findViewById(R.id.item_adapter_card_view)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemImagePickerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_adapter, parent, false)
        return ItemImagePickerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemImagePickerViewHolder, position: Int) {
        val model = listOfImage[position]
        Glide.with(holder.imageView.context).load(model.image).into(holder.imageView)
        holder.rootView.setOnClickListener {
            if (!listOfSelectedImages.contains(model) && listOfSelectedImages.size < 2) {
                listOfSelectedImages.add(model)
                holder.cardView.setBackgroundColor(
                    holder.textView.context.resources.getColor(
                        R.color.purple_500,
                        null
                    )
                )
                listener.onItemSelected(listOfSelectedImages)
            } else {
                listOfSelectedImages.remove(model)
                holder.cardView.setBackgroundColor(
                    holder.textView.context.resources.getColor(
                        R.color.white,
                        null
                    )
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return listOfImage.size
    }

    interface OnItemCallback {
        fun onItemSelected(images: List<ImageModel>)
    }
}