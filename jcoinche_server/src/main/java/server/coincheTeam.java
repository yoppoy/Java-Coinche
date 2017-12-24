package server;

import org.apache.mina.core.session.IoSession;

import java.util.ArrayList;

public class                            coincheTeam implements ITeam {
    private int                         score = 0;
    private int                         scoreRound;
    private ArrayList<coinchePlayer>    players = new ArrayList<coinchePlayer>();

    public boolean                      addPlayer(IPlayer player)
    {
        if (players.size() == 2)
            return (false);
        players.add((coinchePlayer)player);
        return (true);
    }

    public boolean                      deletePlayer(IoSession session)
    {
        for (int i = 0; i < players.size(); i++)
        {
            if (players.get(i).getSession() == session)
            {
                players.remove(i);
                return (true);
            }
        }
        return (false);
    }

    public coinchePlayer                getPlayer(IoSession session)
    {
        for (int i = 0; i < players.size(); i++)
        {
            if (players.get(i).getSession() == session)
            {
                return (players.get(i));
            }
        }
        return (null);
    }

    public int                          getPlayerIndex(IoSession session)
    {
        for (int i = 0; i < players.size(); i++)
        {
            if (players.get(i).getSession() == session)
            {
                return (i);
            }
        }
        return (-1);
    }

    public coinchePlayer                getPlayer(int num)
    {
        return (players.get(num));
    }

    public void                         resetDeck()
    {
        players.get(0).resetDeck();
        players.get(1).resetDeck();
    }

    public void                         startRound()
    {
        scoreRound = 0;
    }

    public void resetPlayed()
    {
        players.get(0).setPlayed(false);
        players.get(1).setPlayed(false);
        players.get(0).setPlayedCard(null);
        players.get(1).setPlayedCard(null);
    }

    public void                         endRound()
    {
        score += scoreRound;
    }

    public void                         sendStart()
    {
        IoSession                       tmp;

        tmp = players.get(0).getSession();
        tmp.write("0004: " + tmp.getId() + ";" + players.get(1).getSession().getId());
        tmp = players.get(1).getSession();
        tmp.write("0004: " + tmp.getId() + ";" + players.get(0).getSession().getId());
    }

    public void                         sendMessage(String message)
    {
        IoSession                       tmp;

        players.get(0).getSession().write(message);
        players.get(1).getSession().write(message);
    }

    public void                         sendEnd()
    {
        if (players.get(0).getSession().isConnected())
            players.get(0).getSession().write("0000");
        if (players.get(1).getSession().isConnected())
            players.get(1).getSession().write("0000");
    }

    public int                          getPlayerCount()
    {
        return (players.size());
    }

    public void                         appendScore(int tmp)
    {
            score += tmp;
    }

    public int                          getScore()
    {
        return (score);
    }

    public int                          getScoreRound()
    {
        return (scoreRound);
    }


}