package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.model.Glucose
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.realm.Realm
import io.realm.RealmConfiguration
import java.util.logging.Logger

class HomeActivity : AppCompatActivity() {

    var realm:Realm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val realmConfig = RealmConfiguration.Builder().name("glucose.realm").build()
        realm = Realm.getInstance(realmConfig)

        val allGlucose = realm!!.where(Glucose::class.java).findAll()

        allGlucose.forEach {
            glucose: Glucose? ->
            Logger.getLogger(this::class.java.name).warning(glucose?.userGlucoValue.toString())
        }


    }
}
