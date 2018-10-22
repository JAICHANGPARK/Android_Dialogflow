package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.model

import io.realm.RealmObject
import io.realm.annotations.RealmClass
import java.util.*

@RealmClass
open class Glucose() : RealmObject() {

    var date : Date? = null // 데이트 변수
    var datetime : String? = null // 날짜 시간 변수
    var rawDate : String? = null
    var rawTime : String? = null
    var userGlucoValue : Float? = null
    var userType : String? = null // 운동, 식전 등등
    var userTypeTime :String? = null // 아침 , 점심, 저녁
    var userDetailTime : String? = null // 30분, 40분, 1시간 , 2시간


}