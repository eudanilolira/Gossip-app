package br.ufpe.cin.gossip

import android.app.LauncherActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    var userLoggedIn: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val salasMenuIntent = Intent(this, Register::class.java)
        startActivity(salasMenuIntent)
    }

    private fun isUserLoggedIn () :Boolean {
        return true
    }

    private suspend fun scanLocalArea(callback: () -> Unit) {
        //code
        callback()
    }
}