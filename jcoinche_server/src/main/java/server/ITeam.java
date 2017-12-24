package server;

import org.apache.mina.core.session.IoSession;

public interface        ITeam {
    public boolean     addPlayer(IPlayer player);
    public int         getPlayerCount();
    public IPlayer     getPlayer(IoSession session);
    public IPlayer     getPlayer(int num);
    public void        sendStart();
    public void        sendEnd();
    public void        sendMessage(String message);
    public boolean     deletePlayer(IoSession session);
}
