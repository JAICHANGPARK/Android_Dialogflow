package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.activity

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.R
import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.consts.IntentConst
import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_web.*

class WebActivity : AppCompatActivity() {

    internal lateinit var alertDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        alertDialog = SpotsDialog.Builder().setContext(this).build()
        alertDialog.show()

        with(web_view) {
            getSettings().setJavaScriptEnabled(true)
            webChromeClient = WebChromeClient()

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    alertDialog.dismiss()
                }
            }
        }

        if (intent != null) {
            if (!intent.getStringExtra(IntentConst.WEB_URL).isEmpty()) {
                web_view.loadUrl(intent.getStringExtra(IntentConst.WEB_URL))
            }
        }
    }
}
