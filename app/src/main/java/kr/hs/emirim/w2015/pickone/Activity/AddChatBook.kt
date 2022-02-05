package kr.hs.emirim.w2015.pickone.Activity

import android.os.Bundle
import android.os.SystemClock
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.hs.emirim.w2015.pickone.DataClass.BookDTO
import kr.hs.emirim.w2015.pickone.DataClass.ChatInfoDTO
import kr.hs.emirim.w2015.pickone.R
import kr.hs.emirim.w2015.pickone.databinding.ActivityAddChatbookBinding

class AddChatBook : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    var addCnt = 0
    var type = ""
    private lateinit var auth: FirebaseAuth
    lateinit var hintbox: LinearLayout
    lateinit var itemlist_box: ConstraintLayout
    lateinit var binding : ActivityAddChatbookBinding
    lateinit var user : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddChatbookBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        user = auth.currentUser?.uid.toString()
        database = FirebaseDatabase.getInstance()
        val databaseReference = database.getReference()

        binding.addchatSubmit.setOnClickListener {
            val asc = AlertDialog.Builder(this)
                .setTitle("책팅방 생성")
                .setMessage("책팅방을 생성하시겠습니까?")
                .setPositiveButton("생성"){ dialog, i ->
                    // 채팅방 키
                    val chatkey = databaseReference.child("chatrooms").push().key
                    chatkey?.let { it1 ->
                        databaseReference.child("chatrooms").child(chatkey).child("book").setValue(getBook()) //책정보 데이터넣기
                        // 채팅방 정보 설정
                        databaseReference.child("chatrooms").child(chatkey).child("chatInfo").setValue(getChatInfo()) // 방정보 생성
                        databaseReference.child("chatrooms").child(chatkey).child("users").child(user).setValue(0L) // 방정보 생성
                        databaseReference.child("users").child(user.toString()).child("rooms").child(chatkey).setValue(true) // 방정보 생성
                    }
                    // 유저의 채팅방 목록에 추가
                }
                .setNegativeButton("취소",null)
        }

    }
    fun getBook() : Map<String, Any?>{
        var name = "null"
        val writer = binding.addbookWriter.text.toString()
        val link = binding.addbookLink.text.toString()
        if(binding.addbookName.text==null){
            printError("책 제목")
        }else{
            name = name.toString()
        }
        return BookDTO(
            user,
            name,
            writer,
            link
        ).toMap()
    }

    fun getChatInfo() : Map<String,Any?>{
        var name = "null"
        val genres = when{
            binding.chatGenre1.isChecked -> R.string.genre1.toString()
            binding.chatGenre1.isChecked -> R.string.genre2.toString()
            binding.chatGenre1.isChecked -> R.string.genre3.toString()
            binding.chatGenre1.isChecked -> R.string.genre4.toString()
            binding.chatGenre1.isChecked -> R.string.genre5.toString()
            binding.chatGenre1.isChecked -> R.string.genre6.toString()
            else -> R.string.genre1.toString()
        }
        if(binding.addtitleEditText.text==null){
            printError("방이름")
        }else{
            name = name.toString()
        }

        return ChatInfoDTO(
            roomname = name,
            genres = genres,
            creator = user,
            comment = binding.addexEditText.text as String?,
            date = SystemClock.currentThreadTimeMillis()
        ).toMap()
    }

    fun printError(line : String){

    }

}