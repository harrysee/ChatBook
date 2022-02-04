package kr.hs.emirim.w2015.pickone.DataClass

class BookDTO(
    val uid : String,
    val name : String,
    val writer :String?,
    val link : String?
){
    fun toMap(): Map<String, Any?>{
        return mapOf(
            "uid" to uid,
            "name" to name,
            "writer" to writer,
            "link" to link
        )
    }
}

