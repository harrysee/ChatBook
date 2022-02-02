package kr.hs.emirim.w2015.pickone.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kr.hs.emirim.w2015.pickone.R

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = Firebase.auth
        val user = auth.currentUser
        
        // 뷰 가져오기
        val emailTextView = findViewById<TextView>(R.id.main_email_text)
        val mainShowView = findViewById<ImageView>(R.id.main_showPicks_btn)
        val addView = findViewById<ImageView>(R.id.main_addPick_btn)

        emailTextView.text = user?.email.toString()
        mainShowView.setOnClickListener{
            val intent = Intent(this, RoomListActivity::class.java)
            startActivity(intent)
        }
        addView.setOnClickListener{
            val intent = Intent(this, AddChatBook::class.java)
            startActivity(intent)
        }

        val text = TextView(this).let {
            it.text = user?.email
        }

    }
}