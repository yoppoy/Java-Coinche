package server;

public class CardValue {
    private Integer[] values = {7, 8, 9, 10, 11, 12, 13, 14};
    private Integer value = -1;

    public void setValue(int nb)
    {
        value = values[nb % 8];
    }

    public Integer getValue()
    {
        return value;
    }
}
