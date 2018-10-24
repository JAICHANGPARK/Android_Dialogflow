package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.fragment

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.R
import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.adapter.HomeScreenAdapter
import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.adapter.ItemClickLitsner
import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.model.Glucose
import android.content.DialogInterface
import android.os.Build
import android.support.v4.content.ContextCompat.getColor
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.model.CalendarEvent
import devs.mulham.horizontalcalendar.utils.CalendarEventsPredicate
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.screen_home.*
import org.jetbrains.anko.toast
import ru.semper_viventem.backdropview.ui.common.Screen
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Logger
import kotlin.collections.ArrayList


class HomeFragment : Screen(), ItemClickLitsner {


    override val layoutId: Int = R.layout.screen_home

    var homeAdapter: HomeScreenAdapter? = null
    var glucoseArrayList = ArrayList<Glucose>()
    lateinit var simpleDateFormat: SimpleDateFormat
    var realm: Realm? = null

    override fun onInitView(view: View) {
        val defaultSelectedDate = Calendar.getInstance();
        simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
        val todayDate = defaultSelectedDate.time
        val todayString = simpleDateFormat.format(todayDate)
        Realm.init(activity?.applicationContext)
        val realmConfig = RealmConfiguration.Builder().name("glucose.realm").build()
        realm = Realm.getInstance(realmConfig)
        val allGlucose = realm!!.where(Glucose::class.java).equalTo("rawDate", todayString).findAll().sort("datetime")

        allGlucose.forEach { glucose: Glucose? ->
            Logger.getLogger(this::class.java.name).warning(glucose?.userGlucoValue.toString())
        }

        glucoseArrayList.addAll(allGlucose)
        homeAdapter = HomeScreenAdapter(glucoseArrayList, activity!!.applicationContext)

        checkLayoutDisplay(glucoseArrayList)

        val startDate = Calendar.getInstance()
        startDate.add(Calendar.MONTH, -1)
        val endDate = Calendar.getInstance()  /* ends after 1 month from now */
        endDate.add(Calendar.MONTH, 1)

        val predict = CalendarEventsPredicate { date ->
            val events = ArrayList<CalendarEvent>()
            val eventDate = simpleDateFormat.format(date!!.time)
            val tmp = realm!!.where(Glucose::class.java).equalTo("rawDate", eventDate).findAll()

            for (i in tmp.indices) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    events.add(CalendarEvent(getColor(activity!!.applicationContext,
                        R.color.colorAccent
                    ), "count"))
                } else {
                    events.add(CalendarEvent(R.color.colorAccent, "count"))
                }
            }
            events
        }

        val horizontalCalendar = HorizontalCalendar.Builder(view,
            R.id.calendarView
        )
            .range(startDate, endDate)
            .datesNumberOnScreen(5)
            .addEvents(predict)
            .build()

        val listener = object : HorizontalCalendarListener() {
            override fun onDateSelected(date: Calendar?, position: Int) {
                Logger.getLogger(HomeFragment::class.java.name).warning(date.toString())
                val selectDate = date?.time
                val selectDateFormattString = simpleDateFormat.format(selectDate)
                glucoseArrayList.clear()
                val result = realm!!.where(Glucose::class.java).equalTo("rawDate", selectDateFormattString).findAll()
                    .sort("datetime")
                glucoseArrayList.addAll(result)

                checkLayoutDisplay(glucoseArrayList)


                homeAdapter!!.notifyDataSetChanged()
            }
        }

//        homeAdapter!!.itemClickListener = this
        homeAdapter!!.setItemClickListener(this)


        horizontalCalendar.calendarListener = listener

        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, LinearLayoutManager.VERTICAL)
        with(recyclerView) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = homeAdapter
            addItemDecoration(dividerItemDecoration)
        }

    }

    fun checkLayoutDisplay(arrayList: ArrayList<Glucose>) {

        if (arrayList.size == 0) {
            empty_layout.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            empty_layout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }

    }

    override fun onItemClicked(v: View, position: Int) {

        activity!!.toast(position.toString())
    }

    override fun onItemLongCllicked(v: View, position: Int) {
        val timeStamps = glucoseArrayList[position].datetime

        val builder =android.app.AlertDialog.Builder(activity)
        builder.setTitle("경고")
        builder.setMessage("삭제하시겠어요?")
        val listener = DialogInterface.OnClickListener { dialog, _ ->
            val results = realm!!.where(Glucose::class.java).equalTo("datetime", timeStamps).findAll()
            realm!!.executeTransaction{
                results.deleteAllFromRealm()
            }
            glucoseArrayList.removeAt(position)
            homeAdapter!!.notifyDataSetChanged()
            dialog.dismiss()
        }
        builder.setPositiveButton(android.R.string.yes, listener)
        builder.setNegativeButton(android.R.string.no) { dialog, _ -> dialog.dismiss() }
        builder.show()
    }
}