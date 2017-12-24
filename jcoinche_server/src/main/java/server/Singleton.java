package server;

import java.io.IOException;

public class                        Singleton {
    private static Singleton        instance = null;
    private apacheminaConnection    connection;

    protected Singleton() { }

    public static Singleton getInstance() {
        if(instance == null) {
            System.out.println("--------> Singleton Created<--------");
            instance = new Singleton();
            instance.connection = new apacheminaConnection();
            try {
                instance.connection.start();
            }
            catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
        return (instance);
    }
}
