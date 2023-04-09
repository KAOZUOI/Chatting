package cn.edu.sustech.cs209.chatting.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DBManager extends DBConnector{
    private static final String IP_ADDRESS = "localhost";
    private static final String PORT_NUM = "3456";
    private static final String DB_NAME ="postgresql";
    private static final String PASSWORD = "***";
    private static final String USER_NAME = "postgresql";
    String url = "jdbc:postgresql://localhost:3456/chatroom";
    private static Connection conn;
    public ArrayList<User> findAllUser() {
        UserManager userManager = new UserManager();
        ArrayList<String> names = new ArrayList<String>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            String sql = "SELECT nickname FROM users_passwords";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery(sql);
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
                if (conn != null) conn.close();
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
        ResultSet rs = null;
        try {
            String sql = "INSERT INTO users_passwords (nickname, pwd) VALUES (?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getNickname());
            stmt.setString(2, user.getPassword());
            rs = stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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
