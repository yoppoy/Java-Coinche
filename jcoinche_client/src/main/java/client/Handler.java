package client;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Handler extends IoHandlerAdapter {
    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
    private final String values;
    private Player gamer = new Player();
    public static Game _game = new Game();
    private boolean finished;

    public Handler(String values)
    {
        this.values = values;
    }

    public boolean isFinished()
    {
        return finished;
    }

    @Override
    public void sessionOpened(IoSession session)
    {
        session.write(values);
    }

    @Override
    public void messageReceived(IoSession session, Object message)
    {
        gamer.handleMessage(session, message);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
    {
        session.close();
    }
}