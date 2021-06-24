package acoroutine;

/**
 *
 * @author Evan Harris
 */
public class City 
{
    private int x, y, id;
    
    /** Creates a new instance of City */
    public City(int id, int x, int y, int cityCount) 
    {
        this.id = id;
        this.x = x;
        this.y = y;
    }
    
    //Define position get methods.
    public int getx(){return x;}
    public int gety(){return y;}
    public int getid(){return id;}
    
}
