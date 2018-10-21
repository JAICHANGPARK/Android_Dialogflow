package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.model.Message
import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.model.User
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import lab.dialogflow.com.dreamwalker.chatkit.messages.MessageInput
import lab.dialogflow.com.dreamwalker.chatkit.messages.MessagesList
import lab.dialogflow.com.dreamwalker.chatkit.messages.MessagesListAdapter
import java.util.*
import java.util.logging.Logger

class MainActivityV2 : AppCompatActivity(), MessageInput.InputListener, MessageInput.TypingListener,
    MessageInput.AttachmentsListener, MessagesListAdapter.SelectionListener, MessagesListAdapter.OnLoadMoreListener {


    protected val senderId = "0"
    private var messagesList: MessagesList? = null
    internal var messagesAdapter: MessagesListAdapter<Message>? = null
    val sessions = UUID.randomUUID()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_v2)

        messagesList = findViewById<View>(R.id.messagesList) as MessagesList
        initAdapter()

        val input = findViewById<View>(R.id.input_msg) as MessageInput
        input.setInputListener(this)
        input.setTypingListener(this)
        input.setAttachmentsListener(this)

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

            }
        return true
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
}
