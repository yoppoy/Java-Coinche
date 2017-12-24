package client;

import com.google.gson.Gson;

import java.util.ArrayList;

public class Game {
    private ArrayList<Card> _fold = new ArrayList<Card>();
    public enum gameState
    {
        non_playing,
        binding,
        playing
    }

    public gameState _state = gameState.non_playing;
    public Integer lastBid = -1;

    public ArrayList<Card> get_fold()
    {
        return _fold;
    }

    public void set_fold(ArrayList<Card> _fold)
    {
        this._fold = _fold;
    }

    public void addCardtoFold(Card card)
    {
        _fold.add(card);
    }

    public void deleteFold()
    {
        _fold.clear();
    }

    public void dumpFold()
    {
        if (_fold.isEmpty())
            System.out.println("Fold is empty");
        else
        {
            for (Card anAl : _fold) {
                anAl.dump();
            }
        }
    }

    public void jsonToFold(String msg)
    {
        /*String[] splited = msg.split(" ");
        Gson gson = new Gson();
        Card card = gson.fromJson(splited[1], Card.class);
        addCardtoFold(card);*/
    }
}
