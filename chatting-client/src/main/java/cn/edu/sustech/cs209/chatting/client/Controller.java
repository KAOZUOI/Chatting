package cn.edu.sustech.cs209.chatting.client;

import cn.edu.sustech.cs209.chatting.common.*;
import cn.edu.sustech.cs209.chatting.common.ClientSockets;
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

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static cn.edu.sustech.cs209.chatting.client.ChatItem.chatList;
import static cn.edu.sustech.cs209.chatting.common.UserManager.UserIOMap;
import static java.util.stream.Collectors.toList;

public class Controller implements Initializable {
    String ip = "localhost";
    int port = 52209;
    @FXML
    ListView<Message> chatContentList;
    String username;
    DBManager dbManager = new DBManager();
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
        if (input.isPresent() && !input.get().isEmpty()) {
            /*
               TODO: Check if there is a user with the same name among the currently logged-in users,
                     if so, ask the user to change the username
             */
            username = input.get();
            AtomicBoolean usernameValid = new AtomicBoolean(false);
            while (!usernameValid.get()) {
                System.out.println(username);
                boolean userOnline = ClientSockets.checkUserNameOnline(username);
                if (userOnline) {
                    cancelButton.setOnAction(event -> usernameValid.set(false));
                    dialog.setTitle("Alert");
                    dialog.setHeaderText("User already Login.");
                    Optional<String> newInput = dialog.showAndWait();
                    if (newInput.isPresent() && !newInput.get().isEmpty()){
                        username = newInput.get();
                    }
                    else{
                        System.out.println("Invalid username " + input + ", exiting");
                        usernameValid.set(false);
                        Platform.exit();
                    }
                } else {
                    User user = new User(username, inputPassword);
                    dbManager.addUser(user);
                    Socket clientSocket = null;
                    try {
                        clientSocket = new Socket(ip, port);
                        UserIO currentClientIO = new UserIO(
                            new ObjectInputStream(clientSocket.getInputStream()),
                            new ObjectOutputStream(clientSocket.getOutputStream()));
                        UserManager.UserIOMap.put(user,currentClientIO);
                    } catch (Exception e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setContentText("Fail to connect");
                        alert.showAndWait();
                    }

                    ClientSockets.bindSocket(user, clientSocket);

                    usernameValid.set(true);
                }
            }
        } else {
            System.out.println("Invalid username " + input + ", exiting");
            Platform.exit();
        }

        chatContentList.setCellFactory(new MessageCellFactory());
    }

    @FXML
    public void createPrivateChat() {
        AtomicReference<String> user = new AtomicReference<>();

        Stage stage = new Stage();
        ComboBox<String> userSel = new ComboBox<>();

        //get the user list from server, the current user's name should be filtered out
        List<String> filteredList = ClientSockets.getUserList().stream()
            .filter(name -> !name.equals(username))
            .collect(toList());
        userSel.getItems().addAll(filteredList);

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
        boolean itemExist = false;
        for (ChatItem chatItem : ChatItem.chatList) {
            if (chatItem.getTitle().equals(user.get())) {
                itemExist = true;
            }
        }
        if (!itemExist){
            ChatItem newChatItem = new ChatItem(user.get());
            chatList.add(newChatItem);
        }

        //if the current user already chatted with the selected user, just open the chat with that user
        //otherwise, create a new chat item in the left panel, the title should be the selected user's name
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
        // TODO
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
}
