package br.ufpe.cin.gossip

import android.graphics.Bitmap
import android.widget.ImageView
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class ImageToItem (val bitmap: Bitmap) : Item<GroupieViewHolder>() {
    override fun getLayout() = R.layout.img_to_item_layout

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        var imgView = viewHolder.itemView.findViewById<ImageView>(R.id.imgContent)
        imgView.setImageBitmap(bitmap)
    }
}