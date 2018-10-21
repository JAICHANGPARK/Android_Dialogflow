package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.model;

import android.speech.tts.Voice;
import lab.dialogflow.com.dreamwalker.chatkit.commons.models.IMessage;
import lab.dialogflow.com.dreamwalker.chatkit.commons.models.IUser;

import java.util.Date;

public class Message implements IMessage {
    private String id;
    private String text;
    private Date createdAt;
    private User user;
    private Voice voice;

    public Message(String id, User user, String text) {
        this(id, user, text, new Date());
    }

    public Message(String id, User user, String text, Date createdAt) {
        this.id = id;
        this.text = text;
        this.user = user;
        this.createdAt = createdAt;
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public IUser getUser() {
        return user;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }
}
