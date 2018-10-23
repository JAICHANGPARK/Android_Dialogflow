package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.adapter.ChatRecommendAdapter
import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.adapter.ItemClickLitsner
import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.model.Glucose
import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.model.Message
import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.model.User
import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.util.RecommendChat
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
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.AsyncTask
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import io.paperdb.Paper
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.activity_main_v2.*
import lab.dialogflow.com.dreamwalker.chatkit.messages.MessageInput
import lab.dialogflow.com.dreamwalker.chatkit.messages.MessagesList
import lab.dialogflow.com.dreamwalker.chatkit.messages.MessagesListAdapter
import net.gotev.speech.Speech
import net.gotev.speech.SpeechDelegate
import net.gotev.speech.SpeechRecognitionNotAvailable
import net.gotev.speech.ui.SpeechProgressView
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Logger
import kotlin.collections.ArrayList

class MainActivityV2 : AppCompatActivity(), MessageInput.InputListener, MessageInput.TypingListener,
        MessageInput.AttachmentsListener, MessagesListAdapter.SelectionListener, MessagesListAdapter.OnLoadMoreListener,
        TextToSpeech.OnInitListener, MessageInput.onMicListener, AIListener, ItemClickLitsner {


    private var messagesList: MessagesList? = null
    val senderId = "0"
    var messagesAdapter: MessagesListAdapter<Message>? = null
    val sessions = UUID.randomUUID()
    var textToSpeech: TextToSpeech? = null
    var mAudioManager: AudioManager? = null

    var aiService: AIService? = null
    var aiDataService: AIDataService? = null
    var aiRequest: AIRequest? = null
    val keys = "aad99a0d97f64a0bbe5b7328ec6a1d22"
    var realm: Realm? = null

    var userExp: Int = 0

    var recommandList: ArrayList<String>? = null
    var recommendAdapter: ChatRecommendAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_v2)
        Realm.init(this)

        title = "당뇨모리..AI 상담원과 대화"

        val realmConfig = RealmConfiguration.Builder().name("glucose.realm").build()
        realm = Realm.getInstance(realmConfig)

        val config = ai.api.android.AIConfiguration(
                keys,
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

        Speech.init(this, packageName)

        userExp = Paper.book("user").read<Int>("exp")

        initRecommendList()
        recommendAdapter = ChatRecommendAdapter(recommandList!!, this)
        recommendAdapter!!.setItemClickListener(this)

        with(recycler_view) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
            adapter = recommendAdapter
        }
