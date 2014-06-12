/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package epic.sir;

import epic.agents.Agent;
//import epic.sir.IndividualStateType;

/**
 * IndividualAgent class represents an individual (e.g. animal)
 * Individuals have an individual state (Susceptible, Exposed, Infected, etc)
 * @author Samantha Lycett
 */
public class IndividualAgent implements Agent {
    
    ////////////////////////////////////////////////////////////////
    // class methods to handle unique agent id numbers
    private static long nextID = 0;
    
    private static long getNextID() {
        nextID++;
        return nextID;
    }
    //////////////////////////////////////////////////////////////
    
   
    //////////////////////////////////////////////////////////////
    // instance variables and methods
    
    IndividualStateType agentState = IndividualStateType.SUSCEPTIBLE;
    String              name;
    final long          id;
    
    public IndividualAgent() {
        id          = getNextID();
        name        = "IA" + String.format("%09d", id);
    }
    
    public IndividualAgent(String name) {
        id          = getNextID();
        this.name   = name;
    }
    
    
    ////////////////////////////////////////////////////////////////////////
    // IndividualAgent specific methods
    
    /**
     * gets the Individual Agent's state as an enumerated state
     * @return agentState
     */
    public IndividualStateType getAgentState() {
        return agentState;
    }
    
     /**
     * sets the Individual Agent's state using the allowed enumerated states
     * @param state 
     */
    public void setAgentState(IndividualStateType state) {
        this.agentState = state;
    }
    
    ///////////////////////////////////////////////////////////////////////
    // Agent inferface methods
    
    @Override
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public long getID() {
        return id;
    }
    
    
    @Override
    public void setState(String state) {
        this.agentState = IndividualStateType.valueOf(state);
    }

    @Override
    public String getState() {
       return agentState.name();
    }
    
    ///////////////////////////////////////////////////////////////////////
    // hashCode and equals generated on the unique id

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IndividualAgent other = (IndividualAgent) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }
    
    
}
