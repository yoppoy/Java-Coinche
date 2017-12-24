package server;

public class CardColor {
    private String [] colors = {"spade", "heart", "spike", "club"};
    private String color = null;


    public String getColor()
    {
        return color;
    }

    public void setColor(int nb)
    {
        if (nb < 8)
            color = colors[0];
        else if (nb < 16)
            color = colors[1];
        else if (nb < 24)
            color = colors[2];
        else if (nb < 32)
            color = colors[3];
    }

    public void setColor(String Color)
    {
        if (Color.equals(colors[0]))
            color = colors[0];
        else if (Color.equals(colors[1]))
            color = colors[1];
        else if (Color.equals(colors[2]))
            color = colors[2];
        else if (Color.equals(colors[3]))
            color = colors[3];
        else
            System.err.printf("Error : invalid color : -%s-\n", Color);
    }
}
