package br.ufpe.cin.gossip

import FirebaseGlobals.firebaseAuth
import FirebaseGlobals.firebaseUser
import android.app.LauncherActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    var userLoggedIn: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user: FirebaseUser? = firebaseAuth.currentUser

//        user?.let {
//            Log.d("firebase", firebaseUser.toString())
//            val salasMenuIntent = Intent(this, SalasMenu::class.java)
//            startActivity(salasMenuIntent)
//            return
//        }
            val salasMenuIntent = Intent(this, LoginActivity::class.java)
            startActivity(salasMenuIntent)



    }
}