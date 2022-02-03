package kr.hs.emirim.w2015.pickone.DataClass

class ChatDTO (
    val message:String?,
    val user : String?,
    val timestamp : String
){
    fun to_map() : Map<String,Any?>{
        return mapOf(
            "message" to message,
            "user" to user,
            "timestamp" to timestamp
        )
    }
}

