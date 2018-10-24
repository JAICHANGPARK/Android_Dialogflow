package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.fragment

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.R
import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.model.Glucose
import android.util.Log
import android.view.View
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.screen_analysis.*
import org.jetbrains.anko.toast
import ru.semper_viventem.backdropview.ui.common.Screen
import java.util.*
import kotlin.collections.ArrayList

class AnalysisFragment : Screen() {

    val TAG = this::class.java.name

    companion object {
        val LAST_DAY = R.id.button1
        val WEEK_DAY = R.id.button2
        val MONTH_DAY = R.id.button3
    }

    override val layoutId: Int = R.layout.screen_analysis
    lateinit var entries : ArrayList<Entry>
    var realm: Realm? = null

    override fun onInitView(view: View) {
        val startDate = Calendar.getInstance()
        startDate.add(Calendar.DAY_OF_MONTH, -1)
        val endDate = Calendar.getInstance()  /* ends after 1 month from now */
        Realm.init(activity!!.applicationContext)
        val realmConfig = RealmConfiguration.Builder().name("glucose.realm").build()
        realm = Realm.getInstance(realmConfig)
//        val allGlucose = realm!!.where(Glucose::class.java).equalTo("rawDate", todayString).findAll().sort("datetime")
        val allGlucose = realm!!.where(Glucose::class.java)
                .greaterThanOrEqualTo("date", startDate.time)
                .lessThanOrEqualTo("date", endDate.time).findAll().sort("datetime")
        Log.e(TAG, "" + allGlucose.size)

        entries = ArrayList<Entry>()
        for (i in 0..(allGlucose.size -1)){
            entries.add(Entry(i.toFloat(), allGlucose[i]!!.userGlucoValue!!.toFloat()))
        }
        val lineDataSet = LineDataSet(entries, "1일")
        val lineData = LineData(lineDataSet)
        with(line_chart){
            data = lineData
            invalidate()
        }


        segmented3.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                LAST_DAY -> {
                    activity!!.toast("최근")
                }
                WEEK_DAY ->{
                    activity!!.toast("일주일")
                }
                MONTH_DAY ->{
                    activity!!.toast("한달")
                }
            }
        }


    }
}



