package server;

import game.*;
import message.Message;

import javax.xml.transform.sax.SAXSource;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.*;

public class Server {
    private final ServerSocket serverSocket;
    private final SecureRandom random;
    private final List<Game> gamaList;
    private final Map<Game , List<Human>> gameMap;
    private final List<Human> lobby;
    private final List<ClientHandler> clientHandlerList;
    private int capacity;

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
            ClientHandler clientHandler = new ClientHandler(socket , random.nextInt() , this , lobby.isEmpty()); //generate todo
            clientHandlerList.add(clientHandler);

            new Thread(clientHandler).start();
        }
    }

    public void setCapacity(int capacity){
        this.capacity = capacity;
    }

    public void startGame() {
        List<Human> humanList = new LinkedList<>(lobby.subList(0 , Math.min(capacity , lobby.size())));
        int numberOfBot = capacity - humanList.size();

        List<Player> playerList = new LinkedList<>(humanList);
        List<Bot> botList = new ArrayList<>();
        for (int i = 1; i <= numberOfBot ; i++) {
            Bot bot = new Bot("Bot" + i);
            botList.add(bot);
            playerList.add(bot);
        }

        Game game = new Game(botList , playerList);

        gameMap.put(game , humanList);
        gamaList.add(game);

        game.nextLevel();
        for (Bot b : botList){
            b.startGame(game);
        }

        List<ClientHandler> clientHandlers = new LinkedList<>(clientHandlerList.subList(0 , humanList.size()));
        for (ClientHandler c : clientHandlers){
            c.startGame(game);
        }

        lobby.removeAll(humanList);
        clientHandlerList.removeAll(clientHandlers);

        if (!clientHandlerList.isEmpty())
            clientHandlerList.get(0).hostHandler(); //handel before start game todo
    }

    public void addToLobby(Human player){
        lobby.add(player);
    }

    public void playCard(int token){//todo
        for (Game g : gamaList){
            for (Human h : gameMap.get(g)){
                if (h.getToken() == token){
                    int playCard = h.playCard();
                    g.play(h , playCard);
                }
            }
        }
    }

    public void playCardNinja(int token){
        System.out.println("Play ninja");
        for (Game g : gamaList){
            for (Human h : gameMap.get(g))
                if (h.getToken() == token)
                    g.playNinja(h);
        }
    }
}
