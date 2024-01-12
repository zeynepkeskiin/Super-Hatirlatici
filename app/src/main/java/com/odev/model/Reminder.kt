package com.odev.model

import java.io.Serializable
import java.util.Date

data class Reminder (var mail : String =  "", var note : String = "", var date : Date? = null, val itemLocation : String =  ""){
}