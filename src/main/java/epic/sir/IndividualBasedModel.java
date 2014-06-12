/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package epic.sir;

import broadwick.rng.RNG;
import broadwick.stochastic.SimulationEvent;
import epic.agents.Agent;
import epic.basic.BasicSimulationState;
import java.util.*;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Samantha Lycett
 * @version 5 June 2014
 */
@Slf4j
public class IndividualBasedModel {
    
    RNG                         random          = new RNG();            // returns the singleton
    List<Agent>                 agents          = new ArrayList<>();
    List<List<Agent>>           agentsInStates  = new ArrayList<>();
    List<IndividualStateType>   allowedStates;
    SIRRates                    rates;
    List<BasicSimulationState>  modelStateCounter;
    String                      delim           = ",";
    
    public IndividualBasedModel() {
        
    }
    
    ///////////////////////////////////////////////////////////////////////
    
    public void setAgents(List<Agent> agents) {
        this.agents = agents;
    }
    
    public List<Agent> getAgents() {
        return agents;
    }
    
    public void setRates(SIRRates rates) {
        this.rates          = rates;
        this.allowedStates  = rates.allowedStates;
        
        modelStateCounter   = new ArrayList<>();
        agentsInStates      = new ArrayList<>();
        for (int i = 0; i < allowedStates.size(); i++) {
            BasicSimulationState newState = new BasicSimulationState( (allowedStates.get(i)).toString() );
            newState.setStateValue(0);
            modelStateCounter.add(newState);
        }
        
    }
    
    public SIRRates getRates() {
        return rates;
    }
    
    void intialiseWithInfected(int N, int initI) {
        
        for (IndividualStateType state : allowedStates) {
            if (state.equals(IndividualStateType.SUSCEPTIBLE)) {
                createIndividualAgentsInState(state, (N-initI));
            } else if (state.equals(IndividualStateType.INFECTED)) {
                createIndividualAgentsInState(state, initI);
            } else {
                createIndividualAgentsInState(state, 0);
            }
        }
    }
    
    public void initialiseWithAgents(List<Agent> aa) {
        agentsInStates = new ArrayList<>();
        agents         = new ArrayList<>();
        
        for (IndividualStateType state : allowedStates) {
            List<Agent> subList = new ArrayList<>();
            agentsInStates.add(subList);
        }
        
        for (Agent a : aa) {
            IndividualStateType st = IndividualStateType.valueOf( a.getState() );
            int listIndex          = allowedStates.indexOf(st);
            agentsInStates.get(listIndex).add(a);
            agents.add(a);
        }
        
    }
    
    ///////////////////////////////////////////////////////////////////////
    
    private void createIndividualAgentsInState(IndividualStateType st, int N) {
        if (allowedStates.contains(st)) {
            List<Agent> subList = new ArrayList<>();
            for (int i = 0; i < N; i++) {
                Agent newAgent = new IndividualAgent();
                agents.add(newAgent);
                subList.add(newAgent);
            }
            agentsInStates.add(subList);
        }
    }
    
    
    private void countStates() {
        
        for (int i = 0; i < allowedStates.size(); i++) {
            int numberInState      = (agentsInStates.get(i)).size();
            (modelStateCounter.get(i)).setStateValue(numberInState);
        }
        
    }
    
    //////////////////////////////////////////////////////////////////////////
    
