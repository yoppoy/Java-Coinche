package server;

import java.util.ArrayList;
import java.util.Collections;

public class                coincheDeck
{
    private ArrayList<Card> cards = new ArrayList<Card>();

    public void             fill()
    {
        for (int i = 0; i < 32; ++i)
        {
            cards.add(new Card(i));
        }
        shuffle();
    }

    public void             receiveCard(Card card)
    {
        cards.add(card);
    }

    public void             shuffle()
    {
        Collections.shuffle(cards);
    }

    public Card             distribute() {
        return (cards.remove(0));
    }

    public Card             get(int a) { return cards.get(a); }

    public void             clear()
    {
        cards.clear();
    }

    public ArrayList<Card>  getList()
    {
        return cards;
    }
}
