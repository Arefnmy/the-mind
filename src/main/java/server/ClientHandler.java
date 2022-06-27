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
        try {
            sendMessage(MessageType.AUTH_TOKEN , String.valueOf(authToken));
            sendMessage(MessageType.NAME, "Enter your name : ");
            Message getName = gson.fromJson(reader.readUTF() , Message.class);
            String name = getName.getMessage();
            human = new Human(authToken , name);
            server.addToLobby(human);
            //waiting
            if (isHost)
                hostHandler();

            new Thread(
                    () ->{
                            try {
                                while (true) {
                                    Message message = gson.fromJson(reader.readUTF(), Message.class);
                                    if (message.getAuthToken() == authToken) {
                                        switch (message.getMessageType()) {
                                            case STATUS:
                                                synchronized (game.getGameStatus().getHistory()) {
                                                    List<String> gameHistory = game.getGameStatus().getHistory();
                                                    if (!gameHistory.isEmpty()) {
                                                        List<String> history = gameHistory.subList(lastIndexOfHistory + 1, gameHistory.size());
                                                        lastIndexOfHistory = gameHistory.size() - 1;
                                                        for (String s : history) {
                                                            sendMessage(MessageType.HISTORY, s);
                                                        }
                                                        if (!history.isEmpty()) {
                                                            sendMessage(MessageType.STATUS, "Cards : " + human.getCards());
                                                            sendMessage(MessageType.STATUS,
                                                                    "Cards on table :" + game.getGameStatus().getPlayedCards());
                                                        }
                                                    }
                                                }
                                                break;
                                            case PLAY_CARD:
                                                String cardStr = message.getMessage();
                                                if (cardStr.equals("NINJA"))
                                                    server.playCardNinja(authToken);
                                                else if (cardStr.equals("NUMBER"))
                                                    server.playCard(authToken);
                                                break;
                                            case START_GAME:
                                                server.startGame();
                                                break;
                                            case REACTION:
                                                String reaction = human.getName() + ": ";
                                                for (String s : Server.emojis) {
                                                    if (message.getMessage().contains(s)) {
                                                        reaction += s + " "; //todo
                                                    }
                                                }
                                                server.sendToAll(game, MessageType.REACTION, reaction);
                                        }
                                    } else
                                        sendMessage(MessageType.AUTH_TOKEN, "Wrong Auth Token!");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }
            ).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startGame(Game game) {
        try {
            sendMessage(MessageType.GAME_STARTED , "Game Started.");
            this.game = game;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void hostHandler() {
        isHost = true;
        try {
            sendMessage(MessageType.NUMBER_OF_PLAYER,
                    "Enter number of other players : ");
            Message getNumberOfPlayer = gson.fromJson(reader.readUTF() , Message.class);
            int numberOfPlayer = Integer.parseInt(getNumberOfPlayer.getMessage());
            server.setCapacity(numberOfPlayer + 1);

            sendMessage(MessageType.START_GAME , "Start game by writing anything");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Human getHuman(){
        return human;
    }

    public void sendMessage(MessageType messageType , String message) throws IOException {
        writer.writeUTF(gson.toJson(new Message(messageType , message)));
    }

    public void closeSocket(boolean isWinner) throws IOException {
        sendMessage(MessageType.GAME_FINISHED ,
                isWinner ? "You wined the game!" : "Oops! You lost the game.");
        socket.close();
    }
}
