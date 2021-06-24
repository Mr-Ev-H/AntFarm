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
import java.math.*;
import java.util.LinkedList;
import java.util.Arrays;

/**
 * @author Evan Harris
 */
public class Network
{
    //Declare storage data structures
    private City[] citys;
    private int distanceMatrix[][];
    private int nnMatrix[][];
    private int cityCount;
    private int acoSolutions = 1;
    private Random rand;
    //Declare array of cities optimised by nn routine.
    private City[] nearestNeighbourRoute;
    //Boolean controlling the status of the nn procedure. 
    private boolean nearestNeighbourRun, displayPheromone;
    private int nnPath;
    private int psSize;
    private int nnDepth;
    private AntController acs;
    int acoTemp[];
    City acoTour[];
    
    /**
     * The basic constructor for the network object.
     * @param citys The number of cities to be generated with random x,y coordinates.
     * @param psSize the XxY problemspace size the network should generate cities to fit within.
     */
    public Network(int citys, int psSize)
    {
        //Setup data structures
        cityCount = citys;
        this.citys = new City[cityCount];
        distanceMatrix = new int[cityCount][cityCount];
        rand = new Random();
        this.psSize = psSize;
        
        //Iterate over the citys array and assign an id and random x,y coordinates
        for (int i = 0; i < this.citys.length; i++) 
        {
            this.citys[i] = new City(i,rand.nextInt(psSize)+10, rand.nextInt(psSize)+10, citys);
        }
                
        generateDistanceMatrix();        
    }
    
    /**
     * Get the array of cities for this particular network.
     * @return Returns the array containing the pseudorandomly generated cities.
     */
    public City[] getCityArray()
    {
        return citys;     
    }
    
    public City[] getACOTour()
    {
        return acoTour;
    }
    
    /**
     * Calculate and return the sub-optimal tour using the nearest neighbour method.
     * This runs the method if not already run on this array. The nnR boolean needs
     * to be updated if the city array changes.
     * @return An array containing the tour generated using the nearest neighbour routine.
     */
    public City[] getNearestNeighbourRoute()
    { 
        if(!nearestNeighbourRun)nearestNeighbour();
        return nearestNeighbourRoute; 
    }
    
    /**
     * Return the length of the tour generated by the nn method.
     * @return The nn optimised tour length.
     */
    public int getNNTourLength()
    {
        if(!nearestNeighbourRun)nearestNeighbour();
        return nnPath; 
    }
    
    public int getACOTourLength()
    {
        return acs.getTourLength();
    }
    
    /**
     * Get the city with id i.
     * @param i The city id to be returned.
     * @return The city object assigned as id i.
     */
    public City getCityNo(int i)
    {
        return citys[i];
    }
    
    /**
     * Returns the number of cities in this network.
     * @return The number of cities in this network.
     */
    public int getCityCount()
    {
        return citys.length;
    }
    
    /**
     * Get the euclidean distance between two cities as an integer.
     * @param city1 The origin city. 
     * @param city2 The destination city
     * @return The distance between city 1 and 2 as an integer.
     */
    public int eucDist(int city1, int city2)
    {
	int DX = citys[city2].getx()-citys[city1].getx();
        int DY = citys[city2].gety()-citys[city1].gety();
	return (int)Math.sqrt((DX*DX)+(DY*DY));
    }
    
    /**
     * Optimise the network using the nearest neighbour algorithm.
     */
    public void nearestNeighbour() 
    {
        /*
         *Define two linked lists containing the remaining cities to be processed 
         *and the cities currently in the tour
         */
        LinkedList<City> citiesRemaining = new LinkedList(Arrays.asList(citys));
        LinkedList<City> tourOrder = new LinkedList();
        
        //Define holding variables for the closest cities and the shortest path.
        City closestCity;
        int closestPath;
        int totalPath = 0;
        
        //Define and mark the starting city 
        int start = 0;
        tourOrder.add(citys[start]);
        citiesRemaining.remove(start);
        
        //Process all cities in array.
        while(citiesRemaining.size()>0)
        {
            //Set the closest city and the closest path to be out of bounds for the array.
            closestCity = new City(cityCount,psSize+1,psSize,cityCount);
            closestPath = psSize^3;
            
            //Obtain the previous city
            City lastCity = (City)tourOrder.getLast();

            
            //Iterate over the cities yet to be processed
            for (City elem2 : citiesRemaining) 
            {            
                //Calculate the distance between the last city and the current city
                int distance = eucDist(lastCity.getid(),elem2.getid());
                  
                //If this city is more optimal than the previous city set this city to be the next city in the tour
                if(distance<closestPath)
                {
                   closestCity = elem2;
                   closestPath = distance;
                }
            } 
            
            //Add the closest city to the last point in the tour
            tourOrder.addLast(closestCity);
            
            //Remove this city from the processing list
            citiesRemaining.remove(closestCity);
            
            //Update the total path
            totalPath+=closestPath;
        }
        totalPath+=eucDist(tourOrder.getFirst().getid(), tourOrder.getLast().getid());
        tourOrder.addLast(tourOrder.getFirst());

        
        //Update completed tour array.java passing array
       nearestNeighbourRoute = new City[tourOrder.size()];
       nearestNeighbourRoute = tourOrder.toArray(nearestNeighbourRoute);
       nearestNeighbourRun = true;
      
       nnPath=totalPath;
    }

