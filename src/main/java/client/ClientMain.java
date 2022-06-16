package client;

import java.io.IOException;

public class ClientMain {
    public static void main(String[] args) throws IOException {
        Client client = new Client("localhost" , 8000);
        new Thread(client).start();
    }
}
