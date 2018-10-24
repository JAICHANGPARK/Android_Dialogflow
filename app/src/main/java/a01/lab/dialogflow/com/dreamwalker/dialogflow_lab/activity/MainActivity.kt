package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.activity

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.R
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
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import java.util.*
import java.util.logging.Logger


class MainActivity : AppCompatActivity(), AIListener {

    companion object {
        private const val ACCESS_TOKEN = "e161b0496a0e4816ba08215c61ea0845"
    }


    private var agent: ChatUser? = null
    private var human: ChatUser? = null

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

        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }

        human = ChatUser(
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
//            "Authorization" to "Bearer e161b0496a0e4816ba08215c61ea0845"
//        )
//
//        FuelManager.instance.basePath = "https://api.dialogflow.com/v1/query?v=20150910"
//
//
//        FuelManager.instance.baseParams = listOf( // latest protocol
//            "sessionId" to UUID.randomUUID(),   // random ID
//            "lang" to "ko"                      // English language
//        )
//

//

        val sessions = UUID.randomUUID()

        my_chat_view.setOnClickSendButtonListener(View.OnClickListener {


            //More code here
            val msg = my_chat_view.inputText


            if (msg.isNotEmpty()) {
                Logger.getLogger(MainActivity::class.java.name).warning(my_chat_view.inputText)

                my_chat_view.send(
                    Message.Builder()
                        .setRight(true)
                        .setUser(this!!.human!!)
                        .hideIcon(true)
                        .setText(msg)
                        .build()
                )

                Fuel.get(
                    "https://api.dialogflow.com/v1/query?",
                    listOf(
                        "v" to "20150910",
                        "sessionId" to sessions,   // random ID 세션 번호가 계속 바뀌니 연속적 대화가 불가능한건가?
                        "lang" to "ko",   // English language
                        "query" to msg
                    )
                ).header(
                    "Authorization" to "Bearer e161b0496a0e4816ba08215c61ea0845"
                )
                    .responseJson { _, _, result ->

                        val reply = result.get().obj()
                            .getJSONObject("result")
                            .getJSONObject("fulfillment")
                            .getString("speech")

                        Logger.getLogger(MainActivity::class.java.name).warning(reply)

                        my_chat_view.send(
                            Message.Builder()
                                .hideIcon(true)
                                .setUser(agent!!)
                                .setText(reply)
                                .build()
                        )
                    }.timeout(5000)

                my_chat_view.inputText = ""


            } else {
                toast("전송할 메세지를 입력하세요")
            }


        }

        )
        my_chat_view.setOnClickMicButtonListener(View.OnClickListener {
            aiService.startListening()
        })

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
                    .setRight(true)
                    .setUser(this!!.human!!)
                    .hideIcon(true)
                    .setText(tmpResult.resolvedQuery)
                    .build()
            )

            my_chat_view.send(
                Message.Builder()
                    .setUser(this!!.agent!!)
                    .hideIcon(true)
                    .setText(tmpResult.fulfillment.speech)
                    .build()
            )
        }
    }

    override fun onListeningStarted() {
        Logger.getLogger(MainActivity::class.java.name).warning("onListeningStarted")
    }

    override fun onAudioLevel(level: Float) {

    }

    override fun onError(error: AIError?) {
        Logger.getLogger(MainActivity::class.java.name).warning(error.toString())
    }

    override fun onListeningCanceled() {
        Logger.getLogger(MainActivity::class.java.name).warning("onListeningCanceled")
    }

    override fun onListeningFinished() {

        Logger.getLogger(MainActivity::class.java.name).warning("onListeningFinished")
    }
}
