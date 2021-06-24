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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 *
 * @author  Evan Harris
 */
public class Interface extends javax.swing.JFrame
{
    // Variables declaration - do not modify
    private javax.swing.JButton runButton, regenNetwork;
    private JCheckBox viewNN, viewPheromone, displayBest, elitist;
    private JSpinner antNumbers, nnDepth, cityNumbers, solutions, evaporation;
    private JProgressBar progress;
    private javax.swing.JPanel menuPanel;
    private NetworkInterface network;
    private int cityNumber = 12;

    /** Creates new form AntFarm */
    public Interface()
    {
        initComponents();
        this.setTitle("Ant Farm ACO Simulator");
    }
    
    public void addMenuComponents()
    {
        //Setup ACO Menu Layout Control
        java.awt.GridBagLayout menuLayout = new java.awt.GridBagLayout();
        java.awt.GridBagConstraints gbC = new java.awt.GridBagConstraints();
        menuLayout.addLayoutComponent(menuPanel, gbC);
        menuPanel.setLayout(menuLayout);
        menuPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,3));
        
        //Setup runButton Constraints
        //gbC.fill = GridBagConstraints.HORIZONTAL;
        gbC.fill = gbC.BOTH;
        
        gbC.gridx = 0;
        gbC.gridy = 0;
        //Setup elitist checkbox
        menuPanel.add(elitist,gbC);
        //Setup antNumbers Constraints
        gbC.gridy = 1;
        menuPanel.add(new JLabel("Ants in Colony"), gbC);
        gbC.gridy = 2;
        menuPanel.add(antNumbers, gbC);
        //Setup nnDepth Constraints
        gbC.gridy = 3;
        menuPanel.add(new JLabel("Depth of Nearest Neighbour Lookahead"), gbC);
        gbC.gridy = 4;
        menuPanel.add(nnDepth, gbC);
        
        
        //Setup solutions Constraints
        gbC.gridy = 5;
        menuPanel.add(new JLabel("Number of repetitions"), gbC);
        gbC.gridy = 6;
        menuPanel.add(solutions, gbC);
        
        //Setup Evaporation Constraints
        gbC.gridy = 7;
        menuPanel.add(new JLabel("Evaporation factor"), gbC);
                        
        gbC.gridy = 8;
        menuPanel.add(evaporation, gbC);
        
        //Setup runButton Constraints
        gbC.gridy = 9;
        menuPanel.add(runButton, gbC);
        
        
        
        
        
        //Setup network features contstraints
        gbC.gridy = 13;
        menuPanel.add(viewNN,gbC);
        //gbC.gridy = 14;
        //menuPanel.add(viewPheromone,gbC);
        
        //Setup displayBest constraints
        gbC.gridy = 15;
        menuPanel.add(displayBest,gbC);
        
        //Setup cityNumbers Constraints
        gbC.gridy = 16;
        gbC.ipady = 350;
        menuPanel.add(new JPanel(), gbC);
        gbC.ipady = 0;
        gbC.gridy = 17;
        menuPanel.add(new JLabel("Number of Cities in network"), gbC);
        //Setup displayBest constraints
        gbC.gridy = 18;
        menuPanel.add(cityNumbers, gbC);
        
       
        
        
        gbC.ipady = 0;
        gbC.gridy=19;
                menuPanel.add(regenNetwork, gbC);
        //Setup action listeners
        runButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent arg0) 
            {
                int ants = (Integer)antNumbers.getValue();
                int depth = (Integer)nnDepth.getValue();
                int sol = (Integer)solutions.getValue();
                float evap = (Float)evaporation.getValue();
                boolean bestAco = displayBest.isSelected();
                boolean elitistCheck = elitist.isSelected();
                boolean displayPheromone = false;
                network.runACO(ants, depth, sol, evap,progress,elitistCheck,bestAco);
                //network.repaint();

            }
        });
        regenNetwork.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent arg0) 
            {
                int tempCity = (Integer)cityNumbers.getValue();
                cityNumber = tempCity;
                network.regenNetwork(tempCity);
                nnDepth.setModel(new SpinnerNumberModel(cityNumber,0,cityNumber,1));
                nnDepth.updateUI();
                progress.setValue(0);
                progress.setString("");
            }
        });
        viewNN.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) 
            {
                network.toggleNN();
            }
        });
        
    }
    
    public void setupFrame()
    {
        java.awt.BorderLayout frameLayout = new java.awt.BorderLayout();
        setLayout(frameLayout);
        add(network, frameLayout.WEST);
        add(menuPanel, frameLayout.EAST);   
        add(progress,frameLayout.SOUTH);
    }

    private void initComponents()
    {
        //Init components
        menuPanel = new javax.swing.JPanel();
        runButton = new javax.swing.JButton("Run ACO Routine");
        regenNetwork = new javax.swing.JButton("Regenerate Network");
        antNumbers = new JSpinner(new SpinnerNumberModel(5,1,100,1));
        nnDepth = new JSpinner(new SpinnerNumberModel(cityNumber,0,cityNumber,1));
        cityNumbers = new JSpinner(new SpinnerNumberModel(cityNumber,3,100,1));
        evaporation = new JSpinner(new SpinnerNumberModel((Float)0.5f, (Float)0.1f, (Float)1f, (Float)0.1f));
        solutions = new JSpinner(new SpinnerNumberModel(10,1,100,1));
        viewNN = new JCheckBox("Display Nearest Neighbour Tour", true);
        displayBest = new JCheckBox("Display the best ACO tour iteratively", true);
        elitist = new JCheckBox("Use the elitist pheromone update",false);
        network = new NetworkInterface(cityNumber);

        network.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY,5));
                
        network.setVisible(true);
        network.setBounds(0, 0, 500, 500);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        progress = new JProgressBar(SwingConstants.HORIZONTAL);
        progress.setStringPainted(true);
        progress.setString("");
        
        //Add ACO menu to the frame
        setupFrame();
        //Add components to the ACO menu
        addMenuComponents();

        pack();
    }
    
    public static void main(String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                new Interface().setVisible(true);
            }
        });
    } 
}
