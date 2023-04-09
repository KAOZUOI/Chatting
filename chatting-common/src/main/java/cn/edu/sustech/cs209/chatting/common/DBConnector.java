package cn.edu.sustech.cs209.chatting.common;

import java.sql.*;


public class DBConnector {
    private static final String IP_ADDRESS = "localhost";
    private static final String PORT_NUM = "5432";
    private static final String DB_NAME ="Chatting";
    private static final String PASSWORD = "020928";
    private static final String USER_NAME = "postgres";
    String url = "jdbc:postgresql://localhost:3456/chatroom";
    private static Connection connection;

    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(
                "jdbc:postgresql://" + IP_ADDRESS + ":" + PORT_NUM + "/" + DB_NAME +
                    "?userSSL=false&useUnicode=true&characterEncoding=UTF8&serverTimezone=GMT",
                USER_NAME, PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }
    public static void closeConnection(){
        try {
            connection.close();
            connection = null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
