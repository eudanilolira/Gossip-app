package br.ufpe.cin.gossip

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.net.URI
import java.util.*

class Register : AppCompatActivity() {

    var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val selectPhotoButton = findViewById<Button>(R.id.selectPhotoButton)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val alreadyRegisteredButton = findViewById<Button>(R.id.goToLoginActivity)

        selectPhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        alreadyRegisteredButton.setOnClickListener {
            this.goToLogin()
        }

        registerButton.setOnClickListener {
            this.register()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val selectPhotoButton = findViewById<Button>(R.id.selectPhotoButton)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            this.uri = data.data
            val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            val circle = findViewById<CircleImageView>(R.id.select_image_circle)

            circle.setImageBitmap(imageBitmap)
            selectPhotoButton.alpha = 0F
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
                this.uploadImage()
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

    private fun uploadImage() {
        val id = UUID.randomUUID()
        val ref = FirebaseStorage.getInstance().getReference("images/$id")

        if (this.uri != null) {
            ref.putFile(this.uri!!)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        saveUserToDatabse(it.toString())
                    }
                }
        }

    }

    private fun saveUserToDatabse(imageURL: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/user/$uid")
        val user = User(uid, imageURL)
        Log.d("MuitoSucesso", "O sucesso veio!!")
        ref.setValue(user).addOnSuccessListener {
            Log.d("MuitoSucesso", "O sucesso veio!!")
        }
    }
}