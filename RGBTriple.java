import java.awt.Color;
public class RGBTriple
{
    // instance variables - replace the example below with your own
    private int R;
    private int G;
    private int B;
    
    /**
     * Constructor for objects of class RGBTriple
     */
    public RGBTriple(int R, int G, int B)
    {
        this.R = R;
        this.G = G;
        this.B = B;
    }
    
    public int getRed()
    {
        return R;
    }

    public void setRed(int R)
    {
        this.R = R;
    }
    
    
    public int getGreen()
    {
        return G;   
    }
    
    public void setGreen(int G)
    {
        this.G = G;
    }

    public int getBlue()
    {
        return B;
    }

    public void setBlue(int B)
    {
        this.B = B;
    }
    
    public Color getColor()
    {
        return new Color(R,G,B);
    }
    
}
