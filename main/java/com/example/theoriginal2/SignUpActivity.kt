package com.example.theoriginal2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.theoriginal2.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

private const val TAG = "SignUpActivity"
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("user")

        binding.buttonSignUp.setOnClickListener {

           if (binding.editTextEmail.text.toString().isEmpty()) {
                binding.editTextEmail.error = "Required Email"
                return@setOnClickListener
            } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.editTextEmail.text.toString()).matches()) {
                binding.editTextEmail.error = "Email Not Valid"
                return@setOnClickListener
            }else if (binding.editTextPassword.text.toString().isEmpty()) {
                binding.editTextPassword.error = "Required Password"
                return@setOnClickListener
            } else if (binding.editTextPassword.text.length<8) {
                binding.editTextPassword.error = "Password Strength Weak"
                return@setOnClickListener
            }else if (binding.editTextConfirmPassword.text.toString().isEmpty()) {
                binding.editTextConfirmPassword.error = "Required Password"
                return@setOnClickListener
            }else if (binding.editTextConfirmPassword.text.length<8) {
                binding.editTextConfirmPassword.error = "Password Strength Weak"
                return@setOnClickListener
            } else {
                binding.progressBarSignUp.isVisible = true

                val email = binding.editTextEmail.text.toString()
                val password = binding.editTextPassword.text.toString()
                val confirmPassword = binding.editTextConfirmPassword.text.toString()


                if (password == confirmPassword) {

                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val firebaseUser = firebaseAuth.currentUser

                                //databaseReference.child(firebaseUser!!.uid).setValue(user)
                                databaseReference.child("users").child(firebaseUser!!.uid)


                                // Send a Verification Link
                                firebaseUser.sendEmailVerification().addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        "Verification sent check your email",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    binding.progressBarSignUp.isVisible =false
                                }.addOnFailureListener { it ->
                                    Log.d(
                                        TAG,
                                        "onCreate: verification not sent ${it.localizedMessage}"
                                    )
                                    binding.progressBarSignUp.isVisible =false
                                }

                                // Logout the created user
                                firebaseAuth.signOut()

                                val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                                startActivity(intent)
                                finish()
                                binding.progressBarSignUp.visibility = View.INVISIBLE

                            } else {
                                //Toast.makeText(this, "${it.exception}", Toast.LENGTH_SHORT).show()
                                binding.progressBarSignUp.isVisible =false
                            }
                        }.addOnFailureListener {
                            Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                            binding.progressBarSignUp.isVisible =false
                        }

                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    binding.progressBarSignUp.isVisible =false
                }

            }
        }

        binding.alreadyHaveAccount.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }


    }
}