    // this is not done a replicate number of times yet !!
    public EventDone performEvent(SimulationEvent reaction) {
        
        if ( reaction instanceof SIREventWithRate) {
            SIREventWithRate event = ((SIREventWithRate) reaction);
            
            Agent fromAgent             = event.getFromAgent();
            Agent toAgent               = event.getToAgent();
            SIREventType    etype       = event.getEventType();
            
            EventDone ev = new EventDone();
            ev.setEventType(etype);
            
            if (fromAgent ==  null) {
                // choose an appropriate from agent
                fromAgent = getAgentWithState( event.getFromAgentState() );
            }
            
            ev.setFromAgent(fromAgent);
            ev.setFromState( event.getFromAgentState() );
            
            if (etype.equals(SIREventType.EXPOSURE) || (etype.equals(SIREventType.INFECTION))) {
                // from and to agent should be different
                
                if (toAgent == null) {
                    // choose an appropriate to agent
                    // this will always be susceptible
                    toAgent = getAgentWithState( IndividualStateType.SUSCEPTIBLE );
                }
                updateAgent(toAgent, IndividualStateType.SUSCEPTIBLE, event.getToAgentState() );
                
                ev.setToAgent( toAgent );
                ev.setToState( event.getToAgentState() );
                
            } else {
                // from and to agents are the same
                updateAgent(fromAgent, event.getFromAgentState(), event.getToAgentState());
                
                ev.setToAgent( fromAgent );
                ev.setToState( event.getToAgentState() );
            }
            
            return ev;
            
        } else {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
    
    /**
     * generates events depending on model state, does not specify the from and to agents
     * @return list of events with rates
     */
    public List<SIREventWithRate> generateEvents() {
        List<SIREventWithRate> events = new ArrayList<>();
        
        
        for ( SIREventType etype : rates.getEventTypes() ) {
            
            // EXPOSURE, BECOME_INFECTIOUS, INFECTION, RECOVERY, IMMUNITY, SUSCEPTIBILITY;
            IndividualStateType fromState = null;
            IndividualStateType toState   = null;
            double              rr        = 0.0;
            
            switch (etype) {
                case EXPOSURE: {
                        int numS  = getNumberOfAgentsInState(IndividualStateType.SUSCEPTIBLE);
                        int numI  = getNumberOfAgentsInState(IndividualStateType.INFECTED);
                        rr        = rates.getRate(etype) * (double)numS * (double)numI / (double)getN();
                        fromState = IndividualStateType.INFECTED;
                        toState   = IndividualStateType.EXPOSED;
                    }
                    break;
                    
                case BECOME_INFECTIOUS: {
                        int numE  = getNumberOfAgentsInState(IndividualStateType.EXPOSED);
                        rr        = rates.getRate(etype) * (double)numE;
                        fromState = IndividualStateType.EXPOSED;
                        toState   = IndividualStateType.INFECTED;
                    }
                    break;
                    
                case INFECTION: {
                        int numS  = getNumberOfAgentsInState(IndividualStateType.SUSCEPTIBLE);
                        int numI  = getNumberOfAgentsInState(IndividualStateType.INFECTED);
                        rr        = rates.getRate(etype) * (double)numS * (double)numI / (double)getN();
                        fromState = IndividualStateType.INFECTED;
                        toState   = IndividualStateType.INFECTED;
                    }
                    break;
                    
                case RECOVERY: {
                        int numI  = getNumberOfAgentsInState(IndividualStateType.INFECTED);
                        rr        = rates.getRate(etype) * (double)numI;
                        fromState = IndividualStateType.INFECTED;
                        toState   = IndividualStateType.RECOVERED;
                    }
                    break;
                    
                case IMMUNITY: {
                        int numR  = getNumberOfAgentsInState(IndividualStateType.RECOVERED);
                        rr        = rates.getRate(etype) * (double)numR;
                        fromState = IndividualStateType.RECOVERED;
                        toState   = IndividualStateType.IMMUNE;
                    }
                    break;
               
                case SUSCEPTIBILITY: {
                        fromState = rates.getLastState();
                        int num   = getNumberOfAgentsInState(fromState);
                        rr        = rates.getRate(etype) * (double)num;
                        toState   = IndividualStateType.SUSCEPTIBLE;
                    }
                    break;
            }    
                
            if (rr > 0) {
                SIREventWithRate ev  = new SIREventWithRate(etype, 
                                        new IndividualSimulationState(fromState), 
                                        new IndividualSimulationState(toState),
                                        rr);  
           
                events.add(ev);
            }
          
        }
        
        return events;
        
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    protected void updateAgent(Agent agent, IndividualStateType fromState, IndividualStateType toState) {
    
        if (!fromState.equals(toState)) {
           
            // move the agent from its original state list
            int listIndex                   = allowedStates.indexOf( fromState );
            List<Agent> fromList            = agentsInStates.get( listIndex  );
            Agent agentToMove               = fromList.remove( fromList.indexOf(agent) );
            (modelStateCounter.get(listIndex)).decrementStateValue();
        
            // agent moving to new state list
            int listIndex2                  = allowedStates.indexOf( toState );
            (agentsInStates.get( listIndex2 )).add(agentToMove);
            (modelStateCounter.get(listIndex2)).incrementStateValue();
            
            
            // update the state of the agent
            // ((IndividualAgent)agent).setAgentState(toState);     // SJL 9 June 2014 - just use Agent interface
            agent.setState( toState.toString() );
            
        
        } else {
            log.error("Sorry attempting to update agent "+agent.toString()+" from "+fromState+" to "+toState);
        }
        
    }
    
    
    public Agent getAgentWithState(IndividualStateType state) {
        
        int listIndex  = allowedStates.indexOf( state );
        
        if (listIndex >= 0) {
            int n       = agentsInStates.get(listIndex).size();
            int choice  = random.getInteger(0, (n-1));
            if (choice >= 0) {
                return (agentsInStates.get(listIndex)).get(choice);
            } else {
                log.error("Sorry cant find enough agents in state = "+state+" there are only "+n);
                return null;
            }
        } else {
            log.error("Sorry cant find agents in state list = "+state);
            return null;
        }
        
        
    }
    
    public List<Agent> getAgentsInState(IndividualStateType state) {
        int listIndex = allowedStates.indexOf( state );
        return agentsInStates.get(listIndex);
    }
    
    /////////////////////////////////////////////////////////////////////
    
    /**
     * returns the number of agents in the state, but if the model doesnt have this state then it returns 0
     * @param state
     * @return number of agents in the state or 0
     */
    public int getNumberOfAgentsInState(IndividualStateType state) {
        
        int listIndex  = allowedStates.indexOf( state );
        if (listIndex >= 0) {
            return agentsInStates.get(listIndex).size();
        } else {
            return 0;
        }
    }
    
    public int getN() {
        return agents.size();
    }
    
    public String modelStateHeader() {
        String txt = allowedStates.get(0).name();
        for (int i = 1; i < allowedStates.size(); i++) {
            txt = txt + delim + allowedStates.get(i).name();
        }
        return txt;
    }
    
    public String modelStateString() {
        String txt = "" + agentsInStates.get(0).size();
        for (int i = 1; i < allowedStates.size(); i++) {
            txt = txt + delim + agentsInStates.get(i).size();
        }
        return txt;
    }
    
    public String toVerboseString() {
        String txt = allowedStates.get(0).name();
        txt = txt + ":" + agentsInStates.get(0).size();
        for (int i = 1; i < allowedStates.size(); i++) {
            txt = txt + "\t" + allowedStates.get(i).name() + ":" + agentsInStates.get(i).size();
        }
        return txt;
    }
    
    public String getDelim() {
        return delim;
    }
    
}
