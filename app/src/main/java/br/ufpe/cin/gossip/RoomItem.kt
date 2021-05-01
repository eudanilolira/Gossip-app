package br.ufpe.cin.gossip

import android.annotation.SuppressLint
import android.net.wifi.p2p.WifiP2pDevice
import android.os.Build
import android.util.Base64
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

@RequiresApi(Build.VERSION_CODES.N)
class RoomItem (record: Map<String, String>, var device: WifiP2pDevice): Item<GroupieViewHolder>() {

    var roomName: String = record.getOrDefault("roomName", "Sem nome")
    var roomDescription: String = record.getOrDefault("roomDescription", "Sem descrição")

//    var roomName: String = Base64.decode(
//        record.getOrDefault("roomName", "Sem nome").toByteArray(), 0
//    ).toString()
//    var roomDescription: String = Base64.decode(
//        record.getOrDefault("roomDescription", "Sem descrição").toByteArray(), 0
//    ).toString()
    val roomImage = record.getOrDefault("roomImage", "")
    val port: Int = record["servicePort"].toString().toInt()

    override fun getLayout() = R.layout.room_item_resource

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        var roomName = viewHolder.itemView.findViewById<TextView>(R.id.roomName)
        var roomDescription = viewHolder.itemView.findViewById<TextView>(R.id.roomDescription)
        var roomImage = viewHolder.itemView.findViewById<ImageView>(R.id.roomImage)

        roomName.text = this.roomName
        roomDescription.text = this.roomDescription
        Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(roomImage);
    }
    override fun equals (other: Any?)
    = (other is RoomItem)
            && other.device == device
            && other.roomName == roomName
            && other.roomDescription == roomDescription
}