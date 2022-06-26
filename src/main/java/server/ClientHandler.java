package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import game.Game;
import game.Human;
import message.Message;
import message.MessageType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Gson gson;

    private final Socket socket;
    private final DataInputStream reader;
    private final DataOutputStream writer;
    private final int authToken;
    private final Server server;
    private Game game;
    private Human human;
    private boolean isHost;
    private int lastIndexOfHistory;

    public ClientHandler(Socket socket , int authToken , Server server , boolean isHost) throws IOException {
        gson = new GsonBuilder().create();

        this.socket = socket;
        this.authToken = authToken;
        this.server = server;
        this.isHost = isHost;
        reader = new DataInputStream(socket.getInputStream());
        writer = new DataOutputStream(socket.getOutputStream());

        lastIndexOfHistory = -1;
    }

    @Override
    public void run() {
        try {//handel in while(true) todo
            writer.writeUTF(gson.toJson(new Message(MessageType.AUTH_TOKEN, String.valueOf(authToken))));
            writer.writeUTF(gson.toJson(new Message(MessageType.NAME, "Enter your name : ")));
            Message getName = gson.fromJson(reader.readUTF() , Message.class);
            String name = getName.getMessage();
            human = new Human(authToken , name);
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

                                    if (message.getMessageType() == MessageType.STATUS)
                                        getStatusHandler();

                                    if (message.getMessageType() == MessageType.PLAY_CARD){
                                        String cardStr = message.getMessage();
                                        if (cardStr.equals("NINJA"))
                                            server.playCardNinja(authToken);
                                        else if (cardStr.equals("NUMBER"))
                                            server.playCard(authToken);
                                    }
                                }
                                else
                                    writer.writeUTF(gson.toJson(new Message(MessageType.AUTH_TOKEN, "Wrong Auth Token!")));
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

    public void startGame(Game game) {
        try {
            writer.writeUTF(gson.toJson(new Message(MessageType.GAME_STARTED , "Game Started.")));
            writer.writeUTF(gson.toJson(new Message(MessageType.STATUS, "Cards : " + human.getCards())));
            this.game = game;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void hostHandler() {
        isHost = true; //todo
        try {
            writer.writeUTF(gson.toJson(new Message(MessageType.NUMBER_OF_PLAYER,
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

    public synchronized void getStatusHandler() throws IOException {
        List<String> gameHistory = game.getGameStatus().getHistory();
        if (!gameHistory.isEmpty()){
            List<String> history = gameHistory.subList(lastIndexOfHistory + 1 , gameHistory.size());
            lastIndexOfHistory = gameHistory.size() - 1;
            for (String s : history){
                writer.writeUTF(gson.toJson(new Message(MessageType.HISTORY, s)));
            }
            if (!history.isEmpty()){
                writer.writeUTF(gson.toJson(
                        new Message(MessageType.STATUS,
                                "Cards : " + human.getCards())));
                writer.writeUTF(gson.toJson(
                        new Message(MessageType.STATUS,
                                "Cards on table :" + game.getGameStatus().getPlayedCards())));
            }
        }
    }
}
