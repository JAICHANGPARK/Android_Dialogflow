package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.activity.appinfo

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_open_source_license.*

class AboutProjectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_project)
        markdown_view.loadMarkdownFromAssets("ABOUTAPP.md") //Loads the markdown file from the assets folder
    }
}
