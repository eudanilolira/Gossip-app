package br.ufpe.cin.gossip

import android.net.wifi.p2p.WifiP2pDevice
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class ChatToItem (): Item<GroupieViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.chat_item_layout_to
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }
}