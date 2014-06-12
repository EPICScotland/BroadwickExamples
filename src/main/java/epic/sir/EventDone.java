/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package epic.sir;

import epic.agents.Agent;
import lombok.Getter;
import lombok.Setter;


/**
 *
 * @author Samantha Lycett
 */
public class EventDone {
    
    @Getter
    @Setter
    SIREventType        eventType;
    
    @Getter
    @Setter
    Agent               fromAgent;
    
    @Getter
    @Setter
    Agent               toAgent;
    
    @Getter
    @Setter
    IndividualStateType fromState;
    
    @Getter
    @Setter
    IndividualStateType toState;
    
    
    @Getter
    @Setter
    double              time = -1;
    
    static String stateSep  = ":";
    static String edgeSep   = " -> ";
    static String delim     = ",";
    
    public EventDone() {
        
    }
    
    @Override
    public String toString() {
        
        String fromName;
        if (fromAgent instanceof IndividualAgent) {
            fromName = ((IndividualAgent)fromAgent).getName();
        } else {
            fromName = fromAgent.getName();
        }
        
        
        String toName;
        if (toAgent instanceof IndividualAgent) {
            toName = ((IndividualAgent)toAgent).getName();
        } else {
            toName = toAgent.getName();
        }
        
        String txt = "" + time + delim + 
                        eventType + delim + 
                        fromName + stateSep + fromState + edgeSep + 
                        toName + stateSep + toState;
        return txt;
    }
    
}
