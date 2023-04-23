package cn.edu.sustech.cs209.chatting.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class UserIO {
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    public UserIO(ObjectInputStream ois, ObjectOutputStream oos){
        this.ois = ois;
        this.oos = oos;
    }
    public ObjectOutputStream getOos(){
        return oos;
    }
    public ObjectInputStream getOis() {
        return ois;
    }
}
