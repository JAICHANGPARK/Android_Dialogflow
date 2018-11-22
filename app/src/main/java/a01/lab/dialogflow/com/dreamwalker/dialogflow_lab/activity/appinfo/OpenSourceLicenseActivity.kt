package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.activity.appinfo

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_open_source_license.*

class OpenSourceLicenseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_source_license)
        title = "오픈 소스 라이센스"
        markdown_view.loadMarkdownFromAssets("README.md") //Loads the markdown file from the assets folder
    }
}
