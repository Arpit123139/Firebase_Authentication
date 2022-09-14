package com.example.firebaseauthentication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseauthentication.databinding.ActivityRegisterBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

class RegisterActivity : AppCompatActivity() {

   // private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


       val binding= ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title="Register"                              // Changing the Title of the Action Bar

        // Configure Google Sign In        It task is to create a request  It helps to load the initial pop-up with different email ids
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))         // The id that we have copied from the firebase and store in String
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)               // we call a intent using googleSignInClient

        binding.loginTV.setOnClickListener{
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }


        // New Account Using Firebase

        binding.createAccountBtn.setOnClickListener{
            val email=binding.emailRegister.text.toString()
            val password=binding.passwordRegister.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty())
                MainActivity.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                    if(it.isSuccessful){
                        startActivity(Intent(this,LoginActivity::class.java))
                        finish()
                    }
                }.addOnFailureListener{
                    Toast.makeText(this,it.localizedMessage,Toast.LENGTH_SHORT).show()
                }
        }

        binding.googleBtn.setOnClickListener{

            // agar signout nhi karenge toh pheli baar toh pop-up() show hoga lekin doosri baar defaul email se login ho jaaega
            googleSignInClient.signOut()
            //call the pop-up
            // We are showing the intent
            startActivityForResult(googleSignInClient.signInIntent,13)           // Now we must override onActivityResult
        }

    }

    // handling Intent result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the intent from Google SignIn Api

        if (requestCode == 13 && resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        MainActivity.auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }.addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }
}