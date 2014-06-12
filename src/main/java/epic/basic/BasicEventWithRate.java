/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package epic.basic;

import broadwick.stochastic.SimulationEvent;
import broadwick.stochastic.SimulationState;

/**
 * class comprised of simulation event and rate, useful for output of intermediate classes
 * e.g. BasicSIR and used for updating transition kernel in BasicSIRAmountManager
 * @author  Samantha Lycett
 * @version 9 April 2014
 */
public class BasicEventWithRate {
    
    double          rate;
    SimulationEvent event;
    
    public BasicEventWithRate(SimulationEvent event, double rate) {
        this.rate   = rate;
        this.event  = event;
    }
    
    public BasicEventWithRate(SimulationState init, SimulationState fin, double rate) {
        this.event = new SimulationEvent(init, fin);
        this.rate  = rate;
    }
    
    /////////////////////////////////////////////////////////////////////
    
    public SimulationEvent getEvent() {
        return event;
    }
    
    public double getRate() {
        return rate;
    }
    
}
