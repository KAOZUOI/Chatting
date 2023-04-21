package cn.edu.sustech.cs209.chatting.client;

import cn.edu.sustech.cs209.chatting.common.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ClientThread extends Thread {
    Controller controller;
    public void run() {
        try {
            while (ClientInfo.clientSocket.isConnected()) {
                System.out.println("Client receive response");
                Response response = (Response) ClientInfo.ois.readObject();
                ResponseType type = response.getType();
                if (type == ResponseType.LOGIN) {
                    User newUser = (User) response.getData("loginUser");
                    ClientInfo.onlineUsers.add(newUser.getNickname());
                }
                else if (type == ResponseType.GETUSERLIST){
                    List<String> updateUserList = (List<String>) response.getData("userList");
                    ClientInfo.onlineUsers = updateUserList;
                }
                else if (type == ResponseType.LOGOUT){
                    //TODO: logout
                }
                else if(type == ResponseType.SENDMESSAGE){
                    Message msg = (Message)response.getData("message");
                    System.out.println(msg.getMessage());
                    // update chatContentList in Controller
                    updateChatContentList(msg);




                }else if(type == ResponseType.AGREERECEIVEFILE){ //对方同意接收文件
                    sendFile(response);
                }else if(type == ResponseType.RECEIVEFILE){ //开始接收文件
                    receiveFile(response);
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /** 发送文件 */
    private void sendFile(Response response) {
        final FileInfo sendFile = (FileInfo)response.getData("sendFile");

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        Socket socket = null;
        try {
            socket = new Socket(sendFile.getDestIp(),sendFile.getDestPort());//套接字连接
            bis = new BufferedInputStream(new FileInputStream(sendFile.getSrcName()));//文件读入
            bos = new BufferedOutputStream(socket.getOutputStream());//文件写出

            byte[] buffer = new byte[1024];
            int n = -1;
            while ((n = bis.read(buffer)) != -1){
                bos.write(buffer, 0, n);
            }
            bos.flush();
            synchronized (this) {
//                ClientSendUtil.appendTxt2MsgListArea("【文件消息】文件发送完毕!\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            IOUtil.close(bis,bos);
            SocketUtil.close(socket);
        }
    }

    /** 接收文件 */
    private void receiveFile(Response response) {
        final FileInfo sendFile = (FileInfo)response.getData("sendFile");

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        ServerSocket serverSocket = null;
        Socket socket = null;
        try {
            serverSocket = new ServerSocket(sendFile.getDestPort());
            socket = serverSocket.accept(); //接收
            bis = new BufferedInputStream(socket.getInputStream());//缓冲读
            bos = new BufferedOutputStream(new FileOutputStream(sendFile.getDestName()));//缓冲写出

            byte[] buffer = new byte[1024];
            int n = -1;
            while ((n = bis.read(buffer)) != -1){
                bos.write(buffer, 0, n);
            }
            bos.flush();
            synchronized (this) {
                //ClientSendUtil.appendTxt2MsgListArea("【文件消息】文件接收完毕!存放在["
                    //+ sendFile.getDestName()+"]\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            IOUtil.close(bis,bos);
            SocketUtil.close(socket);
            SocketUtil.close(serverSocket);
        }
    }

    /** 准备发送文件	 */
//    private void toSendFile(Response response) {
//        FileInfo sendFile = (FileInfo)response.getData("sendFile");
//
//        String fromName = sendFile.getFromUser().getNickname()
//            + "(" + sendFile.getFromUser().getId() + ")";
//        String fileName = sendFile.getSrcName()
//            .substring(sendFile.getSrcName().lastIndexOf(File.separator)+1);
//
//        try {
//            Request request = new Request();
//            request.setAttribute("sendFile", sendFile);
//
//
//            JFileChooser jfc = new JFileChooser();
//            jfc.setSelectedFile(new File(fileName));
//            int result = jfc.showSaveDialog(this.currentFrame);
//
//            if (result == JFileChooser.APPROVE_OPTION){
//                //设置目的地文件名
//                sendFile.setDestName(jfc.getSelectedFile().getCanonicalPath());
//                //设置目标地的IP和接收文件的端口
//                sendFile.setDestIp(ClientInfo.ip);
//                sendFile.setDestPort(ClientInfo.RECEIVE_FILE_PORT);
//
//                request.setAction("agreeReceiveFile");
////                    receiveFile(response);
//                ClientSendUtil.appendTxt2MsgListArea("【文件消息】您已同意接收来自 "
//                    + fromName +" 的文件，正在接收文件 ...\n");
//            } else {
//                request.setAction("refuseReceiveFile");
//                ClientSendUtil.appendTxt2MsgListArea("【文件消息】您已拒绝接收来自 "
//                    + fromName +" 的文件!\n");
//            }
//
//
//            ClientSendUtil.sendTextRequest2(request);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    public void setController(Controller controller) {
        this.controller = controller;
    }
    public void updateChatContentList(Message message) {
        Platform.runLater(() -> {
            // 更新chatContentList
            controller.chatContentList.getItems().add(message);
        });
    }
}
