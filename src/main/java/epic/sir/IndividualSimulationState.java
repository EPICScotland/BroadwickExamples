/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package epic.sir;

import broadwick.stochastic.SimulationState;
import epic.agents.*;
import lombok.Getter;
import lombok.Setter;

/**
 * IndividualSimulationState class is used to describe what you would like to happen
 * e.g. it is used to request that an agent becomes infected
 * @author Samantha Lycett
 * @version 9 June 2014
 */
public class IndividualSimulationState implements SimulationState {
    
    @Getter
    @Setter
    //private IndividualAgent     agent;
    private Agent               agent;
    
    private IndividualStateType state;
    
    /**
     * use this constructor to request that agent updates its state to the desired state (the toState)
     * @param iagent
     * @param desiredState 
     */
    /*
    public IndividualSimulationState(IndividualAgent iagent, IndividualStateType desiredState) {
        this.agent  = iagent;
        this.state  = desiredState;
    }
    */
     public IndividualSimulationState(Agent iagent, IndividualStateType desiredState) {
        this.agent  = iagent;
        this.state  = desiredState;
    }
    
    /**
     * use this constructor to record the current state (the fromState)
     * @param iagent 
     */
     /*
    public IndividualSimulationState(IndividualAgent iagent) {
        this.agent  = iagent;
        this.state  = iagent.getAgentState();
    }
    */
     public IndividualSimulationState(IndividualAgent iagent) {
        this.agent  = iagent;
        this.state  = iagent.getAgentState();
    }
    
    /**
     * use this constructor to set the desired state for an unnamed individual (the toState)
     * @param desiredState 
     */
    public IndividualSimulationState(IndividualStateType desiredState) {
        this.agent = null;
        this.state = desiredState;
    }
    
    ////////////////////////////////////////////////////////////////////////
    
     @Override
    public final String getStateName() {
        String n;
        if (agent != null) {
            n    = agent.getName() + ":" + state.toString();
        } else {
            n    = "any:"+state.toString();
        }
        
        return n;
    }
    
    public IndividualStateType getStateType() {
        return state;
    }
    
    @Override
    public String toString() {
        return getStateName();
    }
}