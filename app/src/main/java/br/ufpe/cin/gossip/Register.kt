package br.ufpe.cin.gossip

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        val registerButton = findViewById<Button>(R.id.registerButton)
        val alreadyRegisteredButton = findViewById<Button>(R.id.goToLoginActivity)

        alreadyRegisteredButton.setOnClickListener {
            this.goToLogin()
        }

        registerButton.setOnClickListener {
            this.register()
        }

    }

    private fun register() {
        val emailField = findViewById<EditText>(R.id.editTextRegisterEmail)
        val passwordField = findViewById<EditText>(R.id.editTextRegisterPassword)
        val firebase = FirebaseAuth.getInstance()

        val email = emailField.text.toString()
        val password = passwordField.text.toString()
        var text = ""
        val duration = Toast.LENGTH_SHORT

        if (email.isEmpty() || password.isEmpty()) {
            text = "Preenchar o e-mail e senha corretamente"
            val toast = Toast.makeText(applicationContext, text, duration)
            toast.show()
            return
        }

        firebase.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (!it.isSuccessful) {
                text = "Algo deu errado!"
                val toast = Toast.makeText(applicationContext, text, duration)
                toast.show()
            } else {
                text = "Registro realizado com sucesso!"
                Log.d("RegisterActivity", "Name ${it.result?.user?.uid}")
                val toast = Toast.makeText(applicationContext, text, duration)
                toast.show()
                this.goToLogin()
            }

        }.addOnFailureListener {
            val text = it.message
            val duration = Toast.LENGTH_SHORT

            val toast = Toast.makeText(applicationContext, text, duration)
            toast.show()
        }
    }

    private fun goToLogin() {
        val loginActivity = Intent(this, LoginActivity::class.java)
        startActivity(loginActivity)
    }
}