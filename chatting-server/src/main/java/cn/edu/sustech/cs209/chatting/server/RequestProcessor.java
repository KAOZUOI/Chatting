package cn.edu.sustech.cs209.chatting.server;

import cn.edu.sustech.cs209.chatting.common.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RequestProcessor implements Runnable {
    private Socket currentClientSocket;  //当前正在请求服务器的客户端Socket

    public RequestProcessor(Socket currentClientSocket){
        this.currentClientSocket = currentClientSocket;
    }

    public void run() {
        boolean flag = true; //if listening break
        try{
            UserIO currentClientIOCache = new UserIO(
                new ObjectInputStream(currentClientSocket.getInputStream()),
                new ObjectOutputStream(currentClientSocket.getOutputStream()));
            while(flag){ //read request from client
                Request request = (Request)currentClientIOCache.getOis().readObject();
                System.out.println("ServerRead:" + request.getAction());

                String actionName = request.getAction();   //get action in request
                if("exit".equals(actionName)){
                    flag = logout(currentClientIOCache, request);
                }else if("chat".equals(actionName)){
                    chat(request);
                } else if("toSendFile".equals(actionName)){
                    toSendFile(request);
                }else if("ReceiveFile".equals(actionName)){
                    ReceiveFile(request);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /** 同意接收文件 */
    private void ReceiveFile(Request request) throws IOException {
        FileInfo sendFile = (FileInfo)request.getAttribute("sendFile");
        //向请求方(发送方)的输出流输出响应
        Response response = new Response();  //创建一个响应对象
        response.setType(ResponseType.AGREERECEIVEFILE);
        response.setData("sendFile", sendFile);
        response.setStatus(ResponseStatus.OK);
        UserIO sendIO = UserManager.UserIOMap.get(sendFile.getFromUser());
        this.sendResponse(sendIO, response);

        //向接收方发出接收文件的响应
        Response response2 = new Response();  //创建一个响应对象
        response2.setType(ResponseType.RECEIVEFILE);
        response2.setData("sendFile", sendFile);
        response2.setStatus(ResponseStatus.OK);
        UserIO receiveIO = UserManager.UserIOMap.get(sendFile.getToUser());
        this.sendResponse(receiveIO, response2);
    }

    /** 客户端退出 */
    public boolean logout(UserIO oio, Request request) throws IOException{
        System.out.println(currentClientSocket.getInetAddress().getHostAddress()
            + ":" + currentClientSocket.getPort() + "走了");

        User user = (User)request.getAttribute("user");

        currentClientSocket.close();  //关闭这个客户端Socket

        DataBuffer.onlineUserTableModel.remove(user.getId()); //把当前下线用户从在线用户表Model中删除
        Response response = new Response();
        iteratorResponse(response);//通知所有其它在线客户端

        return false;  //断开监听
    }


    /** 聊天 */
    public void chat(Request request) throws IOException {
        Message msg = (Message)request.getAttribute("msg");
        Response response = new Response();
        response.setStatus(ResponseStatus.OK);
        response.setType(ResponseType.CHAT);
        response.setData("txtMsg", msg);

        if(msg.getToUser() != null){ //私聊:只给私聊的对象返回响应
            UserIO io = DataBuffer.onlineUserIOCacheMap.get(msg.getToUser().getId());
            sendResponse(io, response);
        }else{  //群聊:给除了发消息的所有客户端都返回响应
            for(Long id : DataBuffer.onlineUserIOCacheMap.keySet()){
                if(msg.getFromUser().getId() == id ){	continue; }
                sendResponse(DataBuffer.onlineUserIOCacheMap.get(id), response);
            }
        }
    }

    /*广播*/
    public static void board(String str) throws IOException {
        User user = new User(1,"admin");
        Message msg = new Message();
        msg.setFromUser(user);
        msg.setSendTime(new Date());

        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        StringBuffer sb = new StringBuffer();
        sb.append(" ").append(df.format(msg.getSendTime())).append(" ");
        sb.append("系统通知\n  "+str+"\n");
        msg.setMessage(sb.toString());

        Response response = new Response();
        response.setStatus(ResponseStatus.OK);
        response.setType(ResponseType.BOARD);
        response.setData("txtMsg", msg);

        for (Long id : DataBuffer.onlineUserIOCacheMap.keySet()) {
            sendResponse_sys(DataBuffer.onlineUserIOCacheMap.get(id), response);
        }
    }





    /** 准备发送文件 */
    public void toSendFile(Request request)throws IOException{
        Response response = new Response();
        response.setStatus(ResponseStatus.OK);
        response.setType(ResponseType.TOSENDFILE);
        FileInfo sendFile = (FileInfo)request.getAttribute("file");
        response.setData("sendFile", sendFile);
        //给文件接收方转发文件发送方的请求
        UserIO ioCache = DataBuffer.onlineUserIOCacheMap.get(sendFile.getToUser().getId());
        sendResponse(ioCache, response);
    }

    /** 给所有在线客户都发送响应 */
    private void iteratorResponse(Response response) throws IOException {
        for(UserIO onlineUserIO : UserManager.UserIOMap.values()){
            ObjectOutputStream oos = onlineUserIO.getOos();
            oos.writeObject(response);
            oos.flush();
        }
    }

    /** 向指定客户端IO的输出流中输出指定响应 */
    private void sendResponse(UserIO onlineUserIO, Response response)throws IOException {
        ObjectOutputStream oos = onlineUserIO.getOos();
        oos.writeObject(response);
        oos.flush();
    }

    /** 向指定客户端IO的输出流中输出指定响应 */
    private static void sendResponse_sys(UserIO onlineUserIO, Response response)throws IOException {
        ObjectOutputStream oos = onlineUserIO.getOos();
        oos.writeObject(response);
        oos.flush();
    }
}