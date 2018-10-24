package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.activity.appinfo

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.R
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_about_app.*
import org.jetbrains.anko.toast

class AboutAppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_app)
        setSupportActionBar(toolbar)

        var version: String? = null

        // TODO: 2018-07-25 폰트 생성용 - 박제창
        val font = Typeface.createFromAsset(assets, "fonts/NotoSansCJKkr-Thin.otf")
        textView5.setTypeface(font, Typeface.NORMAL)

        try {
            val i = packageManager.getPackageInfo(packageName, 0)
            version = i.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        val v = "버전 정보 : " + version!!
        textView5.text = v

        home.setOnClickListener {
            finish()
        }

        imageView.setOnClickListener {

            toast("모리모리당뇨모리 클릭한 당신은 호기심 많은 당뇨인!")

        }
    }
}
