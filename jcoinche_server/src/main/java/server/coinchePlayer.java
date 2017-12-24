package server;

import org.apache.mina.core.session.IoSession;

public class            coinchePlayer implements IPlayer {
    private IoSession   session;
    private coincheDeck deck = new coincheDeck();
    private boolean     bid = false;
    private boolean     played;
    private Card        playedCard = null;

    public              coinchePlayer(IoSession added)
    {
        session = added;
    }

    public void         distributeCard(Card card)
    {
        deck.receiveCard(card);
    }

    public IoSession    getSession()
    {
        return (session);
    }

    public void         resetDeck()
    {
        deck.clear();
    }

    public void         setPlayedCard(Card card)
    {
        playedCard = card;
    }

    public void         setPlayed(boolean state)
    {
        played = state;
    }

    public boolean hasPlayed()
    {
        return (played);
    }

    public void         setBid(boolean state)
    {
        bid = state;
    }

    public boolean hasBid()
    {
        return (bid);
    }

    public Card     getPlayedCard() { return (playedCard); }
}
