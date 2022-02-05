package kr.hs.emirim.w2015.pickone.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.hs.emirim.w2015.pickone.Activity.ChatRoomActivity
import kr.hs.emirim.w2015.pickone.DataClass.ChatInfokeyDTO
import kr.hs.emirim.w2015.pickone.databinding.ItemMainBinding

class UserRoomHolder(val binding : ItemMainBinding) : RecyclerView.ViewHolder(binding.root)
class MainRoomsAdapter(val data : ArrayList<ChatInfokeyDTO>,val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return UserRoomHolder(ItemMainBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as UserRoomHolder).binding
        binding.mainitemTitle.text = data[position].roomname
        binding.mainitemCount.text = "인원(${data[position].userCnt})"
        binding.mainitemGenre.text = data[position].genres

        binding.mainitemBox.setOnClickListener {
            val intent = Intent(context, ChatRoomActivity::class.java)
            intent.putExtra("chatRoomUid", data[position].key)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}