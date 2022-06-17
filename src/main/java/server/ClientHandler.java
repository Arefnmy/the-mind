package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import game.Game;
import game.Human;
import game.Player;
import message.Message;
import message.MessageType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Gson gson;

    private final Socket socket;
    private final DataInputStream reader;
    private final DataOutputStream writer;
    private final int authToken;
    private final Server server;
    private boolean isHost;

    public ClientHandler(Socket socket , int authToken , Server server , boolean isHost) throws IOException {
        gson = new GsonBuilder().create();

        this.socket = socket;
        this.authToken = authToken;
        this.server = server;
        this.isHost = isHost;
        reader = new DataInputStream(socket.getInputStream());
        writer = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try {//handel in while(true) todo
            writer.writeUTF(gson.toJson(new Message(MessageType.GET_AUTH_TOKEN , String.valueOf(authToken))));
            writer.writeUTF(gson.toJson(new Message(MessageType.GET_NAME , "Enter your name : ")));
            Message getName = gson.fromJson(reader.readUTF() , Message.class);
            String name = getName.getMessage();
            Human human = new Human(authToken , name);
            server.addToLobby(human);
            //waiting
            if (isHost)
                hostHandler();

            new Thread(
                    () ->{
                        while (true){
                            try {
                                Message message = gson.fromJson(reader.readUTF(), Message.class);
                                if(message.getAuthToken() == authToken) {

                                    if (message.getMessageType() == MessageType.GET_STATUS) {

                                    }
                                    //todo
                                }
                                else
                                    writer.writeUTF(gson.toJson(new Message(MessageType.GET_AUTH_TOKEN , "Wrong Auth Token!")));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
            ).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startGame() {
        try {
            writer.writeUTF(gson.toJson(new Message(MessageType.GAME_STARTED , "Game Started.")));
            //todo
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void hostHandler() {
        isHost = true; //todo
        try {
            writer.writeUTF(gson.toJson(new Message(MessageType.GET_NUMBER_OF_PLAYER ,
                    "Enter number of other players : ")));
            Message getNumberOfPlayer = gson.fromJson(reader.readUTF() , Message.class);
            int numberOfPlayer = Integer.parseInt(getNumberOfPlayer.getMessage());
            server.setCapacity(numberOfPlayer + 1);

            writer.writeUTF(gson.toJson(new Message(MessageType.START_GAME , "Start game by writing anything")));
            reader.readUTF(); //todo invalid input
            server.startGame();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
