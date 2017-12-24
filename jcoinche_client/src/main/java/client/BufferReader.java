package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Runnable;
import org.apache.mina.core.session.IoSession;

public class BufferReader implements Runnable {

    IoSession session = null;
    Parser parse = new Parser();

    public BufferReader(IoSession sess)
    {
        session = sess;
    }

    public void run()
    {
        BufferedReader br = null;
        br = new BufferedReader(new InputStreamReader(System.in));
        String input = null;
        while (Player._state != Player.state.disconnected) {
            try {
                input = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error on stdin");
            }
            if ("q".equals(input) || input == null) {
                System.out.println("Exit!");
                System.exit(0);
            }
            parse.parse(input, session);
        }
    }
}
