package kr.hs.emirim.w2015.pickone.Activity

import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RestrictTo
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kr.hs.emirim.w2015.pickone.Adapter.RoomListAdapter
import kr.hs.emirim.w2015.pickone.DataClass.ChatInfokeyDTO
import kr.hs.emirim.w2015.pickone.R
import kr.hs.emirim.w2015.pickone.databinding.ActivityRoomlistBinding

class RoomListActivity : AppCompatActivity() {
    lateinit var binding: ActivityRoomlistBinding
    lateinit var dataScope : CoroutineScope
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var adapter : RoomListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomlistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dataScope = CoroutineScope(Dispatchers.IO+ Job())
        firebaseDatabase = FirebaseDatabase.getInstance()

        val datas = getData()
        adapter = RoomListAdapter(datas, this)
        binding.roomlistRecycle.layoutManager= LinearLayoutManager(this)
        binding.roomlistRecycle.adapter= adapter
    }

    fun getData() : ArrayList<ChatInfokeyDTO>{
        val data = ArrayList<ChatInfokeyDTO>()
        dataScope.launch {
            val database = firebaseDatabase.reference

            database.child("chatrooms").addValueEventListener(
                object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (item in snapshot.children){
                            val key = item.key
                            val info = item.child("chatinfo")
                            data.add(ChatInfokeyDTO(
                                key,
                                info.child("roomname").value as String?,
                                info.child("genres").value as String,
                                info.child("creator").value as String,
                                info.child("comment").value as String?
                            ))
                        }
                        adapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@RoomListActivity,"네트워크오류",Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
        return data
    }
}