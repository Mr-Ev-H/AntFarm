package acoroutine;

/**
 *
 * @author Evan Harris
 */
public class Ant
{
    //Declare Ant data structures according to Dorigo & Stutzle p103.
    private int tour[];
    private int tourLength;
    int tourStep;
    private boolean visited[];
    public int totalCities;
    
    public Ant(int totalCities)
    {
     
        this.totalCities = totalCities;
        tour = new int[totalCities+1];
        for (int i = 0; i < tour.length; i++) 
        {
            tour[i] = 0;
        }

        visited = new boolean[totalCities];
        
        for (int i = 0; i < totalCities; i++) 
        {
            visited[i] = false;
        }

        tourLength = 0;
        tourStep = 0;
        
    }
    //Lines 1 to 5 pg 106
    public void emptyMemory()
    {
        tourLength = 0;
        for (int i = 0; i < visited.length; i++) 
        {
            visited[i] = false;
        }
        for (int i = 0; i < tour.length; i++) 
        {
            tour[i] = 0;
        }
        tourStep = 0;
        
    }
    
    public void setStartCity(int cityID)
    {
        tour[0] = cityID;
        tour[totalCities] = cityID;
        visited[cityID] = true;
        tourStep++;
    }
    
    public boolean setNextCity(int cityID, int stepLength)
    {
        if(visited[cityID])
        {
            return false;
        }
        else
        {
            tour[tourStep] = cityID;
            visited[cityID] = true;
            tourStep++;
            tourLength =  tourLength + stepLength;
            return true;
        }
    }
    
    public int getCity(int step)
    {
        return tour[step];
    }
    
    public boolean hasVisited(int city)
    {
        return visited[city];
    }
    
    public int getLastCity()
    {
        return tour[tourStep-1];
    }
    
    public int getFirstCity()
    {
        return tour[0];
    }
    
    public int getTourLength()
    {
        return tourLength;
    }
    
    public int[] getTour()
    {
        return tour;       
    }
    
    public void addFinalDistance(int distance)
    {
        tourLength += distance;      
    }

}