    public void acoRoutine(int ants, int nnDepth, int solutions, float evap, boolean elitist)
    {
        generateNNMatrix(nnDepth);
        acs = new AntController(this.citys, cityCount,psSize,distanceMatrix,nnMatrix,evap,ants, elitist);   
        this.acoSolutions = solutions;
        acoTour = new City[cityCount+1];
    }
    
    public City[] acoTick()
    {
            int tempTour[] = acs.acsTick();
            for (int j = 0; j < tempTour.length; j++) 
            {
                acoTour[j] = citys[tempTour[j]];
            }
            return acoTour;
    }
    
    public void generateDistanceMatrix()
    {
        for (int i = 0; i < citys.length; i++) 
        {
            for (int j = 0; j < citys.length; j++) 
            {
                distanceMatrix[i][j] = eucDist(i, j);
            }
        }
    }
        
    
    public void generateNNMatrix(int nnDepth)
    {
        //Check for nnDepth overrun
        if(nnDepth>=cityCount)nnDepth=cityCount-1;
        
        //Create nnMatrix with required specifications
        nnMatrix = new int[cityCount][nnDepth];
        //Handle a depth of 0;
        if(nnDepth == 0)
        {
            //The ACO routine will see the nnMatrix as containing only 0's and should take the next best option
            nnMatrix = new int[cityCount][1];
            for (int i = 0; i < cityCount; i++) {nnMatrix[i][0] = 0;}
            return;
        }
        //Create and populate the temporary Holding Matrix
        City holdingMatrix[][] = new City[cityCount][cityCount];
        for (int i = 0; i < distanceMatrix.length; i++) 
        {
            for (int j = 0; j < distanceMatrix[i].length; j++) 
            {
                holdingMatrix[i][j] = citys[j];
            }
        }
        
        
        //Iterate over each city in the problem
        for (int i = 0; i < distanceMatrix.length; i++) 
        {
            //Iterate over the connective city array, j will act as a partition between the sorted and unsorted elements
            for (int j = 0; j < nnDepth+1; j++) 
            {
                //Set the closest possible city to be the head of the unsorted partition
                int closest = j;
                //Set the closest possible distance to be the distance between the from to the to city
                int closestDistance = eucDist(i, closest);
                //Iterate over the elements in the unsorted partition
                for (int k = j+1; k < holdingMatrix[i].length; k++) 
                {
                    //calculate the distance between the start and the current city
                    int tempDist = eucDist(i, holdingMatrix[i][k].getid());
                    //Check to see whether or not this city is closer than the previous one
                    //The two cities cannot be a distance of 0 away from each other
                    if(tempDist < closestDistance)
                    {
                        //Set the distance 
                        closestDistance = tempDist;
                        //update the city variable.
                        closest = k;
                    }
                }
                //Swap the two cities
                City tempCity = holdingMatrix[i][j];
                holdingMatrix[i][j] = holdingMatrix[i][closest];
                holdingMatrix[i][closest] = tempCity;
            }    
        }
         
    
        //Copy the sorted cities to the nnMatrix
        for (int i = 0; i < cityCount; i++) 
        {
            for (int j = 0; j < nnDepth; j++) 
            {
                //Skip the first element (the from city)
                nnMatrix[i][j] = holdingMatrix[i][j+1].getid();
            }
        }
    }    
    
    public float[][] getACOPheromone()
    {
        return acs.getPheromone();
    }
             
}
