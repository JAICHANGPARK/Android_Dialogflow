package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.fragment

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.R
import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.model.Glucose
import android.view.View
import io.realm.Realm
import io.realm.RealmConfiguration
import ru.semper_viventem.backdropview.ui.common.Screen

class AnalysisFragment : Screen() {


    override val layoutId: Int = R.layout.screen_analysis
    var realm: Realm? = null
    override fun onInitView(view: View) {
        Realm.init(activity!!.applicationContext)
        val realmConfig = RealmConfiguration.Builder().name("glucose.realm").build()
        realm = Realm.getInstance(realmConfig)
        val result = realm?.where(Glucose::class.java)?.findAll()


    }
}