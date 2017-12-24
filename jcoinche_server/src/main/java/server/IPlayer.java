package server;

import org.apache.mina.core.session.IoSession;

public interface                IPlayer {
    public IoSession            getSession();
    public void                 distributeCard(Card card);
}
