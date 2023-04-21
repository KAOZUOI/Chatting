package cn.edu.sustech.cs209.chatting.client;

import cn.edu.sustech.cs209.chatting.common.*;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.stream.Collectors.toList;

public class Controller implements Initializable {
    String ip = "localhost";
    int port = 52209;
    @FXML
    ListView<Message> chatContentList;
    @FXML
    ListView<User> chatList;
    @FXML
    private TextArea inputArea;


    String username;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Login");
        dialog.setHeaderText("Please input Username and Password");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password:");
        VBox box = new VBox();
        box.getChildren().addAll(dialog.getEditor(), passwordField);
        dialog.getDialogPane().setContent(box);
        Optional<String> input = dialog.showAndWait();
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        String inputPassword = passwordField.getText();
        try {
            ClientInfo.clientSocket = new Socket(ip, port);
            ClientInfo.oos = new ObjectOutputStream(ClientInfo.clientSocket.getOutputStream());
            ClientInfo.ois = new ObjectInputStream(ClientInfo.clientSocket.getInputStream());
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Fail to connect");
            alert.showAndWait();
            e.printStackTrace();
        }

        if (input.isPresent() && !input.get().isEmpty()) {
            /*Check if there is a user with the same name among the currently logged-in users,
              if so, ask the user to change the username */
            username = input.get();
            AtomicBoolean usernameValid = new AtomicBoolean(false);
            while (!usernameValid.get()) {
                System.out.println(username);
                User user = new User(username);
                Request request = new Request();
                request.setAction("userLogin");
                request.setAttribute("username", user.getNickname());
                try {
                    Response response = ClientSendUtil.sendTextRequest(request);
                    System.out.println(response.getStatus());
                    if (response.getStatus() == ResponseStatus.OK) {
                        usernameValid.set(true);
                        ClientInfo.currentUser = user;
                        System.out.println("Login successfully");
                    } else {
                        cancelButton.setOnAction(event -> usernameValid.set(true));
                        dialog.setTitle("Alert");
                        dialog.setHeaderText("Invalid username");
                        Optional<String> newInput = dialog.showAndWait();
                        if (newInput.isPresent() && !newInput.get().isEmpty()) {
                            username = newInput.get();
                        } else {
                            System.out.println("Invalid username");
                            usernameValid.set(false);
                            Platform.exit();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        } else {
            System.out.println("Invalid username " + input + ", exiting");
            Platform.exit();
        }

        chatContentList.setCellFactory(new MessageCellFactory());
        chatList.setCellFactory(new UserCellFactory());
        System.out.println("initialize");
        ClientThread clientThread = new ClientThread();//TODO:start a thread to receive message from server
        clientThread.setController(this);
        clientThread.start();
    }

    @FXML
    public void createPrivateChat() {
        System.out.println("create private chat");
        AtomicReference<String> user = new AtomicReference<>();

        Stage stage = new Stage();
        ComboBox<String> userSel = new ComboBox<>();


        //set listener when I click the comboBox, the client send request to server to get the user list in ClientSockets
        //get the user list from server, the current user's name should be filtered out
        userSel.setOnMouseClicked(e -> {
            try {
                Request request = new Request();
                request.setAction("getUserList");
                ClientSendUtil.sendTextRequestPure(request);
                List<String> filteredList = ClientInfo.onlineUsers.
                    stream()
                    .filter(name -> !name.equals(username))
                    .collect(toList());
                System.out.println(filteredList);
                userSel.getItems().clear();
                userSel.getItems().addAll(filteredList);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


        Button okBtn = new Button("OK");
        okBtn.setOnAction(e -> {
            user.set(userSel.getSelectionModel().getSelectedItem());
            stage.close();
        });

        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20, 20, 20, 20));
        box.getChildren().addAll(userSel, okBtn);
        stage.setScene(new Scene(box));
        stage.showAndWait();


        //if the current user already chatted with the selected user, just open the chat with that user
        //otherwise, create a new chat item in the left panel, the title should be the selected user's name

        if (user.get() != null) {
            boolean exist = false;
            for (User u : chatList.getItems()) {
                if (u.getNickname().equals(user.get())) {
                    chatList.getSelectionModel().select(u);
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                User newUser = new User(user.get());
                chatList.getItems().add(newUser);
                chatList.getSelectionModel().select(newUser);
            }
        }








    }

    /**
     * A new dialog should contain a multi-select list, showing all user's name.
     * You can select several users that will be joined in the group chat, including yourself.
     * <p>
     * The naming rule for group chats is similar to WeChat:
     * If there are > 3 users: display the first three usernames, sorted in lexicographic order, then use ellipsis with the number of users, for example:
     * UserA, UserB, UserC... (10)
     * If there are <= 3 users: do not display the ellipsis, for example:
     * UserA, UserB (2)
     */
    @FXML
    public void createGroupChat() {

    }

    /**
     * Sends the message to the <b>currently selected</b> chat.
     * <p>
     * Blank messages are not allowed.
     * After sending the message, you should clear the text input field.
     */
    @FXML
    public void doSendMessage() {
        // TODO: Sends the message to the currently selected chat.
        //Blank messages are not allowed.
        //After sending the message, you should clear the text input field.
        String message = inputArea.getText();
        if (message != null && !message.isEmpty()) {
            User user = chatList.getSelectionModel().getSelectedItem();
            System.out.println(user.getNickname());
            if (user != null) {
                Message msg = new Message();
                msg.setFromUser(ClientInfo.currentUser);
                msg.setToUser(user);
                msg.setMessage(message);
                chatContentList.getItems().add(msg);

                Request request = new Request();
                request.setAction("sendMessage");
                request.setAttribute("message", msg);
                try {
                    ClientSendUtil.sendTextRequestPure(request);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        inputArea.clear();



    }

    /**
     * You may change the cell factory if you changed the design of {@code Message} model.
     * Hint: you may also define a cell factory for the chats displayed in the left panel, or simply override the toString method.
     */
    private class MessageCellFactory implements Callback<ListView<Message>, ListCell<Message>> {
        @Override
        public ListCell<Message> call(ListView<Message> param) {
            return new ListCell<Message>() {

                @Override
                public void updateItem(Message msg, boolean empty) {
                    super.updateItem(msg, empty);
                    if (empty || Objects.isNull(msg)) {
                        return;
                    }

                    HBox wrapper = new HBox();
                    Label nameLabel = new Label(msg.getFromUser().getNickname());
                    Label msgLabel = new Label(msg.getMessage());

                    nameLabel.setPrefSize(50, 20);
                    nameLabel.setWrapText(true);
                    nameLabel.setStyle("-fx-border-color: black; -fx-border-width: 1px;");

                    if (username.equals(msg.getFromUser().getNickname())) {
                        wrapper.setAlignment(Pos.TOP_RIGHT);
                        wrapper.getChildren().addAll(msgLabel, nameLabel);
                        msgLabel.setPadding(new Insets(0, 20, 0, 0));
                    } else {
                        wrapper.setAlignment(Pos.TOP_LEFT);
                        wrapper.getChildren().addAll(nameLabel, msgLabel);
                        msgLabel.setPadding(new Insets(0, 0, 0, 20));
                    }

                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    setGraphic(wrapper);
                }
            };
        }
    }
    //实现一个Listview的单元格工厂显示聊天室内正在与client聊天的所有用户
    private class UserCellFactory implements Callback<ListView<User>, ListCell<User>> {
        @Override
        public ListCell<User> call(ListView<User> param) {
            return new ListCell<User>() {

                @Override
                public void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);
                    if (empty || Objects.isNull(user)) {
                        return;
                    }

                    HBox wrapper = new HBox();
                    Label nameLabel = new Label(user.getNickname());
                    Label msgLabel = new Label(user.getNickname());

                    nameLabel.setPrefSize(50, 20);
                    nameLabel.setWrapText(true);
                    nameLabel.setStyle("-fx-border-color: black; -fx-border-width: 1px;");

                    if (username.equals(user.getNickname())) {
                        wrapper.setAlignment(Pos.TOP_RIGHT);
                        wrapper.getChildren().addAll(msgLabel, nameLabel);
                        msgLabel.setPadding(new Insets(0, 20, 0, 0));
                    } else {
                        wrapper.setAlignment(Pos.TOP_LEFT);
                        wrapper.getChildren().addAll(nameLabel, msgLabel);
                        msgLabel.setPadding(new Insets(0, 0, 0, 20));
                    }

                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    setGraphic(wrapper);
                }
            };
        }
    }
    //setchatContentList
    public void setChatContentList(Message msg) {
        System.out.println("setChatContentList");
        chatContentList.getItems().add(msg);
    }
}
