package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.model.Message
import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.model.User
import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import lab.dialogflow.com.dreamwalker.chatkit.messages.MessageInput
import lab.dialogflow.com.dreamwalker.chatkit.messages.MessagesList
import lab.dialogflow.com.dreamwalker.chatkit.messages.MessagesListAdapter
import org.jetbrains.anko.toast
import java.util.*
import java.util.logging.Logger

class MainActivityV2 : AppCompatActivity(), MessageInput.InputListener, MessageInput.TypingListener,
        MessageInput.AttachmentsListener, MessagesListAdapter.SelectionListener, MessagesListAdapter.OnLoadMoreListener,
        TextToSpeech.OnInitListener {

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

    private var messagesList: MessagesList? = null
    val senderId = "0"
    var messagesAdapter: MessagesListAdapter<Message>? = null
    val sessions = UUID.randomUUID()
    var textToSpeech: TextToSpeech? = null
    var mAudioManager: AudioManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_v2)

        messagesList = findViewById<View>(R.id.messagesList) as MessagesList
        initAdapter()

        val input = findViewById<View>(R.id.input_msg) as MessageInput
        input.setInputListener(this)
        input.setTypingListener(this)
        input.setAttachmentsListener(this)

        textToSpeech = TextToSpeech(this, this)
        mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
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
                "Authorization" to "Bearer e161b0496a0e4816ba08215c61ea0845"
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

    override fun onDestroy() {
        if (textToSpeech != null) {
            textToSpeech!!.stop()
            textToSpeech!!.shutdown()
        }
        super.onDestroy()
    }

}
