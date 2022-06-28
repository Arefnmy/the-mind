package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;

public class ServerMain {
    private int port;

    public ServerMain(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws IOException {
        /*Server server = new Server(8000);
        server.accept();*/

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ServerMain serverMain = new ServerMain(8000);
        String json = gson.toJson(serverMain);
        FileWriter fileWriter = new FileWriter("src/main/resources/server.json");
        fileWriter.write(json);
    }
}
