package br.ufpe.cin.gossip

import FirebaseGlobals.firebaseAuth
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseUser

class SalasMenu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_salas_menu)
        this.isUserLoggedIn()



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.m_enu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    fun isUserLoggedIn() {
        val user: FirebaseUser? = firebaseAuth.currentUser
        if (user != null) {
            Log.d("firebase", "Dale dale")
        } else {
            val registerActivity = Intent(this, Register::class.java)
            startActivity(registerActivity)
        }
    }
}

class SalasFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_salas_menu, container, false)
    }
}