//        backgroundTask().execute(aiRequest)
    }

    override fun onItemClicked(v: View, position: Int) {
//        toast(recommandList!![position])

        messagesAdapter?.addToStart(Message("0", User("0", "avater", "0", true), recommandList!![position]), true)
        processResponseFromDialogFlow(recommandList!![position])
    }

    fun initRecommendList() {
        recommandList = ArrayList<String>()
        recommandList!!.add("안녕하세요")
        recommandList!!.add("안녕")
        recommandList!!.add("반가워")
        recommandList!!.add("혈당 기록하기")
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

        messagesAdapter?.addToStart(Message("1", User("1", "agent", "1", true), "안녕하세요 :)"), true)
    }

    override fun onSubmit(input: CharSequence): Boolean {

        val result = input.toString()

        if (result.isNotEmpty()) {
            messagesAdapter?.addToStart(Message("0", User("0", "avater", "0", true), result), true)

            processResponseFromDialogFlow(result)


        } else {
            toast("공백은 전송할 수 없습니다.")
        }


//        Toast.makeText(this, "" + input, Toast.LENGTH_SHORT).show()
//        messagesAdapter?.addToStart(Message("0", User("0", "avater", "0", true), input.toString()), true)
//        Fuel.get(
//                "https://api.dialogflow.com/v1/query?",
//                listOf(
//                        "v" to "20150910",
//                        "sessionId" to sessions,   // random ID 세션 번호가 계속 바뀌니 연속적 대화가 불가능한건가?
//                        "lang" to "ko",   // English language
//                        "query" to input
//                )
//        ).header(
//                "Authorization" to "Bearer $keys"
//        )
//                .responseJson { _, _, result ->
//
//                    val reply = result.get().obj()
//                            .getJSONObject("result")
//                            .getJSONObject("fulfillment")
//                            .getString("speech")
//
//                    Logger.getLogger(MainActivity::class.java.name).warning(reply)
//
////                my_chat_view.send(
////                    com.github.bassaer.chatmessageview.model.Message.Builder()
////                        .hideIcon(true)
////                        .setUser(agent!!)
////                        .setText(reply)
////                        .build())
//                    messagesAdapter?.addToStart(Message("1", User("1", "agent", "1", true), reply), true)
//                    //todo dialog flow 리턴값 tts 처리
//                    speekResponse(reply)
//                }

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

        val colors = intArrayOf(
                ContextCompat.getColor(this, R.color.color1),
                ContextCompat.getColor(this, R.color.color2),
                ContextCompat.getColor(this, R.color.color3),
                ContextCompat.getColor(this, R.color.color4),
                ContextCompat.getColor(this, R.color.color5)
        )

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

        val delegate = object : SpeechDelegate {
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
//                toast("Result -->" + result)
//                Log.i("speech", "result: " + result)
                if (result != null && result.isNotEmpty()) {
                    messagesAdapter?.addToStart(Message("0", User("0", "avater", "0", true), result), true)

                    processResponseFromDialogFlow(result)

//                aiRequest!!.sessionId = senderId
//                aiRequest?.language = "ko"
//                aiRequest!!.setQuery(result)
//
//                backgroundTask().execute(aiRequest)

                } else {
                    toast("공백은 전송할 수 없습니다.")
                }
                dialog.dismiss()
            }
        }

        try {
            Speech.getInstance().startListening(speechProgressView, delegate)
        } catch (e: SpeechRecognitionNotAvailable) {

        }
