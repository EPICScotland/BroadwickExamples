/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package epic.network;

import broadwick.io.FileOutput;
import broadwick.stochastic.Observer;
import broadwick.stochastic.SimulationEvent;
import broadwick.stochastic.StochasticSimulator;
import epic.sir.SIRAmountManager;
import java.util.Collection;

/**
 *
 * @author Samantha Lycett
 */
public class IndividualNetworkObserver extends Observer {
    
    private int         counter     = 0;
    private int         recordEvery = 1;
    private String      rootname    = "individualNetworkModel_output";
    
    
    /**
     * Comments from Observer class
     * Creates an observer dedicated to one process. The observer is NOT registered at the process, you have to call
     * Simulator.addObserver(Observer) in order to do this.
     * @param sim the process
     */
    public IndividualNetworkObserver(StochasticSimulator sim) {
        super(sim);
    }
    
    public IndividualNetworkObserver(StochasticSimulator sim, String fname) {
        super(sim);
        setRootname(fname);
    }

    ///////////////////////////////////////////////////////////////////////////
    
    private void setRootname(String fname) {
        this.rootname      = fname;
    }
    
    ///////////////////////////////////////////////////////////////////////////
    
    @Override
    public void started() {
        
        NetworkOfIndividualsModel model = (NetworkOfIndividualsModel)(((SIRAmountManager)super.getProcess().getAmountManager()).getModel());
        
        FileOutput locationsFile      = new FileOutput(rootname + "_locations.txt");
        locationsFile.write(model.toNameLocationsList());
        locationsFile.flush();
        locationsFile.close();
        
        FileOutput initialNetworkFile = new FileOutput(rootname + "_initialNetwork.net");
        initialNetworkFile.write(model.toNameLocationsEdgeList());
        initialNetworkFile.flush();
        initialNetworkFile.close();
        
        FileOutput currentStateFile = new FileOutput(rootname + "_individualStates_initial.txt");
        currentStateFile.write(model.toIndividualStateList());
        currentStateFile.flush();
        currentStateFile.close();
        
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void step() {
        
        if ( (counter % recordEvery) == 0) {
            NetworkOfIndividualsModel model = (NetworkOfIndividualsModel)(((SIRAmountManager)super.getProcess().getAmountManager()).getModel());
            
            String counterTxt = String.format("%09d", counter);
            FileOutput currentStateFile = new FileOutput(rootname + "_individualStates_"+counterTxt+".txt");
            currentStateFile.write(model.toIndividualStateList());
            currentStateFile.flush();
            currentStateFile.close();
        }
        
        counter++;
    }

    @Override
    public void finished() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            NetworkOfIndividualsModel model = (NetworkOfIndividualsModel)(((SIRAmountManager)super.getProcess().getAmountManager()).getModel());
            
            FileOutput finalStateFile = new FileOutput(rootname + "_individualStates_final.txt");
            finalStateFile.write(model.toIndividualStateList());
            finalStateFile.flush();
            finalStateFile.close();
    
    }

    @Override
    public void theta(double thetaTime, Collection<Object> events) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Gets called BEFORE an event is triggered.
     * Has no effect in this implementation
     */
    @Override
    public void observeEvent(SimulationEvent event, double tau, int times) {
        // Do nothing; only get output at step
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
