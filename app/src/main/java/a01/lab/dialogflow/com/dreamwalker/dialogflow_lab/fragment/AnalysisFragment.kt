package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.fragment

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.R
import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.model.Glucose
import android.support.v4.content.ContextCompat
import android.view.View
import com.github.mikephil.charting.components.XAxis
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

    val TAG: String = this::class.java.name

    companion object {
        val LAST_DAY = R.id.button1
        val WEEK_DAY = R.id.button2
        val MONTH_DAY = R.id.button3
    }

    override val layoutId: Int = R.layout.screen_analysis
    lateinit var entries: ArrayList<Entry>
    var realm: Realm? = null

    override fun onInitView(view: View) {

        Realm.init(activity!!.applicationContext)
        val realmConfig = RealmConfiguration.Builder().name("glucose.realm").build()
        realm = Realm.getInstance(realmConfig)


        initLineChartSettting()
        processGraph(0)

        with(segmented3) {
            setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    LAST_DAY -> {
                        processGraph(0)
                        activity!!.toast("최근 1일 데이터를 가져올게요")
                    }
                    WEEK_DAY -> {
                        processGraph(1)
                        activity!!.toast("일주일간 데이터를 가져올게요")
                    }
                    MONTH_DAY -> {
                        processGraph(2)
                        activity!!.toast("한달간 데이터를 가져올게요")
                    }
                }
            }
            check(LAST_DAY)
        }

    }

    fun checkLayoutDisplay(arrayList: ArrayList<Glucose>) {

        if (arrayList.size == 0) {
            empty_layout_analysis.visibility = View.VISIBLE
            line_chart.visibility = View.GONE
        } else {
            empty_layout_analysis.visibility = View.GONE
            line_chart.visibility = View.VISIBLE
        }

    }


    private fun initLineChartSettting() {
        val xAxis = line_chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        line_chart.axisRight.isEnabled = false

    }

    private fun processGraph(select: Int) {
        when (select) {
            0 -> {

                val startDate = Calendar.getInstance()
                startDate.add(Calendar.DAY_OF_MONTH, -1)
                val endDate = Calendar.getInstance()  /* ends after 1 month from now */

//        val allGlucose = realm!!.where(Glucose::class.java).equalTo("rawDate", todayString).findAll().sort("datetime")
                val allGlucose = realm!!.where(Glucose::class.java)
                    .greaterThanOrEqualTo("date", startDate.time)
                    .lessThanOrEqualTo("date", endDate.time).findAll().sort("datetime")
//                Log.e(TAG, "" + allGlucose.size)
                val glucoseArrayList = ArrayList<Glucose>()
                glucoseArrayList.addAll(allGlucose)
                checkLayoutDisplay(glucoseArrayList)

                if (allGlucose.size != 0) {
                    entries = ArrayList<Entry>()
                    for (i in 0..(allGlucose.size - 1)) {
                        entries.add(Entry(i.toFloat(), allGlucose[i]!!.userGlucoValue!!.toFloat()))
                    }
                    val lineDataSet = LineDataSet(entries, "혈당 수치")
                    with(lineDataSet) {
                        mode = LineDataSet.Mode.CUBIC_BEZIER
                        cubicIntensity = 0.15f

                        color = ContextCompat.getColor(activity!!.applicationContext, R.color.colorAccent)
                        setCircleColor(ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimary))

                        circleRadius = 6.0f
                        circleHoleRadius = 3.0f
                        lineWidth = 3.0f
                    }

                    val lineData = LineData(lineDataSet)

                    with(line_chart) {
                        data = lineData
                        animateX(200)
                        invalidate()
                    }
                }


            }
            1 -> {

                val startDate = Calendar.getInstance()
                startDate.add(Calendar.DAY_OF_MONTH, -7)
                val endDate = Calendar.getInstance()  /* ends after 1 month from now */

//        val allGlucose = realm!!.where(Glucose::class.java).equalTo("rawDate", todayString).findAll().sort("datetime")
                val allGlucose = realm!!.where(Glucose::class.java)
                    .greaterThanOrEqualTo("date", startDate.time)
                    .lessThanOrEqualTo("date", endDate.time).findAll().sort("datetime")
//                Log.e(TAG, "" + allGlucose.size)

                val glucoseArrayList = ArrayList<Glucose>()
                glucoseArrayList.addAll(allGlucose)
                checkLayoutDisplay(glucoseArrayList)

                if (allGlucose.size != 0){
                    entries = ArrayList<Entry>()
                    for (i in 0..(allGlucose.size - 1)) {
                        entries.add(Entry(i.toFloat(), allGlucose[i]!!.userGlucoValue!!.toFloat()))
                    }
                    val lineDataSet = LineDataSet(entries, "혈당 수치")

                    with(lineDataSet) {
                        mode = LineDataSet.Mode.CUBIC_BEZIER
                        cubicIntensity = 0.15f

                        color = ContextCompat.getColor(activity!!.applicationContext, R.color.colorAccent)
                        setCircleColor(ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimary))

                        circleRadius = 6.0f
                        circleHoleRadius = 3.0f
                        lineWidth = 3.0f
                    }

                    val lineData = LineData(lineDataSet)

                    with(line_chart) {
                        data = lineData
                        animateX(1000)
                        invalidate()
                    }
                }

            }

            2 -> {

                val startDate = Calendar.getInstance()
                startDate.add(Calendar.DAY_OF_MONTH, -30)
                val endDate = Calendar.getInstance()  /* ends after 1 month from now */

//        val allGlucose = realm!!.where(Glucose::class.java).equalTo("rawDate", todayString).findAll().sort("datetime")
                val allGlucose = realm!!.where(Glucose::class.java)
                    .greaterThanOrEqualTo("date", startDate.time)
                    .lessThanOrEqualTo("date", endDate.time).findAll().sort("datetime")

                val glucoseArrayList = ArrayList<Glucose>()
                glucoseArrayList.addAll(allGlucose)
                checkLayoutDisplay(glucoseArrayList)

                if (allGlucose.size != 0){
                    entries = ArrayList<Entry>()
                    for (i in 0..(allGlucose.size - 1)) {
                        entries.add(Entry(i.toFloat(), allGlucose[i]!!.userGlucoValue!!.toFloat()))
                    }
                    val lineDataSet = LineDataSet(entries, "혈당 수치")

                    with(lineDataSet) {
                        mode = LineDataSet.Mode.CUBIC_BEZIER
                        cubicIntensity = 0.15f

                        color = ContextCompat.getColor(activity!!.applicationContext, R.color.colorAccent)
                        setCircleColor(ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimary))

                        circleRadius = 6.0f
                        circleHoleRadius = 3.0f
                        lineWidth = 3.0f
                    }

                    val lineData = LineData(lineDataSet)

                    with(line_chart) {
                        data = lineData
                        animateX(1000)
                        invalidate()
                    }
                }

            }
        }


    }


}



