package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.model.Glucose
import android.view.View
import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener
import io.realm.Realm
import io.realm.RealmConfiguration
import ru.semper_viventem.backdropview.ui.common.Screen
import java.util.*
import java.util.logging.Logger

class HomeFragment : Screen() {
    override val layoutId: Int = R.layout.screen_home


    override fun onInitView(view: View) {

        Realm.init(activity?.applicationContext)
        val realmConfig = RealmConfiguration.Builder().name("glucose.realm").build()
        val realm = Realm.getInstance(realmConfig)
        val allGlucose = realm.where(Glucose::class.java).findAll()

        allGlucose.forEach { glucose: Glucose? ->
            Logger.getLogger(this::class.java.name).warning(glucose?.userGlucoValue.toString())
        }

        val startDate = Calendar.getInstance()
        startDate.add(Calendar.MONTH, -1)

        /* ends after 1 month from now */
        val endDate = Calendar.getInstance()
        endDate.add(Calendar.MONTH, 1)
        val horizontalCalendar = HorizontalCalendar.Builder(view, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .build()

        val listener = object  : HorizontalCalendarListener() {
            override fun onDateSelected(date: Calendar?, position: Int) {
                Logger.getLogger(HomeFragment::class.java.name).warning(date.toString())
            }
        }

        horizontalCalendar.calendarListener = listener


    }



}