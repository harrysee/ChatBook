package kr.hs.emirim.w2015.pickone.Activity

import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kr.hs.emirim.w2015.pickone.databinding.ActivityAddChatbookBinding
import kr.hs.emirim.w2015.pickone.databinding.ActivityResultLauncherBinding

class ActivityResultLauncher : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    var user : FirebaseUser? = null
    lateinit var binding: ActivityResultLauncherBinding
    lateinit var nickname : EditText
    lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        nickname = EditText(this)
        firebaseDatabase = FirebaseDatabase.getInstance()

        binding.loginBtn.setOnClickListener{
            val email = binding.inputEmail.text.toString()
            val password = binding.inputPassword.text.toString()

            auth.signInWithEmailAndPassword(email, password)    // 로그인
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
                        val dialog = AlertDialog.Builder(this).run { 
                            setTitle("회원가입 메시지")
                            setMessage("해당 아이디와 비밀번호를 찾을수 없습니다\n해당 이메일,비밀번호로 회원가입을 진행합니다.\n닉네임을 입력 > ")
                            setView(nickname)
                            setPositiveButton("yes") { dialogInterface: DialogInterface, i: Int ->
                                create_user(email, password)
                            }
                            setNegativeButton("NO"){ dialog, i->
                                Toast.makeText(baseContext, "로그인 실패",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }.show()
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
//                        updateUI(null)
                    }
                }
        }

    }


    fun create_user(email : String, password : String ){
        val pref = getSharedPreferences("user",Context.MODE_PRIVATE)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    user = auth.currentUser
                    // 유저에 닉네임 저장
                    firebaseDatabase.getReference().child("users").child(user.toString()).child("nickname").setValue(nickname.text).addOnSuccessListener {
                        // Sign in success, update UI with the signed-in user's information
                        pref.edit().run {
                            this.putString("nickname", nickname.text as String? )
                        }
                        Log.d(TAG, "createUserWithEmail:success")
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
//                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    AlertDialog.Builder(this).run {
                        setTitle("비밀번호 오류")
                        setMessage("비밀번호는 최소 6자 이상입니다.")
                        setPositiveButton("yes",null)
                        setNegativeButton("NO",null)
                    }.show()
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
//                    updateUI(null)
                }
            }
    }
}