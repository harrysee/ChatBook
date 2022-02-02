package kr.hs.emirim.w2015.pickone.DataClass

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ItemDataClass(
        var title: String,
        var ex: String,
        var itemList: ArrayList<String?>,
        var datetime: Long,
        var type: String,
        var state: Boolean,
        val uid: String?
) {

    fun to_Map() : Map<String, Any?> {
        return mapOf(
            "title" to title,
            "explan" to ex,
            "itemList" to itemList,
            "datetime" to datetime,
            "type" to type,
            "state" to state,
            "uid" to uid
        )
    }

}


