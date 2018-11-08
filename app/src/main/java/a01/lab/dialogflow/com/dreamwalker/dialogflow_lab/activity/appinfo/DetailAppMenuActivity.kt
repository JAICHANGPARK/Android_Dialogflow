package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.activity.appinfo

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.R
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import de.cketti.library.changelog.ChangeLog
import kotlinx.android.synthetic.main.activity_detail_app_menu.*

class DetailAppMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_app_menu)

        buttonInformation0.setOnClickListener {
            startActivity(Intent(this, AboutProjectActivity::class.java))
        }

        buttonInformation1.setOnClickListener {

            startActivity( Intent(this, AboutAppActivity::class.java))

        }

        buttonInformation2.setOnClickListener {
            val cl = ChangeLog(this)
            cl.logDialog.show()
        }

        home.setOnClickListener {
            finish()
        }

    }
}
