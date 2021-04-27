//DEPRECATED

package br.ufpe.cin.gossip

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.json.JSONArray
import org.json.JSONObject

// Source: https://stackoverflow.com/questions/44870961/how-to-map-a-json-string-to-kotlin-map
fun JSONObject.toMap(): Map<String, *> = keys().asSequence().associateWith {
    when (val value = this[it])
    {
        is JSONArray ->
        {
            val map = (0 until value.length()).associate { Pair(it.toString(), value[it]) }
            JSONObject(map).toMap().values.toList()
        }
        is JSONObject -> value.toMap()
        JSONObject.NULL -> null
        else            -> value
    }
}

class FindRoomActivity : AppCompatActivity() {
    private var tag: String = "FindRoomActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_room)

    }

    private fun startComponents () {}

    private fun setUpListeners () {}

}