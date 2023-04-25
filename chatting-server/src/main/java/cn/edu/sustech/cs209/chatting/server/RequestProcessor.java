package cn.edu.sustech.cs209.chatting.server;

import cn.edu.sustech.cs209.chatting.common.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Objects;


public class RequestProcessor implements Runnable {
    private Socket currentClientSocket;
    private DBManager dbManager;

    public RequestProcessor(Socket currentClientSocket){
        this.currentClientSocket = currentClientSocket;
    }

    public void run() {
        boolean flag = true; //if listening break
        try{
            UserIO currentClientIOCache = new UserIO(
                new ObjectInputStream(currentClientSocket.getInputStream()),
                new ObjectOutputStream(currentClientSocket.getOutputStream()));
            dbManager = new DBManager();
            while(flag){ //read request from client
                Request request = (Request)currentClientIOCache.getOis().readObject();
                String actionName = request.getAction();   //get action in request
                System.out.println("actionName: " + actionName);
                if (actionName.equals("userRegister")) {  //user register
                    register(currentClientIOCache, request);

                }
                else if(actionName.equals("getHistoryMessage")){
                    getHistoryMessage(currentClientIOCache, request);
                }
                else if(actionName.equals("userLogin")) {  //user login
                    login(currentClientIOCache, request);
                }else if("getUserList".equals(actionName)){  //user list
                    getUserList(currentClientIOCache, request);
                }else if ("sendMessage".equals(actionName)) {  //send Message
                    sendMessage(currentClientIOCache, request);
                }
//                request.setAction("sendGroupMessage");
//                request.setAttribute("message", msg);
                else if("sendGroupMessage".equals(actionName)){
                    sendGroupMessage(currentClientIOCache, request); //send Group Message
                }
                else if("exit".equals(actionName)) {
                    flag = logout(currentClientIOCache, request);
                }
//                }else if("chat".equals(actionName)){
//                    chat(request);
//                }
                else if("preSendFile".equals(actionName)){ //send file ready
                    preSendFile(request);
                }
                else if ("getFile".equals(actionName)){ // ready to receive file
                    getFile(request);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void getHistoryMessage(UserIO currentClientIOCache, Request request) throws IOException {
        String username = (String) request.getAttribute("username");
        List<Message> historyMessage = dbManager.getMessage();
        Response response = new Response();
        response.setType(ResponseType.GETHISTORYMESSAGE);
        response.setData("historyMessage", historyMessage);
        sendResponse(currentClientIOCache, response);
    }

    public void preSendFile(Request request)throws IOException{
        Response response = new Response();
        response.setStatus(ResponseStatus.OK);
        response.setType(ResponseType.SENDFILE);
        FileInfo sendFile = (FileInfo)request.getAttribute("file");
        response.setData("sendFile", sendFile);
        UserIO ioSender = UserManager.UserIOMap.get(sendFile.getToUser());
        //get FileInfo from sender and forwarding to receiver
        sendResponse(ioSender, response);
    }

    private boolean logout(UserIO currentClientIO, Request request) throws IOException {
        User user = (User)request.getAttribute("user");
        UserManager.UserIOMap.remove(user);
        ClientSockets.userSocketMap.remove(user);
        Response response = new Response();
        response.setType(ResponseType.LOGOUT);
        response.setData("logoutUser", user);
        currentClientSocket.close();
        sendAllResponse(response);
        return false;
    }

    private void getFile(Request request) throws IOException {
        FileInfo sendFile = (FileInfo)request.getAttribute("sendFile");
        //To
        Response responseToReceiver = new Response();
        responseToReceiver.setType(ResponseType.RECEIVEFILE);
        responseToReceiver.setData("sendFile", sendFile);
        responseToReceiver.setStatus(ResponseStatus.OK);
        UserIO receiveIO = UserManager.UserIOMap.get(sendFile.getToUser());
        this.sendResponse(receiveIO, responseToReceiver);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //From
        Response response = new Response();
        response.setType(ResponseType.READYTORECEIVEFILE);
        response.setData("sendFile", sendFile);
        response.setStatus(ResponseStatus.OK);
        UserIO sendIO = UserManager.UserIOMap.get(sendFile.getFromUser());
        this.sendResponse(sendIO, response);



    }
//    /*广播*/
//    public static void board(String str) throws IOException {
//        User user = new User("admin");
//        Message msg = new Message();
//        msg.setFromUser(user);
//        msg.setSendTime(new Date());
//
//        DateFormat df = new SimpleDateFormat("HH:mm:ss");
//        StringBuffer sb = new StringBuffer();
//        sb.append(" ").append(df.format(msg.getSendTime())).append(" ");
//        sb.append("系统通知\n  "+str+"\n");
//        msg.setMessage(sb.toString());
//
//        Response response = new Response();
//        response.setStatus(ResponseStatus.OK);
//        response.setType(ResponseType.BOARD);
//        response.setData("txtMsg", msg);
//
//        for (Long id : DataBuffer.onlineUserIOCacheMap.keySet()) {
//            sendResponse_sys(DataBuffer.onlineUserIOCacheMap.get(id), response);
//        }
//    }
    //查询是否存在于数据库中并检查连接状态
    //找到对应map<User, Socket>并绑定当前socket与User
    public void login(UserIO currentClientIO, Request request) throws IOException {
        String id = (String)request.getAttribute("username");
        User user = new User(id);
        System.out.println("user: " + id);
        boolean userRegistered = dbManager.findUser(user);
        Response response = new Response();

        //if userManager map contains this user and the socket is connected
        if (userRegistered){//user is registered
            if(ClientSockets.userSocketMap.containsKey(user) && ClientSockets.userSocketMap.get(user).isConnected()){
                response.setStatus(ResponseStatus.ERROR);
                response.setData("msg", "This user has been logged in");
                currentClientIO.getOos().writeObject(response);
                currentClientIO.getOos().flush();
            } else { //login successfully
                ClientSockets.bindSocket(user, currentClientSocket);
                UserManager.UserIOMap.put(user, currentClientIO);
                response.setStatus(ResponseStatus.OK);
                response.setData("msg", "Login successfully");
                currentClientIO.getOos().writeObject(response);
                currentClientIO.getOos().flush();
                Response responseAll = new Response();
                responseAll.setType(ResponseType.LOGIN);
                responseAll.setData("loginUser", user);
                sendAllResponse(responseAll);
            }
        } else {
            response.setStatus(ResponseStatus.ERROR);
            response.setData("msg", "This user is not registered");
            currentClientIO.getOos().writeObject(response);
            currentClientIO.getOos().flush();
        }

    }
    //get userList in ClientSockets and send to client
    public void getUserList(UserIO currentClientIO, Request request) throws IOException {
        Response response = new Response();
        response.setType(ResponseType.GETUSERLIST);
        response.setData("userList", ClientSockets.getUserList());
        System.out.println(response.getData("userList"));

        currentClientIO.getOos().writeObject(response);
        currentClientIO.getOos().flush();
    }
    public void sendMessage(UserIO currentClientIO, Request request) throws IOException {
        Message message = (Message)request.getAttribute("message");
        User toUser = message.getToUser();
        User fromUser = message.getFromUser();
        System.out.println("fromUser: " + fromUser);
        System.out.println("toUser: " + toUser);
        System.out.println("message: " + message);
        Response response = new Response();
        response.setType(ResponseType.SENDMESSAGE);
        response.setData("message", message);
        //TODO:
        dbManager.addMessage(message);
        UserIO toUserIO = UserManager.UserIOMap.get(toUser);
        sendResponse(toUserIO, response);

    }
    public void sendGroupMessage(UserIO currentClientIO, Request request) throws IOException {
        Message message = (Message) request.getAttribute("message");
        User fromUser = message.getFromUser();
        User toUser = message.getToUser();
        List<User> toUserList = toUser.getGroupMembers();
        for (User user : toUserList) {
            System.out.println(user.getNickname());
            if (Objects.equals(user.getNickname(), fromUser.getNickname())) {
                continue;
            }
            Response response = new Response();
            response.setType(ResponseType.SENDGROUPMESSAGE);
            response.setData("message", message);
            UserIO toUserIO = UserManager.UserIOMap.get(user);
            sendResponse(toUserIO, response);
        }
        System.out.println("fromUser: " + fromUser);
        System.out.println("message: " + message);
    }
    //register
    public void register(UserIO currentClientIO, Request request) throws IOException {
        User user = (User)request.getAttribute("user");
        System.out.println("user: " + user);
        boolean userRegistered = dbManager.findUser(user);
        Response response = new Response();
        if (userRegistered){
            response.setStatus(ResponseStatus.ERROR);
            response.setData("msg", "This user has been registered");
            currentClientIO.getOos().writeObject(response);
            currentClientIO.getOos().flush();
        } else {
            dbManager.addUser(user);
            response.setStatus(ResponseStatus.OK);
            response.setData("msg", "Register successfully");
            currentClientIO.getOos().writeObject(response);
            currentClientIO.getOos().flush();
        }
    }

    /** 给所有在线客户都发送响应 */
    private void sendAllResponse(Response response) throws IOException {
        for(UserIO onlineUserIO : UserManager.UserIOMap.values()){
            ObjectOutputStream oos = onlineUserIO.getOos();
            oos.writeObject(response);
            oos.flush();
        }
    }

    /** 向指定客户端IO的输出流中输出指定响应 */
    private void sendResponse(UserIO onlineUserIO, Response response)throws IOException {
        if (onlineUserIO != null) {
            ObjectOutputStream oos = onlineUserIO.getOos();
            oos.writeObject(response);
            oos.flush();
        }

    }

    /** 向指定客户端IO的输出流中输出指定响应 */
    private static void sendResponse_sys(UserIO onlineUserIO, Response response)throws IOException {
        ObjectOutputStream oos = onlineUserIO.getOos();
        oos.writeObject(response);
        oos.flush();
    }

}