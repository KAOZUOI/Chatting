package cn.edu.sustech.cs209.chatting.client;

import cn.edu.sustech.cs209.chatting.common.User;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientInfo {
    /** 当前用户 */
    public static User currentUser;
    /** 当前客户端连接到服务器的Socket */
    public static Socket clientSocket;
    /** 当前客户端连接到服务器的输出流 */
    public static ObjectOutputStream oos;
    /** 当前客户端连接到服务器的输入流 */
    public static ObjectInputStream ois;
    /** 当前用户信息 */
    public static List<String> onlineUsers = new ArrayList<>();

}
