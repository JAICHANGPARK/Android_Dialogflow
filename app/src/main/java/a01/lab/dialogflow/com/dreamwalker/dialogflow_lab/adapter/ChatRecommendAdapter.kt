package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.adapter

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.R
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class ChatRecommendAdapter(val recommendList: ArrayList<String>, val context: Context) : RecyclerView.Adapter<ChatRecommendAdapter.ChatRecommendViewHolder>() {

    private var itemClickLitenerr: ItemClickLitsner? = null

    fun setItemClickListener(itemClickListener: ItemClickLitsner) {
        this.itemClickLitenerr = itemClickListener
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ChatRecommendViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.item_chat_layout, p0, false)
        return ChatRecommendViewHolder(view)
    }

    override fun getItemCount(): Int {
       return  recommendList.size
    }

    override fun onBindViewHolder(p0: ChatRecommendViewHolder, p1: Int) {
        with(p0){
            recommandTextView.text = recommendList[p1]
        }
    }

    inner class ChatRecommendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var recommandTextView: TextView

        init {
            recommandTextView = itemView.findViewById(R.id.text_view) as TextView
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (itemClickLitenerr != null) {
                if (v != null) {
                    itemClickLitenerr!!.onItemClicked(v, adapterPosition)
                }

            }
        }

    }
}