package com.example.theoriginal2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.theoriginal2.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.buttonSignIn.setOnClickListener {

            binding.progressBarSignIn.isVisible = true

            when {
                binding.editTextEmailSignIn.text.toString().isEmpty() -> {
                    binding.editTextEmailSignIn.error = "Required Email"
                    return@setOnClickListener
                }
                binding.editTextTextPasswordSignIn.text.toString().isEmpty() -> {
                    binding.editTextTextPasswordSignIn.error = "Required Phone Number"
                    return@setOnClickListener
                }
                else -> {
                    val email = binding.editTextEmailSignIn.text.toString()
                    val password = binding.editTextTextPasswordSignIn.text.toString()


                    firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                if (firebaseAuth.currentUser!!.isEmailVerified){
                                    binding.progressBarSignIn.isVisible = false
                                    startActivity(Intent(this, MapsActivity::class.java))
                                    Toast.makeText(this, "logged in", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                else{
                                    binding.progressBarSignIn.isVisible = false
                                    Toast.makeText(this, "Verify your email first", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                //Toast.makeText(this, "${it.exception}", Toast.LENGTH_SHORT).show()
                                binding.progressBarSignIn.isVisible = false
                            }
                        }.addOnFailureListener {
                            Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT)
                                .show()
                            binding.progressBarSignIn.isVisible = false
                        }

                }
            }
        }
        binding.textViewDontHaveAccount.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        binding.textViewForgotPassword.setOnClickListener {
            ForgotFragment().show(supportFragmentManager,"forgotFragment")
        }
    }
}