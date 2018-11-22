package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.activity.appinfo

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.R
import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.activity.WebActivity
import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.consts.IntentConst
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_developer.*

class DeveloperActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer)

        setSupportActionBar(toolbar)

        GithubButton.setOnClickListener {
            val intent = Intent(this, WebActivity::class.java)
            intent.putExtra(IntentConst.WEB_URL, "https://github.com/JAICHANGPARK")
            startActivity(intent)
        }
        qiitaButton.setOnClickListener {

            val intent = Intent(this, WebActivity::class.java)
            intent.putExtra(IntentConst.WEB_URL, "https://qiita.com/Dreamwalker")
            startActivity(intent)
        }
    }
}
