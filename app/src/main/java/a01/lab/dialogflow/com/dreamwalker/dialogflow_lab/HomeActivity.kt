package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.model.Glucose
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.activity_home.*
import lab.dialogflow.com.dreamwalker.backdrop.BackdropBehavior
import java.util.logging.Logger

class HomeActivity : AppCompatActivity() {

    var realm: Realm? = null
    private lateinit var backdropBehavior: BackdropBehavior
    private val FRAGMENT_CONTAINER = R.id.foregroundContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        showPage(HomeFragment())

        backdropBehavior = foregroundContainer.findBehavior()

        with(backdropBehavior) {
            attachBackContainer(R.id.backContainer)
            attachToolbar(R.id.toolbar)
        }
        with(toolbar) {
            setTitle(R.string.app_name)
        }

        Realm.init(this)

        val realmConfig = RealmConfiguration.Builder().name("glucose.realm").build()
        realm = Realm.getInstance(realmConfig)

        val allGlucose = realm!!.where(Glucose::class.java).findAll()

        allGlucose.forEach { glucose: Glucose? ->
            Logger.getLogger(this::class.java.name).warning(glucose?.userGlucoValue.toString())
        }
    }


    override fun onBackPressed() {
        if (!backdropBehavior.close()) {
            finish()
        }
    }

    private fun showPage(page: Fragment) {
        supportFragmentManager
                .beginTransaction()
                .replace(FRAGMENT_CONTAINER, page)
                .commit()
    }



}
