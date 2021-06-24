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