//        builder.show()
        dialog.show()

        return true
    }

    fun processResponseFromDialogFlow(result: String?) {

        Fuel.get(
                "https://api.dialogflow.com/v1/query?",
                listOf(
                        "v" to "20150910",
                        "sessionId" to sessions,   // random ID 세션 번호가 계속 바뀌니 연속적 대화가 불가능한건가?
                        "lang" to "ko",   // English language
                        "query" to result
                )
        ).header("Authorization" to "Bearer $keys")
                .responseJson { _, _, result ->

                    if (result.get().obj().getJSONObject("result").getJSONArray("contexts").length() != 0) {
                        val responseName = result
                                .get()
                                .obj()
                                .getJSONObject("result")
                                .getJSONArray("contexts")
                                .getJSONObject(0)
                                .getString("name")

                        val parameters = result
                                .get()
                                .obj()
                                .getJSONObject("result")
                                .getJSONArray("contexts")
                                .getJSONObject(0)
                                .getJSONObject("parameters")


                        val actions = result.get().obj().getJSONObject("result").getString("action")

                        val agentReply = result.get().obj()
                                .getJSONObject("result")
                                .getJSONObject("fulfillment")
                                .getString("speech")

                        //TODO 리코멘드 값 처리
                        processRecommandFromDialogFlow(actions, agentReply)

                        if (responseName == "writeuserrequest-yes-datain-followup") {

                            val writeType = parameters.getString("WriteType")
                            val dateTime = parameters.getString("date-time")
                            val dateTimeOriginal = parameters.getString("date-time.original")
                            val userTypeTime = parameters.getString("UserTypeTime")
                            val userTypeTimeOriginal = parameters.getString("UserTypeTime.original")
                            val timeOriginal = parameters.getString("time.original")
                            val numberIntegerByglucose = parameters.getString("number-integer")
                            val detailType = parameters.getString("DetailTypes")
                            val tms = parameters.getString("time")

                            Logger.getLogger(MainActivity::class.java.name).warning(
                                    "$writeType|$dateTime|$dateTimeOriginal|" +
                                            "$userTypeTime|$userTypeTimeOriginal|$timeOriginal" +
                                            "|$numberIntegerByglucose|$detailType|"
                            )

//                                    String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
                            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.KOREA)
                            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
                            val outputTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.KOREA)
                            val outputDateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)
                            val date = inputFormat.parse(dateTime) //2018-04-10T04:00:00.000Z
                            val formattedDate = outputFormat.format(date)
                            val formattedTime = outputTimeFormat.format(date)
                            val formattedDateTime = outputDateTimeFormat.format(date)
                            println(formattedDate) // prints 10-04-2018


                            realm?.executeTransaction {
                                val gluco = realm!!.createObject(Glucose::class.java)
                                gluco.date = date
                                gluco.rawDate = formattedDate
                                gluco.rawTime = formattedTime
                                gluco.datetime = formattedDateTime
                                gluco.userGlucoValue = numberIntegerByglucose.toFloat()
                                gluco.userTypeTime = userTypeTime // 아침 점심 저녁
                                gluco.userType = detailType // 운동
                                gluco.userDetailTime = tms
                            }

                            userExp += when (detailType) {
                                "공복" -> 10
                                "취침전" -> 10
                                "취침 전" -> 10
                                else -> 5
                            }

                            Paper.book("user").write("exp", userExp)

                            val intent = Intent(applicationContext, HomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()

                        }

                        Logger.getLogger(MainActivity::class.java.name).warning(
                                result
                                        .get()
                                        .obj()
                                        .getJSONObject("result")
                                        .getJSONArray("contexts")
                                        .getJSONObject(0)
                                        .getString("name")
                        )
                    }


                    val reply = result.get().obj()
                            .getJSONObject("result")
                            .getJSONObject("fulfillment")
                            .getString("speech")

                    Logger.getLogger(MainActivity::class.java.name).warning(reply)

                    messagesAdapter?.addToStart(Message("1", User("1", "agent", "1", true), reply), true)
                    //todo dialog flow 리턴값 tts 처리
                    speekResponse(reply)
                }

    }

    fun processRecommandFromDialogFlow(action: String?, agentReply: String) {


        if ("맞나요?" in agentReply) {
            recommandList?.clear()
            recommandList?.addAll(RecommendChat.getRecommendYes())
            recommandList?.addAll(RecommendChat.getRecommendNo())
            recommendAdapter?.notifyDataSetChanged()
        }

        if (action == "WriteUserRequest.WriteUserRequest-yes") {
            recommandList?.clear()
            recommendAdapter?.notifyDataSetChanged()
        }

        when (agentReply) {

            "안녕하세요" -> {
                recommandList?.clear()
                recommandList?.addAll(RecommendChat.getinitRecommendDataSet())
                recommendAdapter?.notifyDataSetChanged()
            }

            "네 알겠습니다. 혈당 기록을 진행할까요?" -> {
                recommandList?.clear()
                recommandList?.addAll(RecommendChat.getRecommendYes())
                recommendAdapter?.notifyDataSetChanged()
            }

            "언제 채혈하셨어요?" -> {
                recommandList?.clear()
                recommandList?.addAll(RecommendChat.getRecommendWhen())
                recommendAdapter?.notifyDataSetChanged()
            }

            "혈당 수치[mm/dL]는 어떻게 되세요?" -> {
                recommandList?.clear()
                recommendAdapter?.notifyDataSetChanged()
            }

            "측정하신 시점의 유형은 어떻게 되시나요? (공복, 식사전 또는 후, 운동 전 또는 후 취침전)" -> {
                recommandList?.clear()
                recommandList?.addAll(RecommendChat.getRecommendDetailTypes())
                recommendAdapter?.notifyDataSetChanged()
            }

            "아침, 점심, 저녁 언제 하셨어요?" -> {
                recommandList?.clear()
                recommandList?.addAll(RecommendChat.getRecommendDetailTypeTimes())
                recommendAdapter?.notifyDataSetChanged()
            }

        }
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
                messagesAdapter?.addToStart(
                        Message(
                                "1",
                                User("1", "agent", "1", true),
                                result.result.fulfillment.speech
                        ), true
                )
            }
            super.onPostExecute(result)
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
