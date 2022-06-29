package server;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ServerMain {
    private final static String path = "src/main/resources/server.properties";

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(path));
        int port = Integer.parseInt(properties.getProperty("port"));


        Server server = new Server(port);
        server.accept();

    }
}
