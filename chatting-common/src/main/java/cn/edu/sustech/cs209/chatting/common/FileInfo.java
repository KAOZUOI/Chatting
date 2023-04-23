package cn.edu.sustech.cs209.chatting.common;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

public class FileInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 169895250418838944L;
    private User toUser;
    private User fromUser;
    private String srcName;
    private Date sendTime;
    private String destIp;
    private int destPort;
    private String destName;
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
    public String getPathName() {
        return srcName;
    }
    public void setPathName(String srcName) {
        this.srcName = srcName;
    }
    public Date getSendTime() {
        return sendTime;
    }
    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }
    public String getDestIp() {
        return destIp;
    }
    public void setDestIp(String destIp) {
        this.destIp = destIp;
    }
    public int getDestPort() {
        return destPort;
    }
    public void setDestPort(int destPort) {
        this.destPort = destPort;
    }
    public String getDestName() {
        return destName;
    }
    public void setDestName(String destName) {
        this.destName = destName;
    }
}