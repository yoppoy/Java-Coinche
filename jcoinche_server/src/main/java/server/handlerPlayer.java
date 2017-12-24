package server;

import org.apache.mina.core.session.IoSession;

public class handlerPlayer {
    private IoSession       session;
    private IGame           game;

    public                  handlerPlayer(IoSession newSession, IGame newGame)
    {
        session = newSession;
        game = newGame;
    }

    public void             setGame(IGame change)
    {
        game = change;
    }

    public void             setSession(IoSession change)
    {
        session = change;
    }

    public IoSession        getSession() {
        return (session);
    }

    public IGame            getGame() {
        return (game);
    }
}
