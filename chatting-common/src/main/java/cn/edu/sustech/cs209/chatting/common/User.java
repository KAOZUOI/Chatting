package cn.edu.sustech.cs209.chatting.common;

import javax.swing.*;
import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private static final long serialVersionUID = 5942011574971970871L;
    private long id;
    private String nickname;




    private Boolean ifGroup = false;



    private List<User> groupMembers;

    public User(String nickname) {
        if (nickname.equals("")) {
            this.nickname = "未命名";
        } else {
            this.nickname = nickname;
        }
    }

    public User(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
            return other.nickname == null;
        } else return nickname.equals(other.nickname);
    }

    @Override
    public String toString() {
        return this.getClass().getName()
            + "[id=" + this.id
            + ",nickname=" + this.nickname
            + "]";
    }

    public String getPassword() {
        return null;
    }
    public void setIfGroup(Boolean ifGroup) {
        this.ifGroup = ifGroup;
    }
    public Boolean getIfGroup() {
        return ifGroup;
    }
    public List<User> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(List<User> groupMembers) {
        this.groupMembers = groupMembers;
    }
}