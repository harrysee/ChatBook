package kr.hs.emirim.w2015.pickone.DataClass

class ChatInfoDTO(
    val roomname : String,
    val genres : String,
    val creator : String,
    val comment : String?,
    val date : Long
){
    fun toMap(): Map<String, Any?>{
        return mapOf(
            "roomname" to roomname,
            "genres" to genres,
            "creator" to creator,
            "comment" to comment,
            "date" to date
        )
    }
}

data class ChatInfokeyDTO(
    val key : String?,
    val roomname : String?,
    val genres : String,
    val creator : String,
    val comment : String?,
    val userCnt : Long?
)
