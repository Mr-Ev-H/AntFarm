/**
 *  AntFarm - Ant Colony Optimisation Toolkit
 *  Copyright (C) 2008  Evan Harris
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package acoroutine;

import java.util.Random;

/**
 *
 * @author Evan Harris
 */

public class AntController
{
    //Define city pointer and ant holding array
    private City cityList[];
    private Ant antList[];
    //Define heuristic matrices
    private float pheromoneMatrix[][], choiceInfoMatrix[][], heuristicMatrix[][];
    private int distanceMatrix[][], nnMatrix[][];
    private float evap;
    private int bestTour[], globalBestTour[];
    private int solutions = 20;
    //Define problem variables
    private int cityCount, tourList, tourLimit, problemScale, globalBestTourLength;
    private int tourLength = 0;
    //The max height/width of the problem space.
    private Random random = new Random();
    private boolean elitist;
    
    
    public AntController(City cityList[], int cityCount, int problemScale, int[][] distanceMatrix, int[][] nnMatrix, float evap, int ants, boolean elitist)
    {
        //Create and assign problem variables
        this.cityCount = cityCount;
        this.cityList = cityList;
        this.evap = evap;
        this.problemScale = problemScale;
        globalBestTourLength = 10000000;
        this.elitist = elitist;
        
        //Initialise problem matrices and data structures
        pheromoneMatrix = new float[cityCount][cityCount];
        choiceInfoMatrix = new float[cityCount][cityCount];
        heuristicMatrix = new float[cityCount][cityCount];
        antList = new Ant[ants];
        this.distanceMatrix = distanceMatrix;
        this.nnMatrix = nnMatrix;
        
        //Construct heuristic matrix.
        for (int i = 0; i < distanceMatrix.length; i++) 
        {
            for (int j = 0; j < distanceMatrix[i].length; j++) 
            {
                if(i==j){ heuristicMatrix[i][j] = 0; }
                else{ heuristicMatrix[i][j] = (1/(float)(distanceMatrix[i][j])); }
            }
        }
        
        //Construct pheromone matrix with a starting weight of 1.0
        for (int i = 0; i < distanceMatrix.length; i++) 
        {
            for (int j = 0; j < distanceMatrix[i].length; j++) 
            {
                 pheromoneMatrix[i][j] = 1; 
            }
        }
        //Construct choiceInfo Matrix
        for (int i = 0; i < choiceInfoMatrix.length; i++) 
        {
            for (int j = 0; j < choiceInfoMatrix[i].length; j++) 
            {
                choiceInfoMatrix[i][j] = pheromoneMatrix[i][j]*heuristicMatrix[i][j];
            }
        }
        for (int i = 0; i < ants; i++) 
        {
            antList[i] = new Ant(cityCount);          
        }
        
    }  
    
    public void constructSolutions()
    {
        //Clear the ants memory
        for (int i = 0; i < antList.length; i++){antList[i].emptyMemory();}
                
        //Assign a random starting city 
        for (int i = 0; i < antList.length; i++){antList[i].setStartCity(random.nextInt(cityCount));}
        
        //Generate a complete tour for each ant.
        for (int i = 1; i < cityList.length; i++) 
        {
            for (int j = 0; j < antList.length; j++){asDecisionRule(i,j);}
        }
        
        
        //Append the ants length with the distance between the last two cities
        for (int i = 0; i < antList.length; i++)
        {
            antList[i].addFinalDistance(distanceMatrix[antList[i].getLastCity()][antList[i].getFirstCity()]);
        }
    }
    
    public void chooseBestNext(int step, int ant)
    {
        //Create temporary variables
        float bestChoiceInfo = 0;
        int previousCity = antList[ant].getCity(step-1);
        int nextCity = cityCount;
        
        //Iterate over every city in the network
        for (int i = 0; i < cityList.length; i++) 
        {
            //Check to see whether the ant has visited this city
            if(!antList[ant].hasVisited(i))
            {
                //Find the city with the best choice info value
                if(choiceInfoMatrix[previousCity][i]>bestChoiceInfo)
                {
                    nextCity = i;
                    bestChoiceInfo = choiceInfoMatrix[previousCity][nextCity];
                }
            }
        }
        
        //Set the next city and the additional tour calculation
        
        try{
            antList[ant].setNextCity(nextCity,distanceMatrix[previousCity][nextCity]);
        }
        catch(ArrayIndexOutOfBoundsException err)
        {
            err.printStackTrace();
            System.err.println("next city:" + nextCity);
            System.err.println("previousCity: " + previousCity);
            System.err.println("nextCity: " + nextCity);
            System.err.println("DM Length: "+ distanceMatrix.length + " " + distanceMatrix[previousCity].length);
        }
    }
    
