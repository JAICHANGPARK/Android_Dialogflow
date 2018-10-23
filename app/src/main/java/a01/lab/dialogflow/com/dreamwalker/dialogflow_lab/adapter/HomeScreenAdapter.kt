package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.adapter

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.R
import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.model.Glucose
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import java.text.DateFormat


class HomeScreenAdapter(list: ArrayList<Glucose>, context: Context) : RecyclerView.Adapter<HomeScreenAdapter.HomeScreenViewHolder>() {

    val glucoList = list
    val context = context
    private val mColorGenerator = ColorGenerator.DEFAULT
    private var mDrawableBuilder: TextDrawable? = null

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): HomeScreenViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.item_glucose_layout, p0, false)
        return HomeScreenViewHolder(view)
    }

    override fun onBindViewHolder(p0: HomeScreenViewHolder, p1: Int) {
        val userType = glucoList[p1].userType
        with(p0){
            setReminderTitle(userType)
            imageView.setImageDrawable(mDrawableBuilder)
            userTypeTextView.text = userType
            userTypeTimeTextView.text = glucoList[p1].userTypeTime
            userValueTextView.text = glucoList[p1].userGlucoValue.toString() + " mg/dL"
            userTimeTextView.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(glucoList[p1].date)
        }

    }


    override fun getItemCount(): Int {
        return glucoList.size
    }

    // Set reminder title view
    fun setReminderTitle(title: String?) {
        var letter = "G"
        if (title != null && !title.isEmpty()) {
            letter = title.substring(0, 1)
        }
        val color = mColorGenerator.randomColor

        // Create a circular icon consisting of  a random background colour and first letter of title
        mDrawableBuilder = TextDrawable.builder().buildRound(letter, color)

    }


    class HomeScreenViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById(R.id.imageView) as ImageView
        val userTypeTimeTextView = itemView.findViewById(R.id.text_type_time) as TextView
        val userTypeTextView = itemView.findViewById(R.id.text_type) as TextView
        val userTimeTextView = itemView.findViewById(R.id.user_time) as TextView
        val userValueTextView = itemView.findViewById(R.id.user_value) as TextView

    }

}