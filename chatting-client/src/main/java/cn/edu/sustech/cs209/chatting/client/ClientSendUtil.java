package cn.edu.sustech.cs209.chatting.client;

import cn.edu.sustech.cs209.chatting.common.Message;
import cn.edu.sustech.cs209.chatting.common.Request;
import cn.edu.sustech.cs209.chatting.common.Response;

import java.io.IOException;


public class ClientSendUtil {

    /** 发送请求对象,主动接收响应 */
    public static Response sendTextRequest(Request request) throws IOException {
        Response response = null;
        try {
            // 发送请求
            ClientInfo.oos.writeObject(request);
            ClientInfo.oos.flush();
            System.out.println("Client send request: " + request.getAction());
            if(!"exit".equals(request.getAction())){
                response = (Response) ClientInfo.ois.readObject();
                System.out.println("Client receive response: " + response);
            }else{
                System.out.println("Client exit");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return response;
    }

    /** 发送请求对象,不主动接收响应 */
    public static void sendTextRequestPure(Request request) throws IOException {
        ClientInfo.oos.writeObject(request); // 发送请求
        ClientInfo.oos.flush();
    }

    /** 把指定文本添加到消息列表文本域中 */
    public static void appendTxt2MsgListArea(Message msg) {
        //

    }
}

