/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package epic.basic;

import broadwick.stochastic.SimulationController;
import broadwick.stochastic.StochasticSimulator;

/**
 * a base implementation of SimulationController, this one stops when max time is reached
 * @author Samantha Lycett
 * @version 9 April 2014
 */
public class BasicController implements SimulationController {

    double maxTime = 1000.0;
    
    public BasicController(double maxTime) {
        this.maxTime = maxTime;
    }
    
    public double getMaxTime() {
        return maxTime;
    }
    
    public void setMaxTime(double maxTime) {
        this.maxTime = maxTime;
    }
    
    @Override
    public boolean goOn(StochasticSimulator process) {
        return process.getCurrentTime() < maxTime;
    }
    
}
