/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package epic.agents;

/**
 * Base class for all agents in agent based modelling
 * @author Samantha Lycett
 */
public interface Agent {
    
    /**
     * set the name of the agent (as a string)
     * @param name 
     */
    public void     setName(String name);
    
    /**
     * get the name of the agent (as a string)
     * @return name
     */
    public String   getName();
    
    /**
     * get the unique ID of the agent
     * @return id
     */
    public long     getID();
    
    /**
     * set the state of the agent (as a string)
     * @param state 
     */
    public void     setState(String state);
    
    /**
     * get the state of the agent (as a string)
     * @return state
     */
    public String   getState();
    
}
