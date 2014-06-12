/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package epic.basic;

import broadwick.stochastic.SimulationEvent;
import java.util.List;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;

/**
 * class implementing basic SIR stochastic model (but not an individual based model)
 * this is called from BasicSIRAmountManager
 * @author Samantha Lycett
 * @version 9 April 2014
 */
@Slf4j
public class BasicSIR {
    
    private static final String delim = ",";
    
    ////////////////////////////////////////////////////////////////////////
    
    int         N;
    BasicSimulationState[] states   = new BasicSimulationState[3];
    double[]    stateRates          = new double[2];
    
    /**
     * constructor for BasicSIR model, initialised with total population size = N, and S->I,I->R reaction rates
     * @param N
     * @param rates 
     */
    public BasicSIR(int N, double[] rates) {
        this.N          = N;
        this.stateRates = rates;
        
        states[0] = new BasicSimulationState("S");
        states[1] = new BasicSimulationState("I");
        states[2] = new BasicSimulationState("R");
    }
    
    ///////////////////////////////////////////////////////////////////
    
    /**
     * set the total population size for this SIR model
     * @param N 
     */
    public void setN(int N) {
        this.N      = N;
    }
    
    /**
     * set the reaction rates for S->I and I->R, this should be a double array with 2 elements, but it is not checked.
     * Note that the S->I rate is divided by N in the calculation, so you do not need to scale this on input
     * @param rates 
     */
    public void setRates(double[] rates) {
        this.stateRates = rates;
    }
    
    
    ///////////////////////////////////////////////////////////////////
    
    /**
     * initialised the number with the number of infected (numI), usually this = 1
     * @param numI 
     */
    public void initialiseWithInfected(int numI) {
        if ((numI < N) && (numI > 0)) {
            states[0].setStateValue(N-numI);
            
            for (int i = 1; i < states.length; i++) {
                if (states[i].getStateName().equals("I")) {
                    states[i].setStateValue(numI);
                } else {
                    states[i].setStateValue(0);
                }
            }
        } else {
            log.error("BasicSIR - Sorry cannot get numI="+numI+" with N="+N);
        }
    }
    
    ///////////////////////////////////////////////////////////////////
    
    private void infectSome(int n) {
        if (states[0].getStateValue() >= n) {
            // remove n from S and add to I
            states[0].decrementStateValue(n);
            states[1].incrementStateValue(n);
        } else {
            // all remaining S become I
            int allS = states[0].getStateValue();
            states[0].decrementStateValue(allS);
            states[1].incrementStateValue(allS);
        }
    }
    
    private void recoverSome(int n) {
        if (states[1].getStateValue() >= n) {
            // remove n from I and add to R
            states[1].decrementStateValue(n);
            states[2].incrementStateValue(n);
        } else {
            // all remaining I become recovered
            int allI = states[1].getStateValue();
            states[1].decrementStateValue(allI);
            states[2].incrementStateValue(allI);
        }
    }
    
    /**
     * performs the event (reaction), a replicate number of times (times)
     * here this will be either the S->I event, or the I->R event
     * @param reaction
     * @param times 
     */
    public void performEvent(SimulationEvent reaction, int times) {
        String initState = reaction.getInitialState().getStateName();
        String finalState= reaction.getFinalState().getStateName();
        
        if ( (initState.equals(states[0].getStateName())) &&
             (finalState.equals(states[1].getStateName())) ) {
            // S to I
            infectSome(times);
            
        } else if ( (initState.equals(states[1].getStateName())) &&
                    (finalState.equals(states[2].getStateName()))  ) {
            // I to R
            recoverSome(times);
           
        } else {
            log.error("BasicSIR - unperformable event "+initState+" to "+finalState);
        }
    }
    
    /**
     * returns S to I, and I to R events with rates
     * call this after perform event and use to add to transition kernel
     * @return list of events to be added to the transition kernel for the next step
     */
    public List<BasicEventWithRate> generateEvents() {
        @SuppressWarnings("Convert2Diamond")
        List<BasicEventWithRate> events = new ArrayList<BasicEventWithRate>();
        
        // S to I event
        // rate = beta * S * I / N
        double s_to_i_rate        = (stateRates[0] * getNumberS() * getNumberI())/(double)N;
        
        // I to R event
        // rate = gamma * I
        double i_to_r_rate        = stateRates[1] * getNumberI();
        
        // for each susceptible, add a potential infection event from any of the other infecteds
        events.add( new BasicEventWithRate(states[0], states[1], s_to_i_rate) );    
        
        // for each infected add a potential recovery event
        events.add( new BasicEventWithRate(states[1], states[2], i_to_r_rate) );
          
        return events;
    }
    
    /////////////////////////////////////////////////////////////////////////
    
    /**
     * returns the number of susceptibles (in state 0)
     * @return number of susceptibles
     */
    public int getNumberS() {
        return states[0].getStateValue();
    }
    
    /**
     * returns the number of infecteds (in state 1)
     * @return number of infecteds
     */
    public int getNumberI() {
        return states[1].getStateValue();
    }
    
    /**
     * returns the number of recovereds (in state 2)
     * @return number of recovereds
     */
    public int getNumberR() {
        return states[2].getStateValue();
    }
    
    /**
     * returns number in the named state, or -1 if state not found
     * @param stateName
     * @return number in the named state, or -1 if state not found
     */
    public int getNumberInState(String stateName) {
        int value = -1;
        for (BasicSimulationState s : states) {
            if (s.getStateName().equals(stateName)) {
                value = s.getStateValue();
            }
        }
        return value;
    }
    
    /**
     * returns the total population size as defined at initialisation (does not check the S+I+R=N)
     * @return N
     */
    public int getN() {
        return N;
    }
    
    //////////////////////////////////////////////////////////////////
    // methods for model output (e.g. logging)
    
    /**
     * returns the model state as a string, suitable for writing to a csv file: numberS,numberI,numberR
     * @return model state as a string
     */
    public String modelStateString() {
        String txt = "" + states[0].getStateValue();
        for (int i = 1; i < states.length; i++) {
            txt = txt + delim + states[i].getStateValue();
        }
        return txt;
    }
    
    /**
     * returns the header i.e. column names for the model state as a string, suitable for writing to an csv file: S,I,R
     * @return column names for the model state
     */
    public String modelStateHeader() {
        String txt = states[0].getStateName();
        for (int i = 1; i < states.length; i++) {
            txt = txt + delim + states[i].getStateName();
        }
        return txt;
    }
    
    /**
     * returns the delimiter used in the modelStateString and modelStateHeader; the default is comma (,)
     * @return delim
     */
    public String getDelim() {
        return delim;
    }
    
}
