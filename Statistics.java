package acoroutine;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * @author Evan Harris
 */
public class Statistics extends JPanel
{
    //Setup tour data structures
    private int tours[];
    private int tourLengths[][];
    private int tempWorstTourLength;
    private int nnTour, xScale, ants[], nnDepth[];
    private boolean elitist[], bestACO[];
    private float evap[];
    private int bestTourLength[], worstTourLength[], globalBest, globalWorst;
    private int runCounter;
    private int viewCounter;
    //Setup graph constants
    private final int width = 600;
    private final int height = 500;
    private final int topPadding = 35;
    private final int bottomPadding = 140;
    private final int leftPadding = 40;
    private final int rightPadding = 0;
    private final int yHeight = height-bottomPadding-topPadding;
    private final int xWidth = width-leftPadding-rightPadding;
    private final int ySpacing = 10;
    private JButton viewNext, viewPrevious;
    private JPanel buttons;
    
    public Statistics()
    {   
        tourLengths = new int[1000][100];
       
        runCounter=0;
        viewCounter=runCounter;
        //this.setBounds(100,100,width,height);     
        
        ants = new int[100];
        evap = new float[100];
        tours = new int[100];
        nnDepth = new int[100];
        elitist = new boolean[100];
        bestACO = new boolean[100];
        bestTourLength = new int[100];
        worstTourLength = new int[100];
        
        viewNext = new JButton("View Next Iteration");
        viewNext.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent arg0) 
            {
                if(viewCounter == runCounter) {}
                else{ viewCounter++; repaint();}
            }
        });
        viewPrevious = new JButton("View Previous Iteration");
        viewPrevious.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent arg0) 
            {
                if(viewCounter == 1) {}
                else{ viewCounter--; repaint();}
            }
        });
        
        
        this.add(viewPrevious);
        this.add(viewNext);
        
    }
    
    public void newTour(int ants, int nnDepth, float evap, boolean elitist, boolean bestACO)
    {
        runCounter++;
        viewCounter = runCounter;
        tours[runCounter]=0;
        this.ants[runCounter] = ants;
        
        this.nnDepth[runCounter] = nnDepth;
        this.evap[runCounter] = evap;
        this.elitist[runCounter] = elitist;
        this.bestACO[runCounter] = bestACO;
        
    }
    
    public void addNNTour(int tourLength)
    {
        nnTour = tourLength;
    }
    
    public void addTour(int tourLength)
    {
        tourLengths[runCounter][tours[runCounter]] = tourLength;
        if(tourLength < bestTourLength[runCounter] || bestTourLength[runCounter] == 0) bestTourLength[runCounter] = tourLength;
        if(tourLength > worstTourLength[runCounter] || worstTourLength[runCounter] == 0) worstTourLength[runCounter] = tourLength;
        if(tourLength < globalBest || globalBest == 0) globalBest = tourLength;
        if(tourLength > globalWorst || globalWorst == 0) globalWorst = tourLength;
        tours[runCounter] = tours[runCounter]+1;
    }
    
    public int getTours()
    {
        return tours[runCounter];
    }
    
    public void drawGraph(Graphics2D g2)
    {
        //Draw Graph Boundaries
        g2.drawLine(leftPadding, topPadding, leftPadding, height-bottomPadding);
        g2.drawLine(leftPadding, height-bottomPadding, width, height-bottomPadding);
        
        //Draw lines along the x axis representing the tours
        for (int i = 0; i < tours[viewCounter]; i++) 
        {
            g2.drawLine(leftPadding+(xScale*i),height-bottomPadding,leftPadding+(xScale*i),height-bottomPadding+10);
        }
        
        //Scale and draw markers on the y axis representing the tour lengths
        for (int i = 0; i <= ySpacing; i++) 
        {
            int yPosition = (height-bottomPadding)-(((tempWorstTourLength/ySpacing)*i)*(height-topPadding-bottomPadding)/(tempWorstTourLength));
            g2.drawLine(leftPadding,yPosition,leftPadding-6,yPosition);
            g2.drawString((tempWorstTourLength/ySpacing)*i+"", 2, yPosition+8);
        }
         //Draw graph labels
        g2.drawString("Tour Length", 2, 30);
        g2.drawString("Iterations", width/2, height-bottomPadding+20);
                
    }
    
    public void plotGraph(Graphics2D g2)
    {
        //Plot points relating to each iteration of the tour
        for (int i = 0; i < tours[viewCounter]; i++) 
        {
            int yPosition = (height-bottomPadding)-((tourLengths[viewCounter][i]*(height-topPadding-bottomPadding))/(tempWorstTourLength));

            g2.fillOval((i*xScale)+leftPadding-2,yPosition-2, 4, 4);
            //g2.drawLine(60+(xScale*i),360,60+(xScale*i),370);
        }
        
        //Connect the dots
        for (int i = 0; i < tours[viewCounter]-1; i++)
        {
            int yPositioni = (height-bottomPadding)-((tourLengths[viewCounter][i]*(height-topPadding-bottomPadding))/(tempWorstTourLength));
            int yPositionNext = (height-bottomPadding)-((tourLengths[viewCounter][i+1]*(height-topPadding-bottomPadding))/(tempWorstTourLength));
            g2.drawLine(leftPadding+(xScale*i), yPositioni, leftPadding+(xScale*(i+1)), yPositionNext);
        }
        
        //Draw a horizontal line representing the nearest neighbour tour and label
        int yPosition = (height-bottomPadding)-(nnTour*(height-topPadding-bottomPadding)/(tempWorstTourLength));
        g2.drawString("nnLength", width-54, yPosition-3);
        g2.drawLine(leftPadding,yPosition,width,yPosition);
 
    }
    
    public void outputInfo(Graphics2D g2)
    {
        g2.drawString("Total Iterations:\t" + tours[viewCounter], 12, height-bottomPadding+topPadding);
        g2.drawString("Best ACO Tour:\t" + bestTourLength[viewCounter], 12, height-bottomPadding+topPadding+13);
        g2.drawString("Worst ACO Tour:\t" + worstTourLength[viewCounter], 12, height-bottomPadding+topPadding+26);
        g2.drawString("NN Tour:\t" + this.nnTour, 12, height-bottomPadding+topPadding+39);
        g2.drawString("Pheromone Evaporation Constant:\t" + this.evap[viewCounter], 12, height-bottomPadding+topPadding+52);
        if(bestTourLength[viewCounter]<nnTour) g2.drawString("The best ACO tour is better than Nearest Neighbour by a length of " + (nnTour-bestTourLength[viewCounter]), 12, height-bottomPadding+topPadding+65);
        else g2.drawString("The best ACO tour is worse than Nearest Neighbour by a length of " + (bestTourLength[viewCounter]-nnTour), 12, height-bottomPadding+topPadding+65);
        if(elitist[viewCounter]) g2.drawString("Elitist Pheromone Update is Enabled", 12, height-bottomPadding+topPadding+78);
        g2.drawString("Currently displaying graph " + viewCounter + " of " + runCounter, 12, height-bottomPadding+topPadding+91);
    }
    
    public void drawCounterControls()
    {
        if(viewCounter >= runCounter) viewNext.setEnabled(false);
        else viewNext.setEnabled(true);
        if(viewCounter <= 1) viewPrevious.setEnabled(false);
        else viewPrevious.setEnabled(true);
        viewNext.updateUI();
        viewPrevious.updateUI();
        
    }
    
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        //this.setBounds(100,100,width,height);      

        drawCounterControls();
        
        tempWorstTourLength = worstTourLength[viewCounter];
        if(nnTour > worstTourLength[viewCounter]) tempWorstTourLength = nnTour;
        //Calculate the space between each point onbest the x axis
        try
        {
            xScale = (width-leftPadding)/tours[viewCounter];
        }
        catch(ArithmeticException a)
        {
            xScale = 0;
        }
        Graphics2D g2 = (Graphics2D)g;
        //drawCounterControls();
        
        drawGraph(g2);
        plotGraph(g2);
        outputInfo(g2);
        
    }
    
    public Dimension getPreferredSize() 
    {
        return new Dimension(width,height);
    }
}
