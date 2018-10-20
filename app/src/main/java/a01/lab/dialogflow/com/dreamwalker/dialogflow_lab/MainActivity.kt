package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab

import ai.api.AIConfiguration.SupportedLanguages
import ai.api.AIListener
import ai.api.android.AIConfiguration
import ai.api.android.AIConfiguration.RecognitionEngine
import ai.api.android.AIService
import ai.api.model.AIError
import ai.api.model.AIResponse
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.github.bassaer.chatmessageview.model.ChatUser
import com.github.bassaer.chatmessageview.model.Message
import kotlinx.android.synthetic.main.activity_main.*
import java.util.logging.Logger


class MainActivity : AppCompatActivity(), AIListener {

    companion object {
        private const val ACCESS_TOKEN = "0b3c6e3b904648b4972cf6979e2e186f"
    }


    private var agent: ChatUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val config = AIConfiguration(
            "e161b0496a0e4816ba08215c61ea0845",
            SupportedLanguages.Korean,
            RecognitionEngine.System
        )

        val aiService = AIService.getService(this, config)
        aiService.setListener(this)

        val permission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.RECORD_AUDIO
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {

            makeRequest()
        }

        val human = ChatUser(
            2,
            "You",
            BitmapFactory.decodeResource(
                resources,
                R.drawable.ic_account_circle
            )
        )

        agent = ChatUser(
            1,
            "Agent",
            BitmapFactory.decodeResource(
                resources,
                R.drawable.ic_account_circle
            )
        )

//
//        FuelManager.instance.baseHeaders = mapOf(
//            "Authorization" to "Bearer $ACCESS_TOKEN"
//        )
//
//        FuelManager.instance.basePath = "https://api.dialogflow.com/v1/"
//        FuelManager.instance.baseParams = listOf(
//            "v" to "20150910",                  // latest protocol
//            "sessionId" to UUID.randomUUID(),   // random ID
//            "lang" to "ko"                      // English language
//        )
//

//
        my_chat_view.setOnClickSendButtonListener(View.OnClickListener {
            my_chat_view.send(
                Message.Builder()
                    .setRight(true)
                    .setUser(human)
                    .setText(my_chat_view.inputText)
                    .build()
            )

            aiService.startListening()


            // More code here

//                Fuel.get("/query",
//                    listOf("query" to my_chat_view.inputText))
//                    .responseJson { _, _, result ->
//                        val reply = result.get().obj()
//                            .getJSONObject("result")
//                            .getJSONObject("fulfillment")
//                            .getString("speech")
//                        Logger.getLogger(MainActivity::class.java.name).warning(reply)

            // More code here
//
//                        my_chat_view.send(Message.Builder()
//                            .setRight(true)
//                            .setUser(agent)
//                            .setText(reply)
//                            .build()
//                        )
//                    }
        }
        )

    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            101
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            101 -> {

                if (grantResults.size === 0 || grantResults[0] !== PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return
            }
        }
    }

    override fun onResult(result: AIResponse?) {
        Logger.getLogger(MainActivity::class.java.name).warning(result.toString())
        val tmpResult = result?.result
        if (tmpResult != null) {
            Logger.getLogger(MainActivity::class.java.name)
                .warning(tmpResult.action + tmpResult.resolvedQuery + tmpResult.fulfillment)
            Logger.getLogger(MainActivity::class.java.name)
                .warning(tmpResult.fulfillment.source + tmpResult.fulfillment.speech)

            my_chat_view.send(
                Message.Builder()
                    .setUser(this!!.agent!!)
                    .setText(tmpResult.fulfillment.speech)
                    .build()
            )
        }
    }

    override fun onListeningStarted() {

    }

    override fun onAudioLevel(level: Float) {

    }

    override fun onError(error: AIError?) {
        Logger.getLogger(MainActivity::class.java.name).warning(error.toString())
    }

    override fun onListeningCanceled() {

    }

    override fun onListeningFinished() {

    }
}
