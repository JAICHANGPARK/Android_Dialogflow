package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.activity

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.R
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_setting.*
import org.jetbrains.anko.toast

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        home.setOnClickListener {
            finish()
        }

        db_management_button.setOnClickListener {
            toast("준비 중...")
        }

        about_app_button.setOnClickListener {  }
        licenses_button.setOnClickListener {  }
        developer_button.setOnClickListener {  }
        feedback_button.setOnClickListener {  }

    }
}
