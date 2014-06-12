/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package epic.sir;

import broadwick.stochastic.SimulationEvent;
import epic.agents.*;
import java.io.Serializable;

/**
 *
 * @author Samantha Lycett
 */
public class SIREventWithRate extends SimulationEvent implements Serializable {
    
    SIREventType                eventType;
    IndividualSimulationState   fromState = null;
    IndividualSimulationState   toState   = null;
    double                      rate      = 0;
   
    
    
    /**
     * SIREvent constructor for general events of SIREventType from a specified state & individual to a specified state & individual
     * @param eventType
     * @param fromState
     * @param toState
     * @param rate
     */
    public SIREventWithRate(SIREventType eventType, IndividualSimulationState fromState, IndividualSimulationState toState, double rate) {
        this.eventType = eventType;
        this.fromState = fromState;
        this.toState = toState;
        this.rate    = rate;
        
        super.setInitialState(fromState);
        super.setFinalState(toState);
        
    }
    
    
    /**
     * SIREvent constructor for events of SIREventType from a specified individual to an unspecified individual (if applicable)
     * @param eventType
     * @param agent 
     * @param rate
     */
    public SIREventWithRate(SIREventType eventType, IndividualAgent agent, double rate) {
        
        this.rate = rate;
        
        boolean res = false;
        switch (eventType) {
            case EXPOSURE:          res = setExposureEvent(agent);
                break;
            case BECOME_INFECTIOUS: res = setBecomeInfectiousEvent(agent);
                break;
            case INFECTION:         res = setInfectionEvent(agent);
                break;
            case RECOVERY:          res = setRecoveryEvent(agent);
                break;
            case IMMUNITY:          res = setImmunityEvent(agent);
                break;
            case SUSCEPTIBILITY:    res = setSusceptibilityEvent(agent);
                break;   
        }
        
        // if res then success
        // else setting event has failed because currently impossible
        
    }
    

    
    
    //////////////////////////////////////////////////////////////////
    
    /*
    public void setEventType(SIREventType etype) {
        this.eventType = etype;
    }
    */
    
    public SIREventType getEventType() {
        return eventType;
    }
    
    public Agent getFromAgent() {
        return fromState.getAgent();
    }
        
    public Agent getToAgent() {
        return toState.getAgent();
    }
    
    public IndividualStateType getFromAgentState() {
        return fromState.getStateType();
    }
    
    public IndividualStateType getToAgentState() {
        return toState.getStateType();
    }
    
    public IndividualSimulationState getFromState() {
        return fromState;
    }
    
    public IndividualSimulationState getToState() {
        return toState;   
    }
    
    public double getRate() {
        return rate;
    }
    
    public void setFromAgent(IndividualAgent fromAgent) {
        fromState.setAgent(fromAgent);
    }
    
    public void setToAgent(IndividualAgent toAgent) {
        toState.setAgent(toAgent);
    }
   
    ///////////////////////////////////////////////////////////////////////
    
    /**
     * For SIR Models,
     * configures as infection event if from agent is infected,
     * does not specify who will become infected,
     * returns true if from agent is infected (i.e. infection possible)
     * @param fromAgent
     * @return true if event possible
     */
    private boolean setInfectionEvent(IndividualAgent fromAgent) {
        
        this.eventType = SIREventType.INFECTION;
        
        if (fromAgent.getAgentState().equals(IndividualStateType.INFECTED)) {
            
            fromState = new IndividualSimulationState(fromAgent);
            toState   = new IndividualSimulationState(IndividualStateType.INFECTED);
            
            super.setInitialState(fromState);
            super.setFinalState(toState);
            
            return true;
        }  else {
            return false;
        }
        
        
    }
    
    /**
     * For SEIR models
     * configures as exposure event if from agent is infected
     * does not specify who will become exposed
     * returns true if from agent is infected (i.e. exposure possible)
     * @param fromAgent
     * @return true if event possible
     */
    private boolean setExposureEvent(IndividualAgent fromAgent) {
        
        this.eventType = SIREventType.EXPOSURE;
        
        if (fromAgent.getAgentState().equals(IndividualStateType.INFECTED)) {
            fromState = new IndividualSimulationState(fromAgent);
            toState   = new IndividualSimulationState(IndividualStateType.EXPOSED);
            
            super.setInitialState(fromState);
            super.setFinalState(toState);
            
            return true;
        } else {
            return false;
        }
        
        
    }
    
     /**
     * For SEIR models
     * configures as become infectious event if from agent is exposed
     * returns true if from agent is exposed (i.e. infectiousness possible)
     * @param fromAgent
     * @return true if event possible
     */
    private boolean setBecomeInfectiousEvent(IndividualAgent fromAgent) {
        
        this.eventType = SIREventType.BECOME_INFECTIOUS;
        
        if (fromAgent.getAgentState().equals(IndividualStateType.EXPOSED)) {
            fromState = new IndividualSimulationState(fromAgent);
            toState   = new IndividualSimulationState(fromAgent, IndividualStateType.INFECTED);
            
            super.setInitialState(fromState);
            super.setFinalState(toState);
            
            return true;
        } else {
            return false;
        }
        
    }
    
    /**
     * For SIR or SEIR models
     * configures as recovery event if from agent is infected 
     * returns true if from agent is infected (i.e. recovery possible)
     * @param fromAgent
     * @return true if event possible
     */
    private boolean setRecoveryEvent(IndividualAgent fromAgent) {
        
        this.eventType = SIREventType.RECOVERY;
        
        if (fromAgent.getAgentState().equals(IndividualStateType.INFECTED)) {
            fromState = new IndividualSimulationState(fromAgent);
            toState   = new IndividualSimulationState(fromAgent, IndividualStateType.RECOVERED);
            
            super.setInitialState(fromState);
            super.setFinalState(toState);
            
            return true;
        } else {
            return false;
        }
        
    }
    
    /**
     * For models with immunity
     * configures as become immune event for from agent in any state
     * @param fromAgent
     * @return true 
     */
    private boolean setImmunityEvent(IndividualAgent fromAgent) {
        this.eventType = SIREventType.IMMUNITY;
        fromState = new IndividualSimulationState(fromAgent);
        toState   = new IndividualSimulationState(fromAgent, IndividualStateType.IMMUNE);
        
        super.setInitialState(fromState);
        super.setFinalState(toState);
            
        return true;
    }
    
    /**
     * For any model (SIS, SIRS, SEIRS, SEIRMS etc)
     * configures as return to susceptibility event for the from agent in any state
     * @param fromAgent
     * @return true
     */
    private boolean setSusceptibilityEvent(IndividualAgent fromAgent) {
        this.eventType = SIREventType.SUSCEPTIBILITY;
        fromState = new IndividualSimulationState(fromAgent);
        toState   = new IndividualSimulationState(fromAgent, IndividualStateType.SUSCEPTIBLE);
        
        super.setInitialState(fromState);
        super.setFinalState(toState);
           
        return true;
    }
    
    
}
