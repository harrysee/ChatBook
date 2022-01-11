package kr.hs.emirim.w2015.pickone

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ActivityResultLauncher : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var login_btn : Button
    private lateinit var input_email : TextView
    private lateinit var input_password : TextView
    var user : FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_launcher)
        auth = Firebase.auth
        input_email = findViewById(R.id.input_email)
        input_password = findViewById(R.id.input_password)
        login_btn = findViewById(R.id.login_btn)

        login_btn.setOnClickListener{
            val email = input_email.text.toString()
            val password = input_password.text.toString()

            auth.signInWithEmailAndPassword(email, password)    // 생성
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        user = auth.currentUser
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
//                        updateUI(user)
                    }else if(task.exception?.message.isNullOrEmpty()){
                        Toast.makeText(this, "오류가 발생했습니다",Toast.LENGTH_SHORT).show()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        create_user(email, password)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
//                        updateUI(null)
                    }
                }
        }

    }

    fun create_user(email : String, password : String ){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    user = auth.currentUser
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
//                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
//                    updateUI(null)
                }
            }
    }
}