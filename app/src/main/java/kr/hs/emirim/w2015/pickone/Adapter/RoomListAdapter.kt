package kr.hs.emirim.w2015.pickone.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kr.hs.emirim.w2015.pickone.Activity.ChatRoomActivity
import kr.hs.emirim.w2015.pickone.Activity.MainActivity
import kr.hs.emirim.w2015.pickone.DataClass.ChatInfokeyDTO
import kr.hs.emirim.w2015.pickone.databinding.ItemChatbooksBinding

class ViewHolder(val binding : ItemChatbooksBinding) : RecyclerView.ViewHolder(binding.root)
class RoomListAdapter(val data : ArrayList<ChatInfokeyDTO>,val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(ItemChatbooksBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as ViewHolder).binding
        binding.chatTitle.text = data[position].roomname
        binding.chatGenre.text = data[position].genres
        binding.itemChatnum.text = "방번호#${position+1}"

        binding.chatlistItem.setOnClickListener {
            val intent = Intent(context, ChatRoomActivity::class.java)
            intent.putExtra("chatRoomUid", data[position].key)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}