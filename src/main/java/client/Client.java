package client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import message.Message;
import message.MessageType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable {
    private final Gson gson;

    private String authToken;
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

        Thread ping = new Thread(
                    () -> {
                        try {
                            while (true) {
                                Thread.sleep(100);
                                writer.writeUTF(gson.toJson(new Message(authToken, MessageType.STATUS, null)));
                            }
                        } catch (InterruptedException | IOException e) {
                            e.printStackTrace();
                        }
                    }
            );

            new Thread(
                    () -> {
                        while (true) {
                            try {
                                Message message = gson.fromJson(reader.readUTF(), Message.class);
                                switch (message.getMessageType()){
                                    case GET_AUTH_TOKEN:
                                        authToken = message.getMessage();
                                        System.out.println("Auth token : " + authToken);
                                        break;
                                    case STATUS:
                                        System.out.println("\u001B[33m" + message.getMessage() + "\u001B[0m");
                                        break;
                                    case GAME_FINISHED:
                                        //socket.close();
                                        System.out.println(message.getMessage());
                                        return;
                                    case GAME_STARTED:
                                        ping.start();
                                    default:
                                        System.out.println(message.getMessage());
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            ).start();

            new Thread(
                    () ->{
                        while (true) {
                            try {
                                String authToken = scanner.next();
                                String messageTypeValue = scanner.next();
                                MessageType messageType = MessageType.valueOf(messageTypeValue);
                                String text = scanner.next();
                                Message message = new Message(authToken , messageType , text);
                                writer.writeUTF(gson.toJson(message));

                            } catch (IOException /*| IllegalArgumentException*/ ignored) {
                                //ignore
                            }
                        }
                    }
            ).start();

    }
}
