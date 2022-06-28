package server;

import game.*;
import message.MessageType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.*;

public class Server {
    private final ServerSocket serverSocket;
    private final SecureRandom random;
    private final List<Game> gamaList;
    private final Map<Game , List<ClientHandler>> gameMap;
    private final List<Human> lobby;
    private final List<ClientHandler> clientHandlerList;
    private int capacity;

    public final static List<String> emojis = new ArrayList<>(Arrays.asList(":)" , ":(" , ":|" , ":D"));

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        random = new SecureRandom();
        gamaList = new ArrayList<>();
        clientHandlerList = new LinkedList<>();
        lobby = new ArrayList<>();
        gameMap = new HashMap<>();
    }

    public void accept() throws IOException {
        while (!serverSocket.isClosed()){
            Socket socket = serverSocket.accept();
            System.out.println("Client Accepted.");
            ClientHandler clientHandler = new ClientHandler(socket , String.valueOf(Math.abs(random.nextInt())) ,
                    this , lobby.isEmpty());
            clientHandlerList.add(clientHandler);

            new Thread(clientHandler).start();
        }
    }

    public void setCapacity(int capacity){
        this.capacity = capacity;
    }

    public void startGame() {
        List<Human> humanList = new LinkedList<>(lobby.subList(0 , Math.min(capacity , lobby.size())));
        List<ClientHandler> clientHandlers = new LinkedList<>(clientHandlerList.subList(0 , humanList.size()));
        int numberOfBot = capacity - humanList.size();

        List<Player> playerList = new LinkedList<>(humanList);
        List<Bot> botList = new ArrayList<>();
        for (int i = 1; i <= numberOfBot ; i++) {
            Bot bot = new Bot("Bot" + i);
            botList.add(bot);
            playerList.add(bot);
        }

        Game game = new Game(botList , playerList);

        gameMap.put(game , clientHandlers);
        gamaList.add(game);

        game.nextLevel();
        for (Bot b : botList)
            b.startGame(game);

        for (ClientHandler c : clientHandlers)
            c.startGame(game);

        lobby.removeAll(humanList);
        clientHandlerList.removeAll(clientHandlers);

        if (!clientHandlerList.isEmpty())
            clientHandlerList.get(0).hostHandler();
    }

    public void addToLobby(Human player){
        lobby.add(player);
    }

    public void playCard(String token){
        for (Game g : gamaList){
            for (ClientHandler c : gameMap.get(g)){
                Human h = c.getHuman();
                if (h.getToken().equals(token)){
                    if (!h.getCards().isEmpty()) {
                        int playCard = h.playCard();
                        g.play(h, playCard);
                    }
                }
            }
        }
    }

    public void playCardNinja(String token) throws IOException {
        for (Game g : gamaList){
            for (ClientHandler c : gameMap.get(g)) {
                Human h = c.getHuman();
                if (h.getToken().equals(token))
                    if (!g.playNinja(h)){
                        c.sendMessage(MessageType.STATUS , "There is no Ninja card!");
                    }
            }
        }
    }

    public void sendToAll(Game game ,MessageType messageType, String message) throws IOException {
        for (ClientHandler c : gameMap.get(game)){
            c.sendMessage(messageType , message);
        }
    }

    public void setEndGame(Game game , boolean win) throws IOException {
        for (ClientHandler c : gameMap.get(game)){
            c.closeSocket(win);
        }
    }
}
