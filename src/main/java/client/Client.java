package client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import game.Player;
import message.Message;
import message.MessageType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable {
    private final Gson gson;

    private int authToken;
    private final Socket socket;
    private final DataInputStream reader;
    private final DataOutputStream writer;
    private final Scanner scanner;

    public Client(String host , int port) throws IOException {
        socket = new Socket(host , port);
        reader = new DataInputStream(socket.getInputStream());
        writer = new DataOutputStream(socket.getOutputStream());
        scanner = new Scanner(System.in);

        gson = new GsonBuilder().create();
    }

    @Override
    public void run() {
        try {
            Message getAuthToken = gson.fromJson(reader.readUTF() , Message.class);
            authToken = Integer.parseInt(getAuthToken.getMessage());
            System.out.println("Auth token : " + authToken);

            new Thread(
                    () ->{
                        while (true) {
                            try {
                                Message get = gson.fromJson(reader.readUTF(), Message.class);
                                System.out.println(get.getMessage());

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            ).start();

            new Thread(
                    () ->{
                        while (true) {
                            try {//todo
                                /*int authToken = scanner.nextInt();
                                String messageType = scanner.next();
                                String message = scanner.next();*/
                                writer.writeUTF(gson.toJson(new Message(authToken, MessageType.PLAY_CARD, scanner.nextLine())));

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            ).start();

            /*new Thread( //ping todo
                    () -> {
                        try {
                            Thread.sleep(100);
                            writer.writeUTF(gson.toJson(new Message(authToken , MessageType.GET_STATUS , null)));
                        } catch (InterruptedException | IOException e) {
                            e.printStackTrace();
                        }
                    }
            ).start();*/


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
