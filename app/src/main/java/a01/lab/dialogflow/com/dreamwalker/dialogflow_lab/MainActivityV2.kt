package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.model.Message
import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.model.User
import ai.api.AIConfiguration
import ai.api.AIDataService
import ai.api.AIListener
import ai.api.AIServiceException
import ai.api.android.AIService
import ai.api.model.AIError
import ai.api.model.AIRequest
import ai.api.model.AIResponse
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.AsyncTask
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import lab.dialogflow.com.dreamwalker.chatkit.messages.MessageInput
import lab.dialogflow.com.dreamwalker.chatkit.messages.MessagesList
import lab.dialogflow.com.dreamwalker.chatkit.messages.MessagesListAdapter
import net.gotev.speech.Speech
import net.gotev.speech.SpeechDelegate
import net.gotev.speech.SpeechRecognitionNotAvailable
import net.gotev.speech.ui.SpeechProgressView
import org.jetbrains.anko.toast
import java.util.*
import java.util.logging.Logger

class MainActivityV2 : AppCompatActivity(), MessageInput.InputListener, MessageInput.TypingListener,
        MessageInput.AttachmentsListener, MessagesListAdapter.SelectionListener, MessagesListAdapter.OnLoadMoreListener,
        TextToSpeech.OnInitListener, MessageInput.onMicListener, AIListener {


    private var messagesList: MessagesList? = null
    val senderId = "0"
    var messagesAdapter: MessagesListAdapter<Message>? = null
    val sessions = UUID.randomUUID()
    var textToSpeech: TextToSpeech? = null
    var mAudioManager: AudioManager? = null

    var aiService: AIService? = null
    var aiDataService: AIDataService? = null
    var aiRequest: AIRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_v2)

        val config = ai.api.android.AIConfiguration(
                "cefd4057d9184356a0416f37f7f372c8",
                AIConfiguration.SupportedLanguages.Korean,
                ai.api.android.AIConfiguration.RecognitionEngine.System
        )

        aiService = AIService.getService(this, config)
        aiDataService = AIDataService(config)

        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }

        aiService?.setListener(this)

        aiRequest = AIRequest()
//        aiRequest!!.setQuery("Hello")

        messagesList = findViewById<View>(R.id.messagesList) as MessagesList
        initAdapter()

        val input = findViewById<View>(R.id.input_msg) as MessageInput
        input.setInputListener(this)
        input.setTypingListener(this)
        input.setAttachmentsListener(this)
        input.setMicListener(this)

        textToSpeech = TextToSpeech(this, this)
        mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager


        Speech.init(this, getPackageName())

