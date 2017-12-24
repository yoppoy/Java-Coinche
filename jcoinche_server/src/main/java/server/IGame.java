package server;

import org.apache.mina.core.session.IoSession;

public interface    IGame {
    public int      playersLeft();
    public void     addPlayer(IPlayer added);
    public boolean  deletePlayer(IoSession session);
    public IPlayer  getPlayer(IoSession session);
    public void     acceptCommand(String command, IoSession session);
    public boolean  getStart();
    public void     start();
    public void     end();
}
