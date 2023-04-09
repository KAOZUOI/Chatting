package cn.edu.sustech.cs209.chatting.client;

import cn.edu.sustech.cs209.chatting.common.ClientSockets;
import cn.edu.sustech.cs209.chatting.common.Message;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class ChatItem extends HBox {

    private String title;
    private ListView<Message> messageListView;
    static ArrayList<ChatItem> chatList;

    public ChatItem(String title) {
        this.title = title;


        messageListView = new ListView<>();
        messageListView.setPrefSize(300, 400);


        TextField inputTextField = new TextField();
        inputTextField.setPrefWidth(300);
        inputTextField.setPromptText("Type your message here...");


        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            String messageText = inputTextField.getText();
            if (!messageText.trim().isEmpty()) {
//                // 将消息添加到消息列表
//                Message message = new Message(currentUsername, messageText.trim());
//                messageListView.getItems().add(message);

                // 发送消息给服务器
                ClientSockets.getSocketByNickname(title);
//                ClientSockets.sendMessageToUser(title, messageText.trim());

                // 清空输入框
                inputTextField.clear();
            }
        });

        // 将输入框和发送按钮放在同一个HBox中
        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER_LEFT);
        inputBox.getChildren().addAll(inputTextField, sendButton);

        // 将消息列表和输入框放在一个垂直的VBox中
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.getChildren().addAll(messageListView, inputBox);

        // 将ChatItem以便可以将其到面板的聊天列表中
        this.getChildren().add(vbox);
    }

    public String getTitle() {
        return title;
    }
}

