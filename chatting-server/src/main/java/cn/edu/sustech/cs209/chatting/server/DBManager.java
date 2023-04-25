package cn.edu.sustech.cs209.chatting.server;

import cn.edu.sustech.cs209.chatting.common.Message;
import cn.edu.sustech.cs209.chatting.common.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBManager extends DBConnector {
    private static Connection conn = getConnection();;
    public boolean findUser(User user){
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            String sql = "SELECT nickname FROM users_passwords WHERE nickname=?;";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getNickname());
            rs = stmt.executeQuery();

            if (rs.next() && rs.getString(1).equals(user.getNickname())) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public ArrayList<User> findAllUser() {
        UserManager userManager = new UserManager();
        ArrayList<String> names = new ArrayList<String>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            String sql = "SELECT nickname FROM users_passwords";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String nickname = rs.getString("nickname");
                names.add(nickname);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        ArrayList<User> users = new ArrayList<User>();
        for (String name : names) {
            users.add(userManager.getUserByUsername(name));
        }
        return users;
    }
    public int getUserNumberByName(String nickname) {
        int userNumber = 0;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT COUNT(*) FROM users_passwords WHERE nickname=?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nickname);
            rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return userNumber;
    }
    public void addUser(User user) {
        PreparedStatement stmt = null;
        int rs = 0;
        try {
            String sql = "INSERT INTO users_passwords (nickname, pwd) VALUES (?,?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getNickname());
            stmt.setString(2, user.getNickname());
            rs = stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public void addMessage(Message message){
        PreparedStatement stmt = null;
        int rs = 0;
        try {
            String sql = "INSERT INTO messages (sender, receiver, content) VALUES (?,?,?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, message.getFromUser().getNickname());
            stmt.setString(2, message.getToUser().getNickname());
            stmt.setString(3, message.getMessage());
            rs = stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public List<Message> getMessage(){
        List<Message> messages = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM messages order by timestamp";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String sender = rs.getString("sender");
                String receiver = rs.getString("receiver");
                String content = rs.getString("content");
                Message message = new Message();
                message.setFromUser(new User(sender));
                message.setToUser(new User(receiver));
                message.setMessage(content);
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return messages;

    }
    public static void main(String[] args) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT id, nickname, pwd FROM users_passwords WHERE nickname=?";
        stmt = conn.prepareStatement(sql);
        stmt.setString(1, "anonymous");
        rs = stmt.executeQuery();
        if (rs.next()) {
            int id = rs.getInt("id");
            String password = rs.getString("pwd");
            System.out.println(password);
        }
    }
}
