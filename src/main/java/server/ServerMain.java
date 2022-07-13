package server;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ServerMain {
    private final static String path = "src/main/resources/server.properties";
    private final static int defaultPort = 8000;

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(path));

        String portStr = properties.getProperty("port");
        int port = portStr == null ? defaultPort : Integer.parseInt(portStr);


        Server server = new Server(port);
        server.accept();

    }
}
