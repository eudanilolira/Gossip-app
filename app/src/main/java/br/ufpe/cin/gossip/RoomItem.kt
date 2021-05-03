package br.ufpe.cin.gossip

import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import java.net.InetAddress
import java.net.Socket

class RoomItem(
    var roomName: String,
    var roomDescription: String?,
    var host: InetAddress,
    private var servicePort: Int
)
    : Item<GroupieViewHolder>() {
    var roomClient: RoomClient? = null

    private var tag = "RoomClient"
    override fun getLayout() = R.layout.room_item_resource

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        var roomName = viewHolder.itemView.findViewById<TextView>(R.id.roomName)
        var roomDescription = viewHolder.itemView.findViewById<TextView>(R.id.roomDescription)
        var roomImage = viewHolder.itemView.findViewById<ImageView>(R.id.roomImage)

        roomName.text = this.roomName
        roomDescription.text = if (roomDescription != null) this.roomDescription.toString() else ""
        Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(roomImage);
    }
    override fun equals (other: Any?)
    = (other is RoomItem)
            && other.host == host
            && other.servicePort == servicePort
            && other.roomName == roomName
            && other.roomDescription == roomDescription

    fun connect () {
        if (roomClient != null ) return
        roomClient = RoomClient(host, servicePort).apply { start() }
    }
}