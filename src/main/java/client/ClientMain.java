package client;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ClientMain {
    private final static String path = "src/main/resources/client.properties";
    private final static int defaultPort = 8000;
    private final static String defaultHost = "localhost";

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(path));

        String portStr = properties.getProperty("port");
        int port = portStr == null ? defaultPort : Integer.parseInt(portStr);

        String hostStr = properties.getProperty("host");
        String host = hostStr == null ? defaultHost : hostStr;

        Client client = new Client(host , port);
        new Thread(client).start();

    }
}
