package client;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ClientMain {
    private final static String path = "src/main/resources/client.properties";

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(path));
        int port = Integer.parseInt(properties.getProperty("port"));
        String host = properties.getProperty("host");

        Client client = new Client(host , port);
        new Thread(client).start();

    }
}
