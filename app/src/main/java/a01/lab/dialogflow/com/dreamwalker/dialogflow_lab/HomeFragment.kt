package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.adapter.HomeScreenAdapter
import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.model.Glucose
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.screen_home.*
import ru.semper_viventem.backdropview.ui.common.Screen
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Logger
import kotlin.collections.ArrayList


class HomeFragment : Screen() {
    override val layoutId: Int = R.layout.screen_home


    var homeAdapter: HomeScreenAdapter? = null
    var glucoseArrayList = ArrayList<Glucose>()
    lateinit var simpleDateFormat:SimpleDateFormat

    override fun onInitView(view: View) {
        val defaultSelectedDate = Calendar.getInstance();
        simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
        val todayDate = defaultSelectedDate.time
        val todayString = simpleDateFormat.format(todayDate)
        Realm.init(activity?.applicationContext)
        val realmConfig = RealmConfiguration.Builder().name("glucose.realm").build()
        val realm = Realm.getInstance(realmConfig)
        val allGlucose = realm.where(Glucose::class.java).equalTo("rawDate", todayString).findAll().sort("datetime")

        allGlucose.forEach { glucose: Glucose? ->
            Logger.getLogger(this::class.java.name).warning(glucose?.userGlucoValue.toString())
        }

        glucoseArrayList.addAll(allGlucose)
        homeAdapter = HomeScreenAdapter(glucoseArrayList, activity!!.applicationContext)

        val startDate = Calendar.getInstance()
        startDate.add(Calendar.MONTH, -1)
        val endDate = Calendar.getInstance()  /* ends after 1 month from now */
        endDate.add(Calendar.MONTH, 1)

        val horizontalCalendar = HorizontalCalendar.Builder(view, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .build()

        val listener = object : HorizontalCalendarListener() {
            override fun onDateSelected(date: Calendar?, position: Int) {
                Logger.getLogger(HomeFragment::class.java.name).warning(date.toString())
                val selectDate = date?.time
                val selectDateFormattString = simpleDateFormat.format(selectDate)
                glucoseArrayList.clear()
                val result =realm.where(Glucose::class.java).equalTo("rawDate", selectDateFormattString).findAll().sort("datetime")
                glucoseArrayList.addAll(result)
                homeAdapter!!.notifyDataSetChanged()
            }
        }

        horizontalCalendar.calendarListener = listener

        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, LinearLayoutManager.VERTICAL)
        with(recyclerView) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = homeAdapter
            addItemDecoration(dividerItemDecoration)
        }


    }


}