package br.ufpe.cin.gossip

import android.media.Image
import android.net.wifi.p2p.WifiP2pDevice
import android.widget.ImageView
import android.widget.TextView
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class RoomItem (
     var roomName: String,
     var roomDescription: String,
     var roomImage: String,
     var device: WifiP2pDevice?
): Item<GroupieViewHolder>() {

    override fun getLayout(): Int {
        var layout = R.layout.room_item_resource
        return layout
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        var roomName = viewHolder.itemView.findViewById<TextView>(R.id.roomName)
        var roomDescription = viewHolder.itemView.findViewById<TextView>(R.id.roomDescription)
        var roomImage = viewHolder.itemView.findViewById<ImageView>(R.id.roomImage)

        roomName.text = this.roomName
        roomDescription.text = this.roomDescription
        //adicionar o room image
    }
}