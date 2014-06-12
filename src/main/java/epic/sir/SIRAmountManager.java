/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.

* NEED TO COMBINE EVENTS

 */

package epic.sir;

import broadwick.stochastic.AmountManager;
import broadwick.stochastic.SimulationEvent;
import broadwick.stochastic.TransitionKernel;
import java.util.*;


import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Samantha Lycett
 */
@Slf4j
public class SIRAmountManager implements AmountManager {
    
    
    boolean                         printToLogInfo = false;
    IndividualBasedModel            model;
    TransitionKernel                kernel;
    List<EventDone>                 eventsJustDone;
    
    public SIRAmountManager(IndividualBasedModel model, TransitionKernel kernel) {
        this.model  = model;
        this.kernel = kernel;
    }
    
    /////////////////////////////////////////////////////////////////////////
    
    /**
     * sets the underlying IBM model
     * @param model 
     */
    public void setModel(IndividualBasedModel model) {
        this.model = model;
    }
    
    /**
     * gets the underlying IBM model
     * @return model
     */
    public IndividualBasedModel getModel() {
        return model;
    }
    
    /**
     * sets the transition kernel
     * @param kernel 
     */
    public void setTransitionKernel(TransitionKernel kernel) {
        this.kernel = kernel;
    }
    
    /**
     * gets the transition kernel
     * @return kernel 
     */
    public TransitionKernel getTransitionKernel() {
        return kernel;
    }
    
    /**
     * sets the flag to print messages to log.info or not
     * @param p 
     */
    public void setPrintToLogInfo(boolean p) {
        this.printToLogInfo = p;
    }
    
    public List<EventDone> getEventsJustDone() {
        return eventsJustDone;
    }
    
    /////////////////////////////////////////////////////////////////////////
    
    /**
     * generates the first set of events from the model and updates the transition kernel with these new events
     * assumes that the model has been initialised with e.g. at least one infected
     * this is called from the BasicSIRModel class init method (which is the main broadwick entry point)
     */
    public void initialiseTransitionKernelWithFirstEvents() {
        updateTransitionKernel();
    }
    
    /**
     * generates new events from the underlying model and updates the transition kernel with these new events
     * this is called from within the performEvent method so cannot be called externally
     */
    private void updateTransitionKernel() {
        if (printToLogInfo) log.info("SIRAmountManager - Update transition kernel");
        
        // clear old events
        kernel.clear();
        
        // get new events with rates from model
        List<SIREventWithRate> events = model.generateEvents();
        
        // add new events to kernel
        for (SIREventWithRate event : events) {
            kernel.addToKernel(event, event.getRate());
        }
        
    }
    
    /////////////////////////////////////////////////////////////////////////

    /**
     * performs the simulation event (reaction) a replicate number of times (times)
     * this method infact asks the underlying model to perform the simulation event the replicate number of times
     * and then updates the transition kernel with new events generated from the underlying model
     * this is called from the simulator object in the BasicSIRModel class run method (which is the main broadwick entry point)
     * @param reaction
     * @param times 
     */
    @Override
    public void performEvent(SimulationEvent reaction, int times) {
        if (printToLogInfo) log.info("SIRAmountManager - performEvent reps="+times);
        
        eventsJustDone             = new ArrayList<>();
        for (int i = 0; i < times; i++) {
            EventDone ev = model.performEvent(reaction);
            if (ev != null) {
                eventsJustDone.add(ev);
            }
        }
        
        updateTransitionKernel();
    }

    @Override
    public String toVerboseString() {
        String txt = model.toVerboseString();
        return txt;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void resetAmount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void rollback() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
