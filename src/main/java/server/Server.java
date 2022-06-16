package server;

import game.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        clientHandlerList = new ArrayList<>();
        lobby = new ArrayList<>();
        gameMap = new HashMap<>();
    }

    public void accept() throws IOException {
        while (!serverSocket.isClosed()){
            Socket socket = serverSocket.accept();
            System.out.println("Client Accepted.");
            ClientHandler clientHandler = new ClientHandler(socket , random.nextInt() ,
                    this , lobby.isEmpty()); //generate todo
            clientHandlerList.add(clientHandler);

            new Thread(clientHandler).start();
        }
    }

    public void setCapacity(int capacity){
        this.capacity = capacity;
    }

    public void startGame(){
        List<Player> playerList = new ArrayList<>(lobby);
        int numberOfBot = capacity - playerList.size();

        List<Bot> botList = new ArrayList<>();
        for (int i = 1; i <= numberOfBot ; i++) {
            Bot bot = new Bot("Bot" + i);
            botList.add(bot);
            playerList.add(bot);
        }

        Game game = new Game(botList , playerList);

        gameMap.put(game , lobby);
        gamaList.add(game);

        for (Bot b : botList){
            b.startGame(game);
        }
        for (ClientHandler c : clientHandlerList){
            c.startGame();
        }
        lobby.clear();
        clientHandlerList.clear();
    }

    public void addToLobby(Human player){
        lobby.add(player);
    }
}
