package cn.edu.sustech.cs209.chatting.common;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 4037239245053114759L;
    private User toUser;
    private User fromUser;

    private String message;

    private Date sendTime;


    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }
}
