package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.util

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.model.Level

class LevelDesign {

    private val levelList = ArrayList<Level>()

    companion object {
        fun getLevelAndExp(): ArrayList<Level> {
            val userLevelList = ArrayList<Level>()
            for (i in 1..25) {
                print(i)
                userLevelList.add(Level(i, 10 + ((i) * (i - 1) * 5)))
            }
            for (i in userLevelList) {
                print(i.exp)
            }
            return userLevelList
        }

        fun getNeedsExp(level: Int): Int? {
            val list = getLevelAndExp()
            for (x in 0..(list.size - 1)) {
                if (list[x].level == level) {
                    return list[x].exp
                }
            }

            return 0

        }

        fun getUserLevel(exp: Int): Int {
            var level: Int = 0
            when {
                exp in 0..9 -> level = 1
                exp in 10..19 -> level = 2
                exp in 20..39 -> level = 3
                exp in 40..69 -> level = 4
                exp in 70..109 -> level = 5
                exp in 110..159 -> level = 6
                exp in 160..219 -> level = 7
                exp in 220..289 -> level = 8
                exp in 290..369 -> level = 9
                exp in 370..459 -> level = 10
                exp in 460..559 -> level = 11
                exp in 560..669 -> level = 12
                exp in 670..789 -> level = 13
                exp in 790..919 -> level = 14
                exp in 920..1059 -> level = 15
                exp in 1060..1209 -> level = 16
                exp in 1210..1369 -> level = 17
                exp in 1370..1539 -> level = 18
                exp in 1540..1719 -> level = 19
                exp in 1720..1909 -> level = 20
                exp in 1910..2109 -> level = 21
                exp in 2110..2319 -> level = 22
                exp in 2320..2539 -> level = 23
                exp in 2540..2769 -> level = 24
                exp in 2770..3019 -> level = 25
            }
            return level
        }

        fun getUserLevelText(level: Int): String? {
            var levelOut: String? = null
            when {
                level in 0..4 -> levelOut = "당뇨입문"
                level in 5..9 -> levelOut = "당뇨초보"
                level in 10..14 -> levelOut = "당뇨중수"
                level in 15..19 -> levelOut = "당뇨고수"
                level in 20..25 -> levelOut = "당뇨신"
            }
            return levelOut
        }

    }


}