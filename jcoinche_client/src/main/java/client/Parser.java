package client;

import com.google.gson.Gson;
import org.apache.mina.core.session.IoSession;

public class Parser
{
    public void parse(String message, IoSession session)
    {
        if (message.contains("/playerinfo"))
        {
            if (Player.partnerId == -1 && Player.myId == -1)
                System.out.println("Actually not in game");
            else
                System.out.println("Now playing, your ID is : " + Player.myId.toString() + "\nPartner ID is : "
                        + Player.partnerId.toString());
        }
        else if (message.contains("/hand"))
            Player.hand.dump();
        else if (message.contains("/fold"))
            Handler._game.dumpFold();
        else if (message.contains("/help"))
            displayHelp();
        else if (message.contains("/bid"))
            handleBid(message, session);
        else if (message.contains("/pass"))
            handlePass(message, session);
        else if (message.contains("/play"))
            handlePlay(message, session);
        else
        {
            System.err.println("Unknow command, use /help to have further informations");
            session.write(message);
        }
    }

    private void handlePlay(String message, IoSession session) {
        if (Player._state == Player.state.player_turn &&
                Handler._game._state == Game.gameState.playing)
        {
            Card card = Player.hand.getCardByString(message);
            if (card == null)
            {
                System.err.println("An error occured, please use /help to have further informations");
            }
            else {
                Player.hand.deleteCardByCard(card);
                Gson gson = new Gson();
                session.write("0011: " + gson.toJson(card));
                Player._state = Player.state.other_turn;
                System.out.println("Playing this card ...");
                Handler._game.addCardtoFold(card);
            }
        }
        else
        {
            System.err.println("Not playing or not your turn");
        }
    }

    private void handlePass(String message, IoSession session) {
        if (Player._state == Player.state.player_turn) {
            if (Handler._game._state == Game.gameState.binding) {
                System.out.println("Passing");
                session.write("0012");
                Player._state = Player.state.other_turn;
            }
            else
                System.err.println("Actually not in game or not binding");
        }
        else
            System.err.println("Not your turn");
    }

    private void handleBid(String message, IoSession session)
    {
        String[] splited = message.split(" ");
        Integer bid = 0;
        if (Handler._game._state == Game.gameState.binding) {
            if (Player._state == Player.state.player_turn) {
                if (splited.length < 3)
                    System.err.println("Not enought argument, please use /help to have further informations");
                else {
                    if (!splited[1].contains("spade") && !splited[1].contains("heart")
                            && !splited[1].contains("club") && !splited[1].contains("spike"))
                        System.err.println("Bad argument (color), please use /help to have further informations");
                    else {
                        try {
                            bid = Integer.parseInt(splited[2]);
                        } catch (NumberFormatException e) {
                            System.err.println("Bad argument (number expected as second argument), " +
                                    "please use /help to have further informations");
                            return;
                        }
                        if ((bid <= 80 || bid >= 160) && bid <= Handler._game.lastBid) {
                            System.err.println("Bad bid (must be between 80 and 160 and must be superior to last bid");
                        } else {
                            session.write("0010: " + splited[1] + " " + splited[2]);
                            Player._state = Player.state.other_turn;
                            Handler._game.lastBid = Integer.parseInt(splited[2]);
                        }
                    }
                }
            }
            else
                System.err.println("Not your turn");
        }
        else
            System.err.println("Not playing or not in binding phase");
    }

    private void displayHelp()
    {
        System.out.println("Here is the list of command");
        System.out.println("General command :");
        System.out.println("[/help] : Run the information command");
        System.out.println("In game command :");
        System.out.println("[/playinfo] : Get your ID and your partner ID");
        System.out.println("[/hand] : Get the card in your hand");
        System.out.println("[/fold] : Get the card that have been played this round");
        System.out.println("[/bid [color] [amount]] : In binding phase, bid the [amount (80 <= x  <= 160)]  for the" +
                "[color (spade - heart - club - pike)]");
        System.out.println("[/play [color] [value]] : Play a card");
        System.out.println("[/pass] : In binding phase, pass your turn");
    }
}
