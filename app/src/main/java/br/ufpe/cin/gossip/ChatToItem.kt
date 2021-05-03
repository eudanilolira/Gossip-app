package br.ufpe.cin.gossip

import android.widget.ImageView
import android.widget.TextView
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class ChatToItem(val text: String): Item<GroupieViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.chat_item_layout_to
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        var sended_message = viewHolder.itemView.findViewById<TextView>(R.id.text_view_to)
        sended_message.text = text
        var imgProfile = viewHolder.itemView.findViewById<ImageView>(R.id.imageView2)
        imgProfile.setImageBitmap(GossipApplication.profilePicture)
    }
}