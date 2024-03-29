package kr.hs.emirim.w2015.pickone.Activity

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
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
import kr.hs.emirim.w2015.pickone.DataClass.*
import kr.hs.emirim.w2015.pickone.R
import kr.hs.emirim.w2015.pickone.databinding.ActivityChatRoomBinding
import kr.hs.emirim.w2015.pickone.databinding.ChatroomHeaderBinding
import kr.hs.emirim.w2015.pickone.databinding.ItemMessageBinding
import org.w3c.dom.Comment
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class ChatRoomActivity : AppCompatActivity() {
    private val fireDatabase = FirebaseDatabase.getInstance().reference
    private lateinit var chatRoomUid : String
    private var uid: String? = null
    private var recyclerView: RecyclerView? = null
    private var chatinfo : ChatInfoDTO? = null
    private lateinit var binding : ActivityChatRoomBinding
    private lateinit var bindingTool : ChatroomHeaderBinding
    lateinit var toggle : ActionBarDrawerToggle
    var booksinfo : BookDTO? = null
    var line : Long = 0L
    var isNew = true
    val userskey = ArrayList<String?>()

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        bindingTool = ChatroomHeaderBinding.inflate(layoutInflater)
        setContentView (binding.root)

        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM월dd일 hh:mm")
        val curTime = dateFormat.format(Date(time)).toString()
        chatRoomUid = intent.getStringExtra("chatRoomUid").toString()
        uid = Firebase.auth.currentUser?.uid.toString()
        recyclerView = findViewById (R.id.messageActivity_recyclerview)
        setSupportActionBar(binding.toolbar)

        // 토글버튼 보여주기
        toggle = ActionBarDrawerToggle(this, binding.drawer,R.string.open,R.string.close)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 액션바부분에 토글 추가
        toggle.syncState() // 싱크 맞추기

        // 보내기 클릭 시
        binding.messageActivityImageView.setOnClickListener {
            Log.d("클릭 시 dest", "보내기 클릭함")
            val comment = ChatDTO(
                binding.messageActivityEditText.text.toString(),
                uid,
                curTime
            )
            fireDatabase.child("chatrooms").child(chatRoomUid.toString()).child("chats").push()
                .setValue(comment.to_map())
                binding.messageActivityEditText.text = null
                Log.d ("chatUidNotNull dest", "$comment")
        }
        checkChatRoom()
    }

    // 채팅방 정보 초기화
    private fun checkChatRoom() {
        fireDatabase.child("chatrooms").child(chatRoomUid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatinfo = snapshot.child("chatinfo").getValue<ChatInfoDTO>()
                    supportActionBar?.title = chatinfo?.roomname
                    binding.messageActivityImageView.isEnabled = true
                    recyclerView?.layoutManager = LinearLayoutManager(this@ChatRoomActivity)
                    recyclerView?.adapter = RecyclerViewAdapter()

                    booksinfo = snapshot.child("book").getValue<BookDTO>()
                    bindingTool.headerBookname.text = booksinfo?.name as String?
                    bindingTool.headerBookwriter.text = booksinfo?.writer
                    for(user in snapshot.child("users").children){
                        if(user.key.equals(uid)){
                            // 기존 맴버일 경우 보여질채팅 처음부터
                            line = user.value as Long
                            isNew = false
                        }
                    }
                    if(isNew) {
                        // 기존 멤버가 아니면
                        line = snapshot.child("chats").childrenCount as Long
                        fireDatabase.child("chatrooms").child("user").child(uid.toString()).setValue(line)
                    }
                }
            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        when(item.itemId){
            R.id.menu_userlist -> {
                // 유저들 목록 보여주기
                var msg = ""
                for(i in 0..userskey.size){
                    msg = msg+"\n"+userskey.get(i) as String?
                }
                AlertDialog.Builder(this)
                    .setTitle("채팅방 유저목록")
                    .setMessage(msg)
                    .setPositiveButton("확인",null)
                    .show()
            }
            R.id.menu_code ->{
                // 초대코드 : 현재방키 복사시키기
                //클립보드매니저(ClipboardManager)를 생성해주고 ClipData에 id값과 복사할 텍스트를 넣어준 후 클립보드매니저에 set해주면 된다.
                val clip : ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager;
                val clipData = ClipData.newPlainText("Roomkey",chatRoomUid as String?)
                clip.setPrimaryClip(clipData)
                Toast.makeText(this,"책팅방 코드가 복사되었습니다",Toast.LENGTH_SHORT).show()
            }
            R.id.menu_checkout->{
                // 나가기
                AlertDialog.Builder(this)
                    .setTitle("채팅방 유저목록")
                    .setMessage("책팅방을 탈주 합니다.")
                    .setPositiveButton("확인"){ dalog,i ->
                        fireDatabase.child("chatrooms").child("users").child(uid.toString()).setValue(false)
                        Toast.makeText(this,"삭제되었습니다.",Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("취소",null)
                    .show()
            }
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
                                        userskey.add(snapshot.child("nickname").value as String?)
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
                        // 채팅목록 저장하기
                        var count = 0
                        for (data in snapshot.children) {   // 그 유저가 볼수있는 채팅부터 목록 추가하기
                            count+=1    // 목록개수 카운트 
                            if(count < line){   // 볼수없으면 넘기기
                                continue
                            }
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
            return MessageViewHolder(ItemMessageBinding.inflate(LayoutInflater.from(this@ChatRoomActivity),parent,false))
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


