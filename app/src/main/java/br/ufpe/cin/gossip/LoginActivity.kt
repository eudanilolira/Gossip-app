package br.ufpe.cin.gossip

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_activiy)

        val loginButton = findViewById<Button>(R.id.loginButton)
        val goToRegisterButton = findViewById<Button>(R.id.goToRegisterActivity)

        goToRegisterButton.setOnClickListener {
            val registerActivity = Intent(this, Register::class.java)
            startActivity(registerActivity)
        }

        loginButton.setOnClickListener {
            this.login()
        }
    }

    private fun login() {
        val emailTextField = findViewById<EditText>(R.id.editTextLoginEmail)
        val passwordTextField = findViewById<EditText>(R.id.editTextLoginPassword)

        val email = emailTextField.text.toString()
        val password = passwordTextField.text.toString()

        var text = ""
        val duration = Toast.LENGTH_SHORT

        if (email.isNullOrBlank() || password.isNullOrBlank()) {
            text = "Preenchar os dois campos"
            val toast = Toast.makeText(applicationContext, text, duration)
            toast.show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    text = "Login realizado com sucesso!"
                    val toast = Toast.makeText(applicationContext, text, duration)
                    toast.show()
                    val salasMenu = Intent(this, SalasMenu::class.java)
                    startActivity(salasMenu)
                }
            }
            .addOnFailureListener {
                text = it.message.toString()
                val toast = Toast.makeText(applicationContext, text, duration)
                toast.show()
            }
    }
}