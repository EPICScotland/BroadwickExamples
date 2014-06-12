/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package epic.basic;

import broadwick.stochastic.Observer;
import broadwick.stochastic.SimulationEvent;
import broadwick.stochastic.StochasticSimulator;
import broadwick.io.FileOutput;
import java.util.Collection;

import lombok.extern.slf4j.Slf4j;

/**
 * BasicSIRObserver writes model state information to an output log file if the filename (fname) is set
 * BasicSIRObserver writes model state information to the log.info stream about the BasicSIR model if printToLogInfo = true
 * @author Samantha Lycett
 * @version 9 April 2014
 */
@Slf4j
public class BasicSIRObserver extends Observer {
    
    int         tempCounter = 0;            // temp counter for events useful for developement of code
    FileOutput  outFile;                    // if not null then model state vs time written to separate file
    boolean     printToLogInfo = true;      // if true then prints verbose output to log.info
    String      eol            = System.getProperty("line.separator"); //"\n";

     /**
     * Comments from Observer class
     * Creates an observer dedicated to one process. The observer is NOT registered at the process, you have to call
     * Simulator.addObserver(Observer) in order to do this.
     * @param sim the process
     */
    public BasicSIRObserver(StochasticSimulator sim) {
        super(sim);
    }
    
    /**
     * Creates an observer dedicated to one process
     * (but this observer is not registered at the process, you have to do that separately)
     * also opens a log file for the model output with the filename = fname
     * @param sim
     * @param fname 
     */
    public BasicSIRObserver(StochasticSimulator sim, String fname) {
        super(sim);
        setFileName(fname);
    }
    
    /**
     * sets and opens a new file for the model output
     * @param fname 
     */
    private void setFileName(String fname) {
        outFile = new FileOutput(fname);
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
        
        BasicSIR model = ((BasicSIRAmountManager)super.getProcess().getAmountManager()).getModel();
        String sirModelHeader = model.modelStateHeader();
        sirModelHeader = "Time" + model.getDelim() + sirModelHeader;
        
        String sirModelState = model.modelStateString();
        sirModelState =  super.getProcess().getCurrentTime() + model.getDelim() + sirModelState;
        
        if (outFile != null) {
            outFile.write(sirModelHeader + eol);
            outFile.write(sirModelState + eol);
        }
        
        if (printToLogInfo) {
            log.info("BasicSIRObserver - started with model state = "+sirModelState);
        }
    }

    /**
     * Gets called AFTER event.  This observer records the state of the BasicSIR model - writes to file and prints to log.info
     */
    @Override
    public void step() {
        //log.info("BasicSIRObserver - step");
        
        BasicSIR model = ((BasicSIRAmountManager)super.getProcess().getAmountManager()).getModel();
        String sirModelState = model.modelStateString();
        sirModelState =  super.getProcess().getCurrentTime() + model.getDelim() + sirModelState;
        
        if (outFile != null) {
            outFile.write(sirModelState + eol);
        }
        
        if (printToLogInfo) {
            log.info("BasicSIRObserver - after step "+tempCounter+" model state = "+sirModelState);
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
        
        if (printToLogInfo) {
            log.info("BasicSIRObserver - finished");
        }
    }

    @Override
    public void theta(double thetaTime, Collection<Object> events) {
        
        if (printToLogInfo) {
            log.info("BasicSIRObserver - theta");
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
            //log.info("BasicSIRObserver - observeEvent");
            log.info("BasicSIRObserver - about to do step "+tempCounter+" "+
                    event.toString()+" tau="+tau+" times="+times);
        }
        
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
