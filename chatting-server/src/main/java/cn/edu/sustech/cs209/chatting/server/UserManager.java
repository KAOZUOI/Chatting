package cn.edu.sustech.cs209.chatting.server;

import cn.edu.sustech.cs209.chatting.common.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class UserManager {
  ArrayList<User> usersList = new ArrayList<User>();
  public static Map<User, UserIO> UserIOMap = new HashMap<User, UserIO>();

  public void addUser(User user) {
    usersList.add(user);
  }

  public void removeUser(User user) {
    usersList.remove(user);
  }

  public User getUserByUsername(String username) {
    for (User user : usersList) {
      if (user.getNickname().equals(username)) {
        return user;
      }
    }
    return null;
  }

  public ArrayList<User> getAllUsers() {
    return usersList;
  }
}

