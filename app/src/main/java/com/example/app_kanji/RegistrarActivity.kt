package com.example.app_kanji

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.app_kanji.databinding.ActivityRegistrarBinding
import com.google.firebase.auth.FirebaseAuth

class RegistrarActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegistrarBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrarBinding.inflate(layoutInflater )
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.buttonRegistrar.setOnClickListener{
            val nome = binding.editNome.text.toString()
            val email = binding.editEmail.text.toString()
            val senha = binding.editSenha.text.toString()
            val confirmSenha = binding.editConfirmarSenha.text.toString()

            if (nome.isNotEmpty() && email.isNotEmpty() && senha.isNotEmpty() && confirmSenha.isNotEmpty()) {

                if(senha == confirmSenha){
                    firebaseAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener{

                        if (it.isSuccessful){
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)

                        }else{
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(this, "Senhas diferentes!!!", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Preencha todos os campos!!!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}