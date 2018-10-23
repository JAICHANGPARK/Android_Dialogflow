package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.util

class RecommendChat {

    companion object {
        fun getinitRecommendDataSet(): ArrayList<String> {
            val list = ArrayList<String>()
            list.add("안녕하세요")
            list.add("안녕")
            list.add("반가워")
            list.add("혈당 기록하기")
            return list
        }

        fun getRecommendYes() : ArrayList<String>{
            val list = ArrayList<String>()
            list.add("네")
            list.add("그래")
            list.add("응 그래")
            list.add("네 맞아요")
            return list
        }

        fun getRecommendNo() : ArrayList<String>{
            val list = ArrayList<String>()
            list.add("아니")
            list.add("아니야")
            return list
        }

        fun getRecommendWhen() : ArrayList<String>{
            val list = ArrayList<String>()
            list.add("지금")
            list.add("방금")
            list.add("오늘 오전 6시")
            list.add("오늘 오전 9시")
            list.add("오늘 1시")
            list.add("오늘 오후 6시")
            return list
        }

        fun getRecommendDetailTypeTimes() : ArrayList<String>{
            val list = ArrayList<String>()
            list.add("아침")
            list.add("점심")
            list.add("저녁")
            return list
        }

        fun getRecommendDetailTypes() : ArrayList<String>{
            val list = ArrayList<String>()
            list.add("공복")
            list.add("취침전")
            list.add("식사 전")
            list.add("식사 후")
            list.add("운동 전")
            list.add("운동 후")
            list.add("투약 전")
            list.add("투약 후")
            return list
        }
    }

}