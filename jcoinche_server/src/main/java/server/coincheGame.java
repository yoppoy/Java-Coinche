package server;

import com.google.gson.Gson;
import org.apache.mina.core.session.IoSession;

public class coincheGame implements IGame {
    private coincheTeam     teams[] = new coincheTeam[2];
    private boolean         end = false;
    private int             roundBid;
    private coinchePlayer   roundBidder;
    private int             bidPass;
    private char            roundState = 3;
    private int             roundNumber = 0;
    private CardColor       roundColor = null;
    private coincheDeck     roundDeck = new coincheDeck();
    private int             playNumber;
    private coinchePlayer   currentPlayer;

    public                  coincheGame()
    {
        teams[0] = new coincheTeam();
        teams[1] = new coincheTeam();
    }

    public void             start()
    {
        System.out.println("---> A new GAME has STARTED");
        teams[0].sendStart();
        teams[1].sendStart();
        startRound();
    }

    public void             end()
    {
        teams[0].sendEnd();
        teams[1].sendEnd();
        end = true;
        System.out.println("---> GAME ENDED<---");
    }

    public void             startRound()
    {
        roundState = 3;
        System.out.println("------> A new ROUND has STARTED");
        sendMessage("0007: " + roundNumber);
        teams[0].startRound();
        teams[1].startRound();
        playNumber = 0;
        roundBid = 0;
        roundDeck.fill();
        distributeDeck();
        startBidding();
    }

    public void             endRound()
    {
        teams[0].endRound();
        teams[1].endRound();
        roundNumber++;
        System.out.printf("------> Round OVER : %d - %d\n", teams[0].getScore(), teams[1].getScore());
        sendMessage("0077: " + teams[0].getScore() + ";" + teams[1].getScore());
        if ((teams[0].getScore() >= 500 || teams[1].getScore() >= 500) || roundNumber == 8)
        {
            if (teams[0].getScore() > teams[1].getScore())
            {
                teams[0].sendMessage("0777: 1");
                teams[1].sendMessage("0777: 0");
            }
            else
            {
                teams[0].sendMessage("0777: 0");
                teams[1].sendMessage("0777: 1");
            }
            end();
        }
        else
            startRound();
    }

    public void             startBidding()
    {
        System.out.println("---------> Bidding STARTS");
        roundState = 0;
        bidPass = 0;
        teams[0].resetPlayed();
        teams[1].resetPlayed();
        roundBid = 0;
        roundBidder = null;
        roundColor = new CardColor();
        currentPlayer = (coinchePlayer)teams[0].getPlayer(0);
        currentPlayer.getSession().write("0020");
    }

    public void             endBidding()
    {
        currentPlayer = (coinchePlayer)teams[0].getPlayer(0);
        if (roundBidder == null)
        {
            System.out.println("------> Re-shuffling");
            sendMessage("8888");
            teams[0].resetDeck();
            teams[1].resetDeck();
            roundDeck.clear();
            roundDeck.fill();
            roundDeck.shuffle();
            distributeDeck();
            startBidding();
        }
        else
        {
            System.out.printf("---------> Bidding : %s\n", roundColor.getColor());
            sendMessage("7000");
            startPlay();
        }
    }

    public void             distributeDeck()
    {
        for (int i = 0; i < 8; i++)
        {
            distributeCard(teams[0].getPlayer(0));
            distributeCard(teams[1].getPlayer(0));
            distributeCard(teams[0].getPlayer(1));
            distributeCard(teams[1].getPlayer(1));
        }
    }

    public void             distributeCard(IPlayer player)
    {
        Card                distributed;
        Gson                gson = new Gson();

        distributed = roundDeck.distribute();
        player.distributeCard(distributed);
        player.getSession().write("0008: " + gson.toJson(distributed));
    }

    public void             acceptCommand(String cmd, IoSession session)
    {
        String              code;

        System.out.println("---------> Accepting Command : " + cmd);
        code = cmd.substring(0, 4);
        if (roundState != 3)
        {
            switch (code)
            {
                case "0010" :
                    acceptBid(cmd, (coinchePlayer)getPlayer(session));
                    break;
                case "0012" :
                    acceptBid(null, (coinchePlayer)getPlayer(session));
                    break;
                case "0011" :
                    acceptCard(cmd.substring(6), (coinchePlayer)getPlayer(session));
            }
        }
    }

    public void             acceptBid(String bid, coinchePlayer player)
    {
        String[]            arr = null;

        if (bid != null)
            arr = bid.split(" ");
        if (currentPlayer == player && roundState == 0 &&
                bid == null || (arr.length == 3))
        {
            if (bid != null)
            {
                /*if (Integer.parseInt(arr[2]) <= roundBid)
                {
                    currentPlayer.getSession().write("0003");
                    return;
                }*/
                System.out.printf("Setting round Color %s\n", arr[1]);
                roundColor.setColor(arr[1]);
                roundBid = Integer.parseInt(arr[2]);
                roundBidder = player;
                sendToAllExcept("7777: " + currentPlayer.getSession().getId() + " " + arr[1] + " " + arr[2]);
                currentPlayer.setBid(true);
            }
            else
            {
                sendToAllExcept("7778: " + currentPlayer.getSession().getId());
                bidPass++;
            }
            currentPlayer.setPlayed(true);
            if ((roundBidder != null && bidPass == 3) || bidPass == 4)
                endBidding();
            else
            {
                updateCurrentPlayer();
                while (currentPlayer.hasBid() == false && currentPlayer.hasPlayed() == true)
                    updateCurrentPlayer();
                currentPlayer.getSession().write("0020");
            }
        }
    }

    public void             startPlay()
    {
        System.out.println("---------> Starting Play");
        teams[0].resetPlayed();
        teams[1].resetPlayed();
        roundState = 1;
        currentPlayer.getSession().write("0020");
    }

    public void             endPlay()
    {
        playNumber++;
        currentPlayer = getPlayWinner();
        updateRoundScore();
        if (playNumber == 8)
            endRound();
        else
            startPlay();
    }

    public void                 acceptCard(String cardData, coinchePlayer player)
    {
        Card                    card;

        if (player == currentPlayer && roundState == 1)
        {
            /*TODO Check that card is valid
            * if card not valid
            */
            Gson gson = new Gson();
            card = gson.fromJson(cardData, Card.class);
            roundDeck.receiveCard(card);
            sendToAllExcept("0088: " + currentPlayer.getSession().getId() + ";" + cardData);
            currentPlayer.setPlayedCard(card);
            currentPlayer.setPlayed(true);
            if (updateCurrentPlayer() == false)
                endPlay();
            else
                currentPlayer.getSession().write("0020");
        }
    }

    public boolean          updateCurrentPlayer()
    {
       int                  team;
       int                  index;
       int                  new_team;
       int                  new_index;

       team = getPlayerTeam(currentPlayer.getSession());
       index = getPlayerIndex(currentPlayer.getSession());
       if (teams[team].getPlayer(index).hasPlayed() == true)
       {
           new_team = (team == 0) ? 1 : 0;
           if ((index == 0 && team == 1) || (index == 1 && team == 0))
               new_index = 1;
           else
               new_index = 0;
           System.out.printf("New player Team:%d Index: %d\n", new_team, new_index);
           currentPlayer = teams[new_team].getPlayer(new_index);
           if (teams[new_team].getPlayer(new_index).hasPlayed() == true)
               return (false);
       }
       return (true);
    }

    public void             updateRoundScore()
    {
        int                 points [] = {0, 0, 0, 5, 2, 3, 4, 19};
        int                 pointsSuit[] = {0, 0, 9, 5, 14, 1, 3, 6};
        Card                tmp;
        int                 score;
        int                 index;

        score = 0;
        for (int i = 0; i < 2; i++)
        {
            for (int j = 0; j < 2; j++)
            {
                tmp = teams[i].getPlayer(j).getPlayedCard();
                if (tmp.getColor() == roundColor.getColor())
                    score += pointsSuit[tmp.getValue() - 7];
                else
                    score += points[tmp.getValue() - 7];
            }
        }
        System.out.printf("---------> Round winner : %d\n", getPlayWinner().getSession().getId());
        System.out.printf("--------->       Card   : %s %d\n", teams[0].getPlayer(0).getPlayedCard().getColor(), teams[0].getPlayer(0).getPlayedCard().getValue());
        System.out.printf("--------->       Card   : %s %d\n", teams[0].getPlayer(1).getPlayedCard().getColor(), teams[0].getPlayer(1).getPlayedCard().getValue());
        System.out.printf("--------->       Card   : %s %d\n", teams[1].getPlayer(0).getPlayedCard().getColor(), teams[1].getPlayer(0).getPlayedCard().getValue());
        System.out.printf("--------->       Card   : %s %d\n", teams[1].getPlayer(1).getPlayedCard().getColor(), teams[1].getPlayer(1).getPlayedCard().getValue());
        index = getPlayerTeam(getPlayWinner().getSession());
        teams[index].sendMessage("0006: " + score);
        teams[(index == 0) ? 1 : 0].sendMessage("0006: " + score);
        teams[index].appendScore(score);
        //TODO : UPDATE SCORE ACCORDING TO WINNER
    }

    public void             sendMessage(String message)
    {
        teams[0].sendMessage(message);
        teams[1].sendMessage(message);
    }

    public void             sendToAllExcept(String message)
    {
        int index = getPlayerIndex(currentPlayer.getSession());
        int team = getPlayerTeam(currentPlayer.getSession());
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++)
                if (i != index || j != team)
                    teams[j].getPlayer(i).getSession().write(message);
    }

    public int              playersLeft()
    {
        return (4 - (teams[0].getPlayerCount() + teams[1].getPlayerCount()));
    }


    public boolean          isBigger(Card one, Card two)
    {
        if (one.getColor().equals(roundColor.getColor()) && !one.getColor().equals(two.getColor()))
            return (true);
        else if (one.getColor().equals(roundColor.getColor()) && one.getColor().equals(two.getColor()))
           return ((one.getValue() > two.getValue()) ? true : false);
        else if (one.getColor().equals(two.getColor()))
            return ((one.getValue() > two.getValue()) ? true : false);
        return (false);
    }


    public void             addPlayer(IPlayer added)
    {
        if (teams[0].addPlayer(added) == false)
        {
            teams[1].addPlayer(added);
            System.out.println("Player added to team 1");
        }
        else
            System.out.println("Player added to team 0");
        if (playersLeft() != 0)
            added.getSession().write("0044");
        else
            start();
    }

    public boolean          deletePlayer(IoSession session)
    {
        System.out.println("Deleting player");
        if (roundState == 3)
        {
            System.out.println("Player disconnected : Deleting player");
            if (teams[0].deletePlayer(session) == false)
                teams[1].deletePlayer(session);
            return (false);
        }
        else
            end();
        return (true);
    }

    public IPlayer          getPlayer(IoSession session)
    {
        IPlayer             tmp;

        if ((tmp = teams[0].getPlayer(session)) == null)
            return (teams[1].getPlayer(session));
        return (tmp);
    }

    public int              getPlayerIndex(IoSession session)
    {
        int                 tmp;

        if ((tmp = teams[0].getPlayerIndex(session)) == -1)
            return (teams[1].getPlayerIndex(session));
        return (tmp);
    }

    public int              getPlayerTeam(IoSession session)
    {
       if ((teams[0].getPlayer(session)) == null)
            return (1);
        return (0);
    }

    public coinchePlayer    getPlayWinner()
    {
        coinchePlayer       tmp;
        coinchePlayer       winner;

        winner = teams[0].getPlayer(0);
        tmp = teams[0].getPlayer(1);
        if (isBigger(tmp.getPlayedCard(), winner.getPlayedCard()))
            winner = teams[0].getPlayer(1);
        for (int i = 0; i < 2; i++)
        {
            tmp = teams[1].getPlayer(i);
            if (isBigger(tmp.getPlayedCard(), winner.getPlayedCard()))
                winner = teams[1].getPlayer(i);
        }
        return (winner);
    }

    public boolean          getStart()
    {
        if (roundState == 3)
            return (false);
        return (true);
    }
}
