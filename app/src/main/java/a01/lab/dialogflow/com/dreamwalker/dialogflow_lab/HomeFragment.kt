package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab

import android.view.View
import devs.mulham.horizontalcalendar.HorizontalCalendar
import ru.semper_viventem.backdropview.ui.common.Screen
import java.util.*

class HomeFragment : Screen() {
    override val layoutId: Int = R.layout.screen_home

    override fun onInitView(view: View) {
        val startDate = Calendar.getInstance()
        startDate.add(Calendar.MONTH, -1)

        /* ends after 1 month from now */
        val endDate = Calendar.getInstance()
        endDate.add(Calendar.MONTH, 1)
        val horizontalCalendar = HorizontalCalendar.Builder(view, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .build()


    }

}