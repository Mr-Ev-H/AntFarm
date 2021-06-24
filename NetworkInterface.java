package acoroutine;
 
import java.awt.*;
import java.awt.event.*;
import javax.swing.*; 

/**
 * @author Evan Harris
 */
public class NetworkInterface extends javax.swing.JPanel
{
    private Network nt;
    private City acoArray[];
    private float pheromone[][];
    private City nnArray[];
    private City bestACOTour[];
    private int bestACOTourLength;
    private Statistics stat;
    private boolean nnRun = false;
    private boolean acoTourRun = false;
    private boolean viewNN = true;
    private boolean viewBestAco = false;
    private int acoTourLength;
    private int cityNumber;
    private int nnTourLength;
    private JFrame statistics;
    
    
    public NetworkInterface(int cityNumber)
    {
        statistics = new JFrame("Tour Statistics");
        stat = new Statistics();        
        statistics.add(stat);
        statistics.pack();
        statistics.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        
        
        this.cityNumber = cityNumber;
        nt = new Network(cityNumber, 590);
        
        acoArray = new City[1];
        nnArray = new City[1];
        nnTourLength = 0;
        setPreferredSize(new Dimension(600,680));
                
        setVisible(true);
        this.repaint();

        runNN();
        stat.addNNTour(nnTourLength);

    }
    
    public int runACO(int ants, int nnDepth, int solutions, float evap,JProgressBar progress, boolean elitist, boolean bestACO)
    {
        //Initialise a new statistics controller
        stat.newTour(ants, nnDepth, evap, elitist, bestACO);
        
        //Initialise the ACO routine
        nt.acoRoutine(ants, nnDepth, solutions, evap, elitist);
        
        //Setup the progress bar
        progress.setMaximum(solutions-1);
        progress.setValue(0);
        progress.setString("Generating Solutions");
                
        //Setup view parameters
        viewBestAco = bestACO;
                
        //Iterate for the required period
        for (int i = 0; i < solutions; i++) 
        {
            //Update progress bar
            progress.setValue(i);
            progress.update(progress.getGraphics());
            
            //Tick the aco routine and get the best constructed tour along with all relevant matrix references
            acoArray = nt.acoTick();
            pheromone = nt.getACOPheromone();
            acoTourLength = nt.getACOTourLength();
            acoTourRun = true;
            
            //Update the best tour length
            if(bestACOTourLength == 0 || acoTourLength < bestACOTourLength)
            {
                bestACOTourLength = acoTourLength;
                bestACOTour = acoArray;
            }
            //Update the graphics
            this.paint(this.getGraphics());
            
            //Update the statistics frame
            stat.addTour(acoTourLength);
            
            
            //Suggest to the runtime that it should think about considering the possibility that it ought perhaps get prepared to initialise the procedure for pontificating over the garbage collector.
            System.gc();
                    
        }
        //Update the progress bar
        progress.setString("Completed");
        progress.update(progress.getGraphics());
        
        //Display the statistics frame
        stat.repaint();
        statistics.setVisible(true);
        
        //Return the last acoTour Length
        return acoTourLength;
    }
    
    public void runNN()
    {
        //Run the nearestNeighbour routine, get the nn data and update the flag
        nt.nearestNeighbour();
        nnArray = nt.getNearestNeighbourRoute();
        nnTourLength = nt.getNNTourLength();
        nnRun = true;
    }
    
    public void paint(Graphics g)
    {
      
        Graphics2D g2 = (Graphics2D)g;
        //Clear the screen
        g2.clearRect(0, 0, this.getWidth(), this.getHeight());
        
        //Check to see whether the nnTour should be drawn
        if(nnRun && viewNN)
        {
            //Draw the tour
            for (int i = 0; i < nnArray.length-1; i++)
            {
                City firstCity = nnArray[i];
                City tempCity = nnArray[i+1];
                //Set stroke geometry
                g2.setColor(Color.GRAY);
                BasicStroke nnStroke = new BasicStroke(10.0f);
                g2.setStroke(nnStroke);
                //Connect the dots
                g2.drawLine(firstCity.getx(), firstCity.gety(), tempCity.getx(), tempCity.gety());
            } 
            //Print the nnLength
            g2.setColor(Color.BLACK);
            g2.drawString("Nearest Neighbour Tour Length: " + nnTourLength, 10, 640);
        }
        //Check to see whether aco has been run
        if(acoTourRun)
        {
            //Iterate over the tour
            for (int i = 0; i < acoArray.length-1; i++)
            {
                //Setup temp variables
                City firstCity;
                City tempCity;
                
                //If bestAco is selected choose only those cities which exist within the best tour array
                if(viewBestAco) {firstCity = bestACOTour[i]; tempCity = bestACOTour[i+1];}
                //Otherwise get the previous tour
                else{firstCity = acoArray[i]; tempCity = acoArray[i+1];}
                
                //Setup stroke geometry
                BasicStroke connect = new BasicStroke(2.0f);
                g2.setStroke(connect);
                g2.setColor(new Color(0,0,0));
                //Connect the dots
                g2.drawLine(firstCity.getx(), firstCity.gety(), tempCity.getx(), tempCity.gety());
            } 
        }
        
        //Iterate over the network 
        for (int i = 0; i < nt.getCityCount(); i++)
        {
            //Draw each city
            g2.setColor(Color.BLACK);
            City cit = nt.getCityNo(i);
            g2.fillOval(cit.getx()-5, cit.gety()-5,10,10);
            
            g2.drawString(String.valueOf(cit.getid()),cit.getx()+6,cit.gety()+2);
        }
        //Reset colour
        g2.setColor(Color.BLACK);
    }
    
    public void regenNetwork(int cities)
    {
        statistics.remove(stat);
        stat = new Statistics();
        statistics.add(stat);
        statistics.pack();
        
        //Assign nt to a new network
        nt = new Network(cities, 590);
        //Clear all data structures
        acoArray = new City[cityNumber];
        acoTourRun = false;
        bestACOTour = null;
        bestACOTourLength = 0;
        nnRun = false;
        //Run the nearest neighbour
        runNN();
        stat.addNNTour(nnTourLength);
        //Redraw
        this.paint(this.getGraphics());
        
        
    }
    
    public void toggleNN()
    {
        if(viewNN) viewNN = false;
        else viewNN = true;
        this.paint(this.getGraphics());
    }
}