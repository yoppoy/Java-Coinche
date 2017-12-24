package client;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

import com.google.gson.Gson;

public class PlayerHand
{
    private ArrayList<Card> al = new ArrayList<Card>();

    public void addCard(Card card)
    {
        System.out.println("New Card receive");
        card.dump();
        al.add(card);
    }

    public void deleteHand()
    {
        al.clear();
    }

    public void dump()
    {
        if (al.isEmpty())
            System.out.println("No card in your hand");
        else {
            for (Card anAl : al) {
                anAl.dump();
            }
        }
    }

    public void addCardToHand(String msg)
    {
        String[] splited = msg.split(" ");
        Gson gson = new Gson();
        Card card = gson.fromJson(splited[1], Card.class);
        addCard(card);
    }

    public Card getCardByString(String scard)
    {
        String[] splited = scard.split(" ");
        Integer value = -1;
        if (splited.length < 3)
            return null;
        else
        {
            if (!splited[1].equals("spade") && !splited[1].equals("heart")
                    && !splited[1].equals("club") && !splited[1].equals("spike")) {
                System.err.println("Color is not good (must be equal to spade, heart, club, spike)");
                return null;
            }
            if ((splited[2].equals("Ace") || splited[2].equals("ace")))
                value = 14;
            else if ((splited[2].equals("Jack") || splited[2].equals("jack")))
                value = 11;
            else if ((splited[2].equals("Queen") || splited[2].equals("queen")))
                value = 12;
            else if ((splited[2].equals("King") || splited[2].equals("king")))
                value = 13;
            else
                value = Integer.parseInt(splited[2]);
            if (value < 7 && value > 14) {
                System.err.println("Value is not good (must be between 7 and 14 (or equal to Queen/queen, Jack/Jack, " +
                        "King/king, Ace/ace)");
                return (null);
            }
            for (Card anAl : al) {
                if (anAl.color.getColor().equals(splited[1])
                        && anAl.value.getValue().equals(value))
                    return anAl;
            }
            System.err.println("Card " + splited[1] + " - " + splited[2] + " not found in hand");
        }
        return null;
    }

    public void deleteCardByCard(Card card) {
        int index = 0;
        for (Card anAl : al) {
            if (anAl.color.getColor().equals(card.color.getColor())
                    && anAl.value.getValue().equals(card.value.getValue()))
            {
                al.remove(index);
                Collections.shuffle(al);
                break;
            }
            index++;
        }
    }
}
