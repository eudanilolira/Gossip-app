package br.ufpe.cin.gossip

import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.TextView
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class ChatImageFrom(val username: String, val img: Bitmap): Item<GroupieViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.img_to_item_layout
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val profilePicture = viewHolder.itemView.findViewById<ImageView>(R.id.imageChatFromProfile)
        val imageContent = viewHolder.itemView.findViewById<ImageView>(R.id.messageImageFrom)

        imageContent.setImageBitmap(img)
        val profileBitmap: Bitmap? = GossipApplication.room?.roomClient?.userNameToPictureMap?.get(username)
        if (profileBitmap != null) {
            profilePicture.setImageBitmap(profileBitmap)
        }
    }
}