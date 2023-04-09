package cn.edu.sustech.cs209.chatting.common;

import cn.edu.sustech.cs209.chatting.common.DBManager;
import cn.edu.sustech.cs209.chatting.common.User;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientSockets {

    public static Map<User, Socket> userSocketMap;
    public ClientSockets() {
        DBManager dbManager = new DBManager();
        ArrayList<User> users = dbManager.findAllUser();
        for (User user : users) {
            userSocketMap.put(user, null);
        }
    }
    public static Socket getSocketByNickname(String nickname) {
        for (Map.Entry<User, Socket> entry : userSocketMap.entrySet()) {
            if (entry.getKey().getNickname().equals(nickname)) {
                return entry.getValue();
            }
        }
        return null;
    }
    public static List<String> getUserList() {
        List<String> userList = new ArrayList<>();
        for (Map.Entry<User, Socket> entry : userSocketMap.entrySet()) {
            String username = entry.getKey().getNickname();
            Socket socket = entry.getValue();
            boolean isOnline = socket != null && socket.isConnected();
            if (isOnline) {
                userList.add(username + " (Online)");
            } else {
                userList.add(username + " (Offline)");
            }
        }
        return userList;
    }

    public static Map<User, Socket> getUserSocketMap() {
        return userSocketMap;
    }
    public static boolean checkUserNameOnline(String username) {
        for (User u : userSocketMap.keySet()) {
            if (username.equals(u.getNickname())) {
                Socket socket = userSocketMap.get(u);
                if (socket != null && socket.isConnected() && !socket.isClosed()) {
                    return true;
                }
            }
        }
        return false;
    }
    public static boolean checkUserOnline(User user) {
        for (Map.Entry<User, Socket> entry : userSocketMap.entrySet()) {
            if (entry.getKey().equals(user)) {
                Socket socket = entry.getValue();
                if (socket != null && socket.isConnected() && !socket.isClosed()) {
                    return true;
                }
            }
        }
        return false;
    }



    public static void bindSocket(User user, Socket socket) {
        userSocketMap.put(user, socket);
    }
}
