package client;

import com.google.gson.Gson;
import org.apache.mina.core.session.IoSession;

public class Player {
    static public Integer myId = -1;
    static public Integer partnerId = -1;
    public enum state {
        idle,
        connected,
        waiting,
        binding,
        player_turn,
        other_turn,
        disconnected
    }

    public static state _state = state.idle;

    public static PlayerHand hand = new PlayerHand();

    public state get_state()
    {
        return _state;
    }

    public Integer getmyId()
    {
        return myId;
    }

    public Integer getpartnerId()
    {
        return partnerId;
    }

    public void setMyId(Integer id)
    {
        myId = id;
    }

    public void setPartnerId(Integer id)
    {
        partnerId = id;
    }

    public void handleMessage(IoSession session, Object message)
    {
        handleSpecialCase(message);
        handlePlayerTurn(message);
        handleInfoMessage(message);
        //Handle Starting state
        if (_state != state.other_turn && _state != state.player_turn)
            handleState(message, session);
    }

    private void handleInfoMessage(Object message)
    {
        if (message.toString().contains("7778"))
            handlePassPlayer(message.toString());
        else if (message.toString().contains("7777"))
            handleBidPlayer(message.toString());
        else if (message.toString().contains("8888"))
        {
            System.out.println("Everybody has passed, redistribution of the cards");
            Player.hand.deleteHand();
            Handler._game.lastBid = -1;
        }
        else if (message.toString().contains("0088"))
            handleCardPlayer(message.toString());
        else if (message.toString().contains("0006"))
            handleScoreDisplaying(message.toString());
        else if (message.toString().contains("0077:"))
            handleEndofRoundDisplay(message.toString());
    }

    private void handleEndofRoundDisplay(String s) {
        String[] splited = s.split(" ");
        String[] score = splited[1].split(";");
        Integer scoreTeam1 = Integer.parseInt(score[0]);
        Integer scoreTeam2 = Integer.parseInt(score[1]);

        System.out.println("End of the round, here are the score :");
        System.out.println("Team 1 : " + scoreTeam1.toString());
        System.out.println("Team 2 : " + scoreTeam2.toString());
    }

    private void handleScoreDisplaying(String s)
    {
        String[] splited = s.split(" ");

        System.out.println("Score of the fold : " + splited[1]);
    }

    private void handleCardPlayer(String s) {
        String[] splited = s.split(" ");

        if (splited.length >= 2)
        {
            String[] splited2 = splited[1].split(";");
            Integer otherId = Integer.parseInt(splited2[0]);
            Gson gson = new Gson();
            Card played = gson.fromJson(splited2[1], Card.class);
            if (otherId.equals(partnerId))
                System.out.println("Your partner " + otherId.toString() + " has played " + played.color.getColor() + " - "
                        + played.value.getValue());
            else
                System.out.println("Other player " + otherId.toString() + " has played " + played.color.getColor() + " - "
                        + played.value.getValue());
            Handler._game.addCardtoFold(played);
        }
    }


    private void handleBidPlayer(String s)
    {
        String[] splited = s.split(" ");
        Integer otherId = -1;
        if (splited.length >= 4)
        {
            otherId = Integer.parseInt(splited[1]);
            if (otherId.equals(partnerId))
                System.out.println("Your partner " + otherId.toString() + " has bided on " + splited[2] + " for "
                        + splited[3] + " points");
            else
                System.out.println("Other player " + otherId.toString() + " has bided on " + splited[2] + " for "
                        + splited[3] + " points");
            Handler._game.lastBid = Integer.parseInt(splited[3]);
        }
    }

    private void handlePassPlayer(String s)
    {
        String[] splited = s.split(" ");
        Integer otherId = -1;

        if (splited.length >= 2)
        {
            otherId = Integer.parseInt(splited[1]);
            if (otherId.equals(partnerId))
                System.out.println("Your partner " + otherId.toString() + " has passed his turn");
            else
                System.out.println("Player " + otherId.toString() + " has passed his turn");
        }
    }

    private void handleState(Object message, IoSession session)
    {
        if (Player._state == Player.state.idle && message.toString().contains("0002")) {
            session.write("0002 OK");
            Player._state = Player.state.connected;
        }
        else if (Player._state == Player.state.waiting && message.toString().contains("0004")) {
            session.write("0002 OK");
            getIdandPartnerId(message.toString());
            Player._state = Player.state.binding;
            Handler._game._state = Game.gameState.binding;
        }
        else if (Player._state == Player.state.connected &&
                (message.toString().contains("0004") || message.toString().contains("0044")))
        {
            session.write("0002 OK");
            if (message.toString().contains("0004"))
            {
                getIdandPartnerId(message.toString());
                Player._state = Player.state.binding;
                Handler._game._state = Game.gameState.binding;
            }
            else if (message.toString().contains("0044"))
                Player._state = Player.state.waiting;
        }
        else
        {
            session.write("0003 Error");
            System.exit(84);
        }

    }

    private void handlePlayerTurn(Object message)
    {
        if ((message.toString().contains("0007")))
        {
            /*Integer round = Integer.getInteger(message.toString().split(" ")[1]);
            System.out.println("Round " + round.toString() + " start");*/
            System.out.println("Round start");
            Player._state = state.other_turn;
            Handler._game._state = Game.gameState.binding;
        }
        else if (message.toString().contains("0077"))
        {
            System.out.println("End of round");
            Player._state = state.other_turn;
            Handler._game._state = Game.gameState.binding;
            Player.hand.deleteHand();
            Handler._game.deleteFold();
        }
        else if ((message.toString().contains("0020"))) {
            System.out.println("It's your turn");
            Player._state = state.player_turn;
        }
    }

    private void handleSpecialCase(Object message)
    {
        if (message.toString().contains("0000"))
        {
            System.out.println("Now exiting the game");
            System.exit(84);
        }
        else if (message.toString().contains("0008"))
            hand.addCardToHand(message.toString());
        else if (message.toString().contains("7000")) {
            System.out.println("Binding is now over, starting the round");
            Handler._game._state = Game.gameState.playing;
            Handler._game.lastBid = 0;
        }
    }

    private void getIdandPartnerId(String msg)
    {
        String[] splited = msg.split(" ");
        String[] Ids = splited[1].split(";");
        setMyId(Integer.parseInt(Ids[0]));
        setPartnerId(Integer.parseInt(Ids[1]));
        System.out.println("Now playing, your ID is : " + getmyId().toString() + "\nPartner ID is : "
                + getpartnerId().toString());
    }
}