    public void asDecisionRule(int step, int ant)
    {
        //Get previous city
        int previousCity = antList[ant].getCity(step-1);
        
        //Setup temporary variables
        float sumProb = 0;
        float selectionProbability[] = new float[cityCount];

        //Iterate over the nnMatrix
        for (int i = 0; i < nnMatrix[previousCity].length; i++) 
        {
            int tempCity = nnMatrix[previousCity][i];
            //If the city being looked at is in the ants visited array set probability of this city to be 0
            if(antList[ant].hasVisited(tempCity)){selectionProbability[tempCity] = 0;}
            else 
            {
                //Set the selection probability of the nncity to be i.
                float tempProb = choiceInfoMatrix[previousCity][tempCity];
                selectionProbability[tempCity] = tempProb ;
                //Update the sum
                sumProb+=tempProb;
            }
        }
        
        if(sumProb == 0.0){chooseBestNext(step, ant);}
        else
        {
            float rand = random.nextFloat()*sumProb;
            int counter = 0;
            float prob = selectionProbability[counter];

            while(prob<rand)
            {
                counter++;
                prob += selectionProbability[counter];
            }
            
            //Update ant tour when roulette choice has terminated
            antList[ant].setNextCity(counter, distanceMatrix[previousCity][counter]);
        }
    }
    
    public void pheromoneEvaporate()
    {
        for (int i = 0; i < cityList.length; i++) 
        {
            for (int j = 0; j < cityList.length; j++) 
            {
                pheromoneMatrix[i][j] = evap*pheromoneMatrix[i][j];
            }
        }
    }
   
    public void pheromoneUpdate()
    {
        pheromoneEvaporate();
        for (int i = 0; i < antList.length; i++) 
        {
            depositPheromone(i);            
        }
        computeChoiceInformation();
    }
    
    public void depositPheromone(int antID)
    {
        float pheromoneChange = 1.0f/(float)antList[antID].getTourLength();
        for (int i = 0; i < cityList.length; i++) 
        {
            int start = antList[antID].getCity(i);
            int next = antList[antID].getCity(i+1);
            pheromoneMatrix[start][next] = pheromoneMatrix[start][next]+pheromoneChange;
            pheromoneMatrix[next][start] = pheromoneMatrix[start][next];
        }
    }
    
    public void computeChoiceInformation()
    {
        for (int i = 0; i < choiceInfoMatrix.length; i++) 
        {
            for (int j = 0; j < choiceInfoMatrix[i].length; j++) 
            {
                choiceInfoMatrix[i][j] = pheromoneMatrix[i][j]*heuristicMatrix[i][j];
            }
        }
    }

    public int eucDist(int city1, int city2)
    {
	int DX = cityList[city2].getx()-cityList[city1].getx();
        int DY = cityList[city2].gety()-cityList[city1].gety();
	return (int)Math.sqrt(DX*DX+DY*DY);
    }          
    
    public int getTourLength()
    {
        return tourLength;
    }

    public int[] acsTick() 
    {
        constructSolutions();
        updateBestSolution();  
        pheromoneUpdate();
        return bestTour;
    }
    
    public void updateBestSolution()
    {
        int minTour = antList[0].getTourLength();
        int bestAnt = 0;
        for (int i = 1; i < antList.length; i++) 
        {
            if(antList[i].getTourLength() < minTour) 
            {
                minTour = antList[i].getTourLength();
                bestAnt = i;
            }            
        }
        bestTour = antList[bestAnt].getTour();
        
        if(minTour < globalBestTourLength)
        {
            globalBestTourLength = minTour;
            globalBestTour = bestTour;
        }
        
        //Elitist
        if(elitist){elitistPheromoneUpdate();}
        
        tourLength = minTour;             
    }
    
    public int[] getBest()
    {
        return bestTour;        
    }
    
    public float[][] getPheromone()
    {
        return pheromoneMatrix;
    }
    
    public void elitistPheromoneUpdate()
    {
        float pheromoneChange = 1.0f/(float)globalBestTourLength;
        for (int i = 0; i < cityList.length; i++) 
        {
            int start = globalBestTour[i];
            int next =  globalBestTour[i+1];
            pheromoneMatrix[start][next] = pheromoneMatrix[start][next]+pheromoneChange;
            pheromoneMatrix[next][start] = pheromoneMatrix[start][next];
        }
        
    }
}