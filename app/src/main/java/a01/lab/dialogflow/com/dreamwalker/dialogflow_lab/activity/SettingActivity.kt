package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.activity

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.R
import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.activity.appinfo.DetailAppMenuActivity
import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.activity.appinfo.DeveloperActivity
import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.activity.appinfo.FeedbackActivity
import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.activity.appinfo.OpenSourceLicenseActivity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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

        accessory_button.setOnClickListener {
            toast("준비 중...")
        }

        about_app_button.setOnClickListener {
            startActivity(Intent(this, DetailAppMenuActivity::class.java))
        }
        licenses_button.setOnClickListener {
            startActivity(Intent(this, OpenSourceLicenseActivity::class.java))
        }
        developer_button.setOnClickListener {
            startActivity(Intent(this, DeveloperActivity::class.java))
        }
        feedback_button.setOnClickListener {
            startActivity(Intent(this, FeedbackActivity::class.java))
        }

        device_info_icon.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("액세서리 관리하기")
            builder.setMessage("액세서리 관리는 자가혈당기기 추가 와 장치 관리를 할 수 있습니다.")
            builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }

            builder.show()
        }

    }
}
