/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package epic.sir;

import broadwick.io.FileOutput;
import broadwick.stochastic.Observer;
import broadwick.stochastic.SimulationEvent;
import broadwick.stochastic.StochasticSimulator;
import java.util.Collection;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Samantha Lycett
 */
@Slf4j
public class IndividualSIRObserver extends Observer {
    
    int         tempCounter = 0;            // temp counter for events useful for developement of code
    FileOutput  outFile;                    // if not null then model state vs time written to separate file
    FileOutput  transmissionFile;           // if not null then individual transmission events written to separate file
    FileOutput  allEventsFile;              // if not null then all events written to file
    boolean     printToLogInfo = true;      // if true then prints verbose output to log.info
    String      eol            = System.getProperty("line.separator"); //"\n";

     /**
     * Comments from Observer class
     * Creates an observer dedicated to one process. The observer is NOT registered at the process, you have to call
     * Simulator.addObserver(Observer) in order to do this.
     * @param sim the process
     */
    public IndividualSIRObserver(StochasticSimulator sim) {
        super(sim);
    }
    
    /**
     * Creates an observer dedicated to one process
     * (but this observer is not registered at the process, you have to do that separately)
     * also opens a log files for the model output with the root filename = fname
     * @param sim
     * @param fname 
     */
    public IndividualSIRObserver(StochasticSimulator sim, String fname) {
        super(sim);
        setFileName(fname);
    }
    
    /**
     * sets and opens a new file for the model output
     * @param fname 
     */
    private void setFileName(String fname) {
        outFile          = new FileOutput(fname + "_modelState.txt");
        transmissionFile = new FileOutput(fname + "_transmissions.txt");
        allEventsFile    = new FileOutput(fname + "_allEvents.txt");
    }
    
    /**
     * sets the flag to print to log.info or not
     * @param p 
     */
    public void setPrintToLogInfo(boolean p) {
        this.printToLogInfo = p;
    }

    /**
     * start the observer - writes the initial state to the output log file if not null and optionally prints to screen
     */
    @Override
    public void started() {
        //log.info("BasicSIRObserver - started");
        
        IndividualBasedModel model  = ((SIRAmountManager)super.getProcess().getAmountManager()).getModel();
        String sirModelHeader       = model.modelStateHeader();
        sirModelHeader              = "Time" + model.getDelim() + sirModelHeader;
        
        String sirModelState        = model.modelStateString();
        sirModelState               =  super.getProcess().getCurrentTime() + model.getDelim() + sirModelState;
        
        if (outFile != null) {
            outFile.write(sirModelHeader + eol);
            outFile.write(sirModelState + eol);
        }
        
        if (printToLogInfo) {
            log.info("IndividualSIRObserver - started with model state = "+sirModelState);
        }
    }

    /**
     * Gets called AFTER event.  This observer records the state of the BasicSIR model - writes to file and prints to log.info
     */
    @Override
    public void step() {
        
        SIRAmountManager        amountManager = (SIRAmountManager)super.getProcess().getAmountManager();
        IndividualBasedModel    model         = amountManager.getModel();
        String                  sirModelState = model.modelStateString();
        double                  time          = super.getProcess().getCurrentTime();
        sirModelState =  "" + time + model.getDelim() + sirModelState;
        
        if (outFile != null) {
            outFile.write(sirModelState + eol);
        }
        
       
        if (printToLogInfo) {
            log.info("IndividualSIRObserver - after step "+tempCounter+" model state = "+sirModelState);
        }
        
        for (EventDone ev : amountManager.getEventsJustDone()) {
             ev.setTime(time);
             String evTxt = ev.toString();
             if (printToLogInfo) log.info("IndividualSIRObserver - event done = "+evTxt);
             
             if ( ( (ev.eventType == SIREventType.INFECTION) ||
                     (ev.eventType == SIREventType.EXPOSURE) )  && 
                             (transmissionFile != null) ) {
                 transmissionFile.write(evTxt + eol);
             }
             
             if (allEventsFile != null) {
                 allEventsFile.write(evTxt + eol);
             }
        }
        
    }

    /**
     * closes the log file output if it is open
     */
    @Override
    public void finished() {
        
        if (outFile != null) {
            outFile.flush();
            outFile.close();
        }
        
        if (transmissionFile != null) {
            transmissionFile.flush();
            transmissionFile.close();
        }
        
        if (allEventsFile != null) {
            allEventsFile.flush();
            allEventsFile.close();
        }
        
        if (printToLogInfo) {
            log.info("IndividualSIRObserver - finished");
        }
    }

    @Override
    public void theta(double thetaTime, Collection<Object> events) {
        
        if (printToLogInfo) {
            log.info("IndividualSIRObserver - theta");
        }
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
     /**
     * Gets called BEFORE an event is triggered.
     * Has no effect in this implementation, apart from to optionally print to log.info
     * @param event the event which is supposed to fire
     * @param tau   the time the event occurs
     * @param times the number of firings
     */
    @Override
    public void observeEvent(SimulationEvent event, double tau, int times) {
        
        tempCounter++;
        
        if (printToLogInfo) {
            log.info("----------");
            log.info("IndividualSIRObserver - about to do step "+tempCounter+" "+
                    event.toString()+" tau="+tau+" times="+times);
        }
        
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
