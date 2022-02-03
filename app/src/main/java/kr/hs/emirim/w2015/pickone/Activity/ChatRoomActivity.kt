package kr.hs.emirim.w2015.pickone.Activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kr.hs.emirim.w2015.pickone.DataClass.ChatDTO
import kr.hs.emirim.w2015.pickone.DataClass.ChatInfoDTO
import kr.hs.emirim.w2015.pickone.DataClass.Chats
import kr.hs.emirim.w2015.pickone.DataClass.UserDTO
import kr.hs.emirim.w2015.pickone.R
import kr.hs.emirim.w2015.pickone.databinding.ActivityChatRoomBinding
import kr.hs.emirim.w2015.pickone.databinding.ItemMessageBinding
import org.w3c.dom.Comment
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessageActivity : AppCompatActivity() {
    private val fireDatabase = FirebaseDatabase.getInstance().reference
    private lateinit var chatRoomUid : String
    private var destinationUid: String? = null
    private var uid: String? = null
    private var recyclerView: RecyclerView? = null
    private var chatinfo : ChatInfoDTO? = null
    private lateinit var binding : ActivityChatRoomBinding
    lateinit var toggle : ActionBarDrawerToggle

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView (binding.root)

        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM월dd일 hh:mm")
        val curTime = dateFormat.format(Date(time)).toString()
        destinationUid = intent.getStringExtra ("destinationUid")
        chatRoomUid = intent.getStringExtra("chatRoomUid").toString()
        uid = Firebase.auth.currentUser ?. uid.toString()
        recyclerView = findViewById (R.id.messageActivity_recyclerview)
        setSupportActionBar(binding.toolbar)

        // 토글버튼 보여주기
        toggle = ActionBarDrawerToggle(this, binding.drawer,R.string.open,R.string.close)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 액션바부분에 토글 추가
        toggle.syncState() // 싱크 맞추기

        // 보내기 클릭 시
        binding.messageActivityImageView.setOnClickListener {
            Log.d("클릭 시 dest", "$destinationUid")
            val comment = ChatDTO(
                binding.messageActivityEditText.text.toString(),
                uid,
                curTime
            )
            fireDatabase.child("chatrooms").child(chatRoomUid.toString()).child("chats").push()
                .setValue(comment.to_map())
                binding.messageActivityEditText.text = null
                Log.d ("chatUidNotNull dest", "$destinationUid")
        }
        checkChatRoom()
    }

    // 채팅방 정보 초기화
    private fun checkChatRoom() {
        fireDatabase.child("chatrooms").child(chatRoomUid).child("chatinfo")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatinfo = snapshot.getValue<ChatInfoDTO>()
                    supportActionBar?.title = chatinfo?.roomname
                    binding.messageActivityImageView.isEnabled = true
                    recyclerView?.layoutManager = LinearLayoutManager(this@MessageActivity)
                    recyclerView?.adapter = RecyclerViewAdapter()
                }
            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    // 채팅방 리사이클어댑터
    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.MessageViewHolder>() {
    // 단체방 사람들의 users의 uid를 모두 가져옴 - map배열로
    // for문을 돌리면서 유저객체로 map배열 uid에 각각 연결시킴
    // 리사이클뷰를 구성하며 자신의 uid가 아니면 맵으로 그 사람의 정보를 보이게함
        private val comments = ArrayList<ChatDTO>()
        private val usersinfo = mapOf<String?, String?>()
        init {
            fireDatabase.child("chatrooms").child("users")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {}
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (item in snapshot.children) {
                            fireDatabase.child("users").child(item.key.toString())
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onCancelled(error: DatabaseError) {}
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        usersinfo.plus(mapOf(item.key to snapshot.child("nickname").value as String? ))
                                    }
                                })
                        }
                        getMessageList()
                    }
                })
        }

        fun getMessageList() {
            fireDatabase.child("chatrooms").child(chatRoomUid.toString()).child("chats")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {}
                    override fun onDataChange(snapshot: DataSnapshot) {
                        comments.clear()
                        for (data in snapshot.children) {
                            val item = data.getValue<ChatDTO>()
                            comments.add(item!!)
                            println(comments)
                        }
                        notifyDataSetChanged()

                        recyclerView?.scrollToPosition(comments.size - 1)
                    }
                })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            return MessageViewHolder(ItemMessageBinding.inflate(LayoutInflater.from(this@MessageActivity),parent,false))
        }

        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
            val binding = (holder as MessageViewHolder).binding
            binding.messageItemTextViewMessage.textSize = 20F
            binding.messageItemTextViewMessage.text = comments[position].message
            binding.messageItemTextViewTime.text = comments[position].timestamp
            if (comments[position].user.equals(uid)) { // 본인 채팅
                binding.messageItemTextViewMessage.setBackgroundResource(R.drawable.mychat)
                binding.messageItemTextviewName.visibility = View.INVISIBLE
                binding.messageItemLayoutDestination.visibility =View.INVISIBLE
                binding.messageItemLinearlayoutMain.gravity = Gravity.RIGHT
            } else { // 상대방 채팅
                Glide.with(holder.itemView.context).load(R.drawable.profile)
                    .apply(RequestOptions().circleCrop())
                    .into(binding.messageItemImageviewProfile)
                binding.messageItemTextviewName.text = usersinfo[comments[position].user]
                binding.messageItemLayoutDestination.visibility = View.VISIBLE
                binding.messageItemTextviewName.visibility = View.VISIBLE
                binding.messageItemTextViewMessage.setBackgroundResource(R.drawable.yourchat)
                binding.messageItemLinearlayoutMain.gravity =Gravity.LEFT
            }
        }
        inner class MessageViewHolder(val binding : ItemMessageBinding) : RecyclerView.ViewHolder(binding.root)

        override fun getItemCount(): Int {
            return comments.size
        }
        
    }
}