//        backgroundTask().execute(aiRequest)
    }

    inner class backgroundTask : AsyncTask<AIRequest, Void, AIResponse>() {

        override fun doInBackground(vararg params: AIRequest?): AIResponse? {
            val request = params[0]
            try {
                val response = aiDataService!!.request(aiRequest)
                return response
            } catch (e: AIServiceException) {
                toast(e.message.toString())
            }
            return null
        }

        override fun onPostExecute(result: AIResponse?) {
            if (result != null) {
//                toast(result.result.fulfillment.speech)
                messagesAdapter?.addToStart(Message("1", User("1", "agent", "1", true), result.result.fulfillment.speech), true)
            }
            super.onPostExecute(result)
        }


    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 101)
    }

    private fun initAdapter() {
        messagesAdapter = MessagesListAdapter(senderId, null)
        messagesAdapter?.enableSelectionMode(this)
        messagesAdapter?.setLoadMoreListener(this)
        messagesAdapter?.registerViewClickListener(
                R.id.messageUserAvatar
        ) { view, message ->
            //                        AppUtils.showToast(DefaultMessagesActivity.this,
            //                                message.getUser().getName() + " avatar click",
            //                                false);
        }
        this.messagesList!!.setAdapter(messagesAdapter)
    }

    override fun onSubmit(input: CharSequence): Boolean {
        Toast.makeText(this, "" + input, Toast.LENGTH_SHORT).show()
        messagesAdapter?.addToStart(Message("0", User("0", "avater", "0", true), input.toString()), true)
        Fuel.get(
                "https://api.dialogflow.com/v1/query?",
                listOf(
                        "v" to "20150910",
                        "sessionId" to sessions,   // random ID 세션 번호가 계속 바뀌니 연속적 대화가 불가능한건가?
                        "lang" to "ko",   // English language
                        "query" to input
                )
        ).header(
                "Authorization" to "Bearer cefd4057d9184356a0416f37f7f372c8"
        )
                .responseJson { _, _, result ->

                    val reply = result.get().obj()
                            .getJSONObject("result")
                            .getJSONObject("fulfillment")
                            .getString("speech")

                    Logger.getLogger(MainActivity::class.java.name).warning(reply)

//                my_chat_view.send(
//                    com.github.bassaer.chatmessageview.model.Message.Builder()
//                        .hideIcon(true)
//                        .setUser(agent!!)
//                        .setText(reply)
//                        .build())
                    messagesAdapter?.addToStart(Message("1", User("1", "agent", "1", true), reply), true)
                    //todo dialog flow 리턴값 tts 처리
                    speekResponse(reply)
                }

        return true
    }


    private fun speekResponse(reply: String) {

        if (mAudioManager != null) {
            when (mAudioManager!!.ringerMode) {

                AudioManager.RINGER_MODE_NORMAL -> {
                    val utteranceId = this.hashCode().toString() + ""
                    textToSpeech?.speak(reply, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
                }

                AudioManager.RINGER_MODE_VIBRATE -> {
                    Logger.getLogger(this::class.java.name).warning("진동모드")
                }

                AudioManager.RINGER_MODE_SILENT -> {
                    Logger.getLogger(this::class.java.name).warning("무음모드")
                }

            }
        }
    }

    override fun onStartTyping() {
        Toast.makeText(this, "onStartTyping", Toast.LENGTH_SHORT).show()
    }

    override fun onStopTyping() {
        Toast.makeText(this, "onStopTyping", Toast.LENGTH_SHORT).show()
    }

    override fun onAddAttachments() {
        Toast.makeText(this, "onAddAttachments", Toast.LENGTH_SHORT).show()
    }

    override fun onSelectionChanged(count: Int) {

    }

    override fun onLoadMore(page: Int, totalItemsCount: Int) {

    }


    override fun onResult(result: AIResponse?) {

        Logger.getLogger(MainActivity::class.java.name).warning(result.toString())
        val tmpResult = result?.result

        if (tmpResult != null) {
            Logger.getLogger(MainActivity::class.java.name).warning(tmpResult.action + tmpResult.resolvedQuery + tmpResult.fulfillment)
            Logger.getLogger(MainActivity::class.java.name).warning(tmpResult.fulfillment.source + tmpResult.fulfillment.speech)
            messagesAdapter?.addToStart(Message("0", User("0", "avater", "0", true), tmpResult.resolvedQuery), true)
            messagesAdapter?.addToStart(Message("1", User("1", "agent", "1", true), tmpResult.fulfillment.speech), true)
        }

    }

    override fun onListeningStarted() {
    }

    override fun onAudioLevel(level: Float) {
    }

    override fun onError(error: AIError?) {
        if (error != null) {
            toast(error.message)
        }
    }

    override fun onListeningCanceled() {

    }

    override fun onListeningFinished() {

    }


    override fun onVoiceStart(): Boolean {
        toast("onMic Button Clicked")
//        aiService!!.startListening()

        val colors = intArrayOf(ContextCompat.getColor(this, R.color.color1),
                ContextCompat.getColor(this, R.color.color2),
                ContextCompat.getColor(this, R.color.color3),
                ContextCompat.getColor(this, R.color.color4),
                ContextCompat.getColor(this, R.color.color5))

        val heights = intArrayOf(60, 76, 58, 80, 55)


        val builder = AlertDialog.Builder(this)

        val view = layoutInflater.inflate(R.layout.dialog_sst_main, null)
        builder.setTitle("말하세요")
        builder.setView(view)
        val speechProgressView = view.findViewById(R.id.recognition_view) as SpeechProgressView
        val partialTextView = view.findViewById(R.id.partial_text_view) as TextView
        speechProgressView.setColors(colors)
        speechProgressView.setBarMaxHeightsInDp(heights)
        val dialog = builder.create()
        val delegate = object : SpeechDelegate{
            override fun onStartOfSpeech() {
                toast("이야기해주세요")
            }

            override fun onSpeechPartialResults(results: MutableList<String>?) {
                val str = StringBuilder()
                if (results != null) {
                    for (res in results) {
                        str.append(res).append(" ")
                    }
                }
                Log.i("speech", "partial result: " + str.toString().trim { it <= ' ' })

                runOnUiThread {
                    partialTextView.text = str.toString()
                }

            }

            override fun onSpeechRmsChanged(value: Float) {

            }

            override fun onSpeechResult(result: String?) {
                toast("Result -->" + result)
                Log.i("speech", "result: " + result)

                messagesAdapter?.addToStart(Message("0", User("0", "avater", "0", true), result), true)


//                aiRequest!!.sessionId = senderId
//                aiRequest?.language = "ko"
//                aiRequest!!.setQuery(result)
//
//                backgroundTask().execute(aiRequest)

                Fuel.get(
                        "https://api.dialogflow.com/v1/query?",
                        listOf(
                                "v" to "20150910",
                                "sessionId" to sessions,   // random ID 세션 번호가 계속 바뀌니 연속적 대화가 불가능한건가?
                                "lang" to "ko",   // English language
                                "query" to result
                        )
                ).header(
                        "Authorization" to "Bearer cefd4057d9184356a0416f37f7f372c8"
                )
                        .responseJson { _, _, result ->

                            val reply = result.get().obj()
                                    .getJSONObject("result")
                                    .getJSONObject("fulfillment")
                                    .getString("speech")

                            Logger.getLogger(MainActivity::class.java.name).warning(reply)

                            messagesAdapter?.addToStart(Message("1", User("1", "agent", "1", true), reply), true)
                            //todo dialog flow 리턴값 tts 처리
                            speekResponse(reply)
                        }
                dialog.dismiss()
            }

        }
        try {
            Speech.getInstance().startListening(speechProgressView,delegate)
        } catch (e: SpeechRecognitionNotAvailable) {

        }
//        builder.show()
        dialog.show()


        return true
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.language = Locale.KOREA
            val language = textToSpeech?.setLanguage(Locale.KOREAN)

            if (language == TextToSpeech.LANG_MISSING_DATA || language == TextToSpeech.LANG_NOT_SUPPORTED) {
                toast("지원하지 않는 언어입니다.")
            }
        } else {
            toast("TTS 실패!")
        }
    }

    override fun onDestroy() {
        if (textToSpeech != null) {
            textToSpeech!!.stop()
            textToSpeech!!.shutdown()
        }
        Speech.getInstance().shutdown();
        super.onDestroy()
    }


}
