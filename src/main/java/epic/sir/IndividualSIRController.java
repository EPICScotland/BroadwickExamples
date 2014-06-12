/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package epic.sir;

//import broadwick.rng.RNG;
import broadwick.stochastic.SimulationController;
import broadwick.stochastic.StochasticSimulator;

/**
 *
 * @author Samantha Lycett
 * @version 9 June 2014
 */
public class IndividualSIRController implements SimulationController {
    
    //public static RNG       random          = new RNG();
    
    
    double                  maxTime = 1000.0;
    IndividualBasedModel    model;
    
    public IndividualSIRController(double maxTime, IndividualBasedModel model) {
        this.maxTime = maxTime;
        this.model   = model;
    }
    
    public double getMaxTime() {
        return maxTime;
    }
    
    public void setMaxTime(double maxTime) {
        this.maxTime = maxTime;
    }
    
    
    /**
     * returns true to continue on to the next step
     * returns false if max time reached, or there are no more infecteds, or all are recovered
     * @param process
     * @return continues with simulation
     */
    @Override
    public boolean goOn(StochasticSimulator process) {
        
        if (process.getCurrentTime() >= maxTime) {
            // if exceed maxTime then stop
            return false;
            
        } else {
            
            // stop depending on model state
            
            int numI = model.getNumberOfAgentsInState(IndividualStateType.INFECTED);
            numI     = numI + model.getNumberOfAgentsInState(IndividualStateType.EXPOSED);
            
            int numR = model.getNumberOfAgentsInState( model.rates.getLastState() );
            
            if ( numI <= 0.5 ) {
                // if no more infecteds then stop
                return false;
            } else if ( numR > (model.getN()-0.5) ) {
                // if all are recovered, or immune then stop
                return false;
            } else {
                // else carry on
                return true;
            }
        }
        
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
