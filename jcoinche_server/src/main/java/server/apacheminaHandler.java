package server;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import java.util.ArrayList;

public class                apacheminaHandler extends IoHandlerAdapter
{
    protected volatile                ArrayList<IGame> games = new ArrayList<IGame>();
    protected                         ArrayList<handlerPlayer> players = new ArrayList<handlerPlayer>();

    public IGame            getFreeGame()
    {
        if (games.isEmpty() == true)
            return (null);
        for (int i = 0; i < games.size(); i++)
        {
            if (games.get(i).playersLeft() > 0)
                return (games.get(i));
        }
        return (null);
    }

    public void             addPlayer(IPlayer player, IoSession session)
    {
        IGame               tmp;

        session.write("0002");
        if ((tmp = getFreeGame()) == null)
        {
            System.out.println("New game created !");
            tmp = new coincheGame();
            games.add(tmp);
        }
        tmp.addPlayer(player);
        players.add(new handlerPlayer(session, tmp));
    }

    public IGame            getGamebySession(IoSession session)
    {
        for (int i = 0; i < players.size(); i++)
        {
            if (players.get(i).getSession() == session)
                return (players.get(i).getGame());
        }
        return (null);
    }

    public IPlayer          getPlayerbySession(IoSession session)
    {
        for (int i = 0; i < players.size(); i++)
        {
            if (players.get(i).getSession() == session)
                return (players.get(i).getGame().getPlayer(session));
        }
        return (null);
    }

    @Override
    public void             exceptionCaught( IoSession session, Throwable cause ) throws Exception
    {
        cause.printStackTrace();
    }

    @Override
    public void             messageReceived( IoSession session, Object message ) throws Exception
    {
        String str = message.toString();
        System.out.println("Received ---> " + str);
        parseCommand(str, session);
    }

    public void             parseCommand(String str, IoSession session)
    {
        String              code;

        code = str.substring(0, 4);
        getGamebySession(session).acceptCommand(str, session);
    }

    @Override
    public void             sessionOpened(IoSession session) throws Exception
    {
        System.out.println("New player connected");
        addPlayer(new coinchePlayer(session), session);
    }

    public void             sessionClosed(IoSession session) throws Exception
    {
        System.out.printf("CLIENT CLOSED %s\n", session.getId());
        super.sessionClosed(session);
        if (getGamebySession(session).deletePlayer(session) == true)
        {
            for (int i = 0; i < games.size(); i++)
            {
                if (games.get(i) == getGamebySession(session))
                    games.remove(i);
            }
        }
    }

    @Override
    public void             sessionIdle(IoSession session, IdleStatus status) throws Exception
    { }
}