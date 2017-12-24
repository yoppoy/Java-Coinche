package server;

import java.io.IOException;

public class Server {
    private apacheminaConnection    connection;

    public void                  run()
    {
        connection = new apacheminaConnection();
        try {
            connection.start();
        }
        catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
