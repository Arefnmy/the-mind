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
    private boolean isHost;
    private final Server server;

    public ClientHandler(Socket socket , int authToken , Server server , boolean isHost) throws IOException {
        gson = new GsonBuilder().create();

        this.socket = socket;
        this.authToken = authToken;
        this.isHost = isHost;
        this.server = server;
        reader = new DataInputStream(socket.getInputStream());
        writer = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            writer.writeUTF(gson.toJson(new Message(MessageType.GET_AUTH_TOKEN , String.valueOf(authToken))));
            writer.writeUTF(gson.toJson(new Message(MessageType.GET_NAME , "Enter your name : ")));
            Message getName = gson.fromJson(reader.readUTF() , Message.class);
            String name = getName.getMessage();
            Human human = new Human(authToken , name , isHost);
            server.addToLobby(human);
            if (isHost){
                writer.writeUTF(gson.toJson(new Message(MessageType.GET_NUMBER_OF_PLAYER ,
                        "Enter number of other players : ")));
                Message getNumberOfPlayer = gson.fromJson(reader.readUTF() , Message.class);
                int numberOfPlayer = Integer.parseInt(getNumberOfPlayer.getMessage());
                server.setCapacity(numberOfPlayer + 1);

                writer.writeUTF(gson.toJson(new Message(MessageType.START_GAME , "Start game by writing anything")));
                reader.readUTF(); //todo invalid input
                server.startGame();
            }

            while (true){
                Message message = gson.fromJson(reader.readUTF(), Message.class);
                if (message.getMessageType() == MessageType.GET_STATUS){

                }
                System.out.println(message.getMessage()); //todo
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startGame() {
        try {
            writer.writeUTF(gson.toJson(new Message(MessageType.START_GAME , "Game Started.")));
            //todo
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
