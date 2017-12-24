package client;

import java.io.IOException;

public class Client
{
    private static final int PORT = 8000;


    public static void main(String[] args) throws IOException, InterruptedException
    {
        Connexion con = new Connexion();
        con.run(PORT);
    }
}