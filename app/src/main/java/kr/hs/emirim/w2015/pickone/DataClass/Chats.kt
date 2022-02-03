package kr.hs.emirim.w2015.pickone.DataClass

class Chats() {
    class ChatDTO (
        val message:String?,
        val user : String?,
        val timestamp : String
    )
    class users(val user : String)
    class ChatInfo(
        val roomname : String,
        val genres : String,
        val comment : String?,
        val bookkey : String?
    )
}