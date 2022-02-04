package kr.hs.emirim.w2015.pickone.Activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kr.hs.emirim.w2015.pickone.Adapter.MainRoomsAdapter
import kr.hs.emirim.w2015.pickone.R
import kr.hs.emirim.w2015.pickone.databinding.ActivityMainBinding
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding : ActivityMainBinding
    private lateinit var firebase : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        firebase = Firebase.database.getReference()
        val user = auth.currentUser
        val pref = getSharedPreferences("user", Context.MODE_PRIVATE)
        val backgroundScope = CoroutineScope(Dispatchers.IO+ Job())
        
        // 리사이클뷰 어댑터 설정
        binding.userRoomRecycle.adapter = MainRoomsAdapter()

        // 메뉴 이벤트 설정
        val emailTextView = findViewById<TextView>(R.id.main_email_text)
        val nickname = pref.getString("nickname","미정")
        emailTextView.text = user?.email.toString()     // 상단이메일변경
        binding.honeMenu1.text = nickname   // 닉네임 메뉴 변경
        binding.honeMenu2.setOnClickListener{   // 추가메뉴
            val intent = Intent(this, AddChatBook::class.java)
            startActivity(intent)
        }
        binding.honeMenu3.setOnClickListener{
            val intent = Intent(this, RoomListActivity::class.java)
            startActivity(intent)
        }
        binding.honeMenu4.setOnClickListener {
            val code = EditText(this)
            AlertDialog.Builder(this).run {
                setTitle("초대코드입력")
                setMessage("초대코드를 입력하세요")
                setView(code)
                setPositiveButton("완료") { dialogInterface: DialogInterface, i: Int ->
                    backgroundScope.launch {
                        // 코루틴 구동 시 실행되는 코드
                        val key = code.text.toString()
                        firebase.child("chatrooms").child(key)
                            .addListenerForSingleValueEvent(object :ValueEventListener{
                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(this@MainActivity,"일치하는 책팅방이 존재하지않습니다",Toast.LENGTH_SHORT).show()
                                }

                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val intent = Intent(this@MainActivity, ChatRoomActivity::class.java)
                                    intent.putExtra("chatRoomUid",key)
                                    startActivity(intent)
                                }
                            })
                    }
                }
                setNegativeButton("취소",null)
            }.show()
        }

    }
}