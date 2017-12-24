package server;

public class Card {
    CardColor       color = new CardColor();
    CardValue       value = new CardValue();

    public          Card(int nb)
    {
        color.setColor(nb);
        value.setValue(nb);
    }

    public void     dump()
    {
        String val = value.getValue().toString();
        if (val.contains("11"))
            val = "Jack";
        else if (val.contains("12"))
            val = "Queen";
        else if (val.contains("13"))
            val = "King";
        else if (val.contains("14"))
            val = "Ace";
        System.out.println("[" + color.getColor() + "] - [" + val + "]");
    }

    public int      getValue()
    {
        return (value.getValue());
    }

    public String      getColor()
    {
        return (color.getColor());
    }
}