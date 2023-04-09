package cn.edu.sustech.cs209.chatting.common;

import javax.swing.*;
import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 5942011574971970871L;
    private long id;
    private String password;
    private String nickname;

    public User(String nickname, String password) {
        this.password = password;
        if (nickname.equals("")) {
            this.nickname = "未命名";
        } else {
            this.nickname = nickname;
        }
    }

    public User(long id, String password) {
        this.id = id;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return this.nickname;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >> 32));
        result = prime * result + ((nickname == null) ? 0 : nickname.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (id != other.id)
            return false;
        if (nickname == null) {
            if (other.nickname != null)
                return false;
        } else if (!nickname.equals(other.nickname))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return this.getClass().getName()
            + "[id=" + this.id
            + ",pwd=" + this.password
            + ",nickname=" + this.nickname
            + "]";
    }
}