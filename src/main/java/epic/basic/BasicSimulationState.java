/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package epic.basic;

import broadwick.stochastic.SimulationState;

/**
 * BasicSimulationState contains a stateName (e.g. S, I or R) and an integer state value (e.g. number of infecteds).
 * It provides methods to increment and decrement the state value.
 * @author  Samantha Lycett
 * @version 9 April 2014
 */
public class BasicSimulationState implements SimulationState {
    
    private final String stateName;
    private int          stateValue  = 0;
    private final int    minValue    = 0;            // set this to -ve infinity to allow -ve values

    public BasicSimulationState (String stateName) {
        this.stateName = stateName;
    }
    
    ///////////////////////////////////////////////////////////////
    
    public void setStateValue(int sv) {
        this.stateValue = sv;
    }
    
    public int getStateValue() {
        return stateValue;
    }
    
    ////////////////////////////////////////////////////////////////
    
    public void incrementStateValue() {
        stateValue++;
    }
    
    public void incrementStateValue(int n) {
        stateValue = stateValue + n;
    }
    
    public void decrementStateValue() {
        if (stateValue > minValue) {
            stateValue--;
        } else {
            stateValue = minValue;
        }
    }
    
    public void decrementStateValue(int n) {
        if (stateValue >= (minValue + n)) {
            stateValue = stateValue - n;
        } else {
            stateValue = minValue;
        }
    }
    
    
    ////////////////////////////////////////////////////////////////
    
    @Override
    public String getStateName() {
        return stateName;
    }
    
    /**
     * returns state name and value as a single string
     * @return stateName and value
     */
    public String info() {
        return stateName + ":" + stateValue;
    }
    
    @Override
    public String toString() {
        return stateName;
    }
    
}
