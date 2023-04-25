package cn.edu.sustech.cs209.chatting.client;

import cn.edu.sustech.cs209.chatting.common.Message;
import cn.edu.sustech.cs209.chatting.common.User;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientInfo {
  public static final String HOST = "127.0.0.1";
  public static final int FILE_PORT = 52210;
  public static User currentUser;
  public static Socket clientSocket;
  public static ObjectOutputStream oos;
  public static ObjectInputStream ois;
  public static List<String> onlineUsers = new ArrayList<>();
  public static List<Message> userMessageList = new ArrayList<>();

}
