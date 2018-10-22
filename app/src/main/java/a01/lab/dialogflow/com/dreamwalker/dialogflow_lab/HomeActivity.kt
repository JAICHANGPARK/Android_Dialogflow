package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_home.*
import lab.dialogflow.com.dreamwalker.backdrop.BackdropBehavior

class HomeActivity : AppCompatActivity() {


    private lateinit var backdropBehavior: BackdropBehavior
    private val FRAGMENT_CONTAINER = R.id.foregroundContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        backdropBehavior = foregroundContainer.findBehavior()
        with(backdropBehavior) {
            attachBackContainer(R.id.backContainer)
            attachToolbar(R.id.toolbar)
        }

        with(toolbar) {
            setTitle(R.string.app_name)
        }


        showPage(HomeFragment())

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
