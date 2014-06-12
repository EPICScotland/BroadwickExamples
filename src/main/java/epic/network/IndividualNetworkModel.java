/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package epic.network;

import broadwick.model.Model;
import broadwick.rng.RNG;
import broadwick.stochastic.AmountManager;
import broadwick.stochastic.Observer;
import broadwick.stochastic.SimulationController;
import broadwick.stochastic.StochasticSimulator;
import broadwick.stochastic.TransitionKernel;
import broadwick.stochastic.algorithms.GillespieSimple;
import broadwick.stochastic.algorithms.TauLeapingFixedStep;

import epic.sir.IndividualSIRController;
import epic.sir.IndividualSIRObserver;
import epic.sir.IndividualStateType;
import epic.sir.SIRAmountManager;
import epic.sir.SIRRates;
import java.util.List;

import lombok.extern.slf4j.Slf4j;


/**
 * This is an Individual Based SIR type model with infections over a network and stochastic simulation algorithm.
 * It can be run under the Broadwick framework.
 * This is very similar to epic.sir.IndividualSIRModel, except that the individuals are in a network structure, 
 * consequently this model uses many of the classes in epic.sir.
 * 
 * If you want to run with this model, remember to change the Broadwick.xml first.  
 * Or use Broadwick_with_IndividualNetworkModel.xml  
 * To run in NetBeans change Project Properties under Run to use Broadwick_with_IndividualNewtorkModel.xml
 * 
 * @author Samantha Lycett
 * @version 9 June 2014
 */

@Slf4j
public class IndividualNetworkModel extends Model {
    
    static RNG          random = null;
    
    StochasticSimulator simulator;
    TransitionKernel    kernel;
    AmountManager       amountManager;
    Observer            observer;
    Observer            netObserver;

    @Override
    public void init() {
    
        log.info("IndividualNetworkModel - init");
     
        /*
         try {
            int seed = this.getParameterValueAsInteger("seed");
            random.setSeed(seed);
        } catch (Exception e) {
            log.info("Seed not set in xml, using own value");
            log.info("Seed from rng = "+random.getSeed());
        }
        */
        
        
        /////////////////////////////////////////////////////////////////
        // INITIALISE TRANSITION KERNEL
        /////////////////////////////////////////////////////////////////
        
        // an empty kernel for now
        kernel = new TransitionKernel();
        kernel.clear();
        
        
        /////////////////////////////////////////////////////////////////
        // INITIALISE AMOUNT MANAGER
        /////////////////////////////////////////////////////////////////
        
        // read in parameters from xml file
        
        // set up SIR model
        
        String      modelType=this.getParameterValue("modelType");        
        SIRRates    rates   = new SIRRates(modelType);
        
        if ( rates.getAllowedStates().contains(IndividualStateType.EXPOSED) ) {
            try {
                // alpha is rate of E -> I
                double      alpha   = this.getParameterValueAsDouble("alpha");
                rates.setRate(IndividualStateType.EXPOSED, IndividualStateType.INFECTED, alpha);
            } catch (Exception e) {
            }
        }
        
        try {
            // beta is the main rate of becoming infected
            double      beta    = this.getParameterValueAsDouble("beta");
            if ( rates.getAllowedStates().contains(IndividualStateType.EXPOSED) ) {
                // S -> E and E -> I
                rates.setRate(IndividualStateType.SUSCEPTIBLE, IndividualStateType.EXPOSED, beta);
                rates.setRate(IndividualStateType.INFECTED, IndividualStateType.EXPOSED, beta);
            } else {
                // S -> I
                rates.setRate(IndividualStateType.SUSCEPTIBLE, IndividualStateType.INFECTED, beta);
            }
        } catch (Exception e) {
        }    
        
        if ( rates.getAllowedStates().contains(IndividualStateType.RECOVERED) ) {
            try {
                // gamma is the rate of recovery
                double      gamma   = this.getParameterValueAsDouble("gamma");
                rates.setRate(IndividualStateType.INFECTED, IndividualStateType.RECOVERED, gamma);
            } catch (Exception e) {
            }
        }
        
         if ( rates.getAllowedStates().contains(IndividualStateType.IMMUNE) ) {
            try {
                // immunity is the rate of immunity
                double                      immunity    = this.getParameterValueAsDouble("immunity");
                List<IndividualStateType>   allowed     = rates.getAllowedStates();
                rates.setRate(allowed.get( allowed.size()-2 ), IndividualStateType.IMMUNE, immunity);
            } catch (Exception e) {
            }
        }
         
        try {
            double     sus = this.getParameterValueAsDouble("susceptibility");
            List<IndividualStateType>   allowed     = rates.getAllowedStates();
            rates.setRate(allowed.get( allowed.size()-1 ), allowed.get(0), sus);
        } catch (Exception e) {
            log.info("Optional parameter susceptibility (=wanning immunity) is not set, but this is OK");
        }
        
        ///////////////////////////////////////////////////////////////////
        // NETWORK INITIALISATION
        ///////////////////////////////////////////////////////////////////
        
        NetworkOfIndividualsModel netModel   = new NetworkOfIndividualsModel();
        netModel.setRates(rates);
        
        // the network type = RANDOM, LINE, STAR, RING, GRID, GENERAL;
        NetworkType  netType = NetworkType.valueOf(this.getParameterValue("networkType"));
        netModel.setNetworkType( netType );
        
        // number of agents / network nodes
        if (netType != NetworkType.GENERAL) {
            try {
                int N       = this.getParameterValueAsInteger("N");
                netModel.setParameter("N", N);
            } catch (Exception e) {
            }
        }
        
        // number of initial infecteds
        int         initI   = 1;
        try {
            initI = this.getParameterValueAsInteger("initI");
        } catch (Exception e) {
        }
        
        if (netType == NetworkType.RANDOM) {
            // p parameter for RANDOM network
            try {
                double p    = this.getParameterValueAsDouble("p");
                netModel.setParameter("p",p);
            } catch (Exception e) {
            }
        }
        
        if (netType == NetworkType.GENERAL) {
            // filename parameter for GENERAL network
            try {
                String locName = this.getParameterValue("locationsFile");
                netModel.setLocationsFileName(locName);
                String linkName = this.getParameterValue("linksFile");
                netModel.setLinksFileName(linkName);
                String locType = this.getParameterValue("locationType");
                netModel.setLocationType(locType);
                
                log.info("Network Model locationsFile = "+netModel.network.getLocationsFileName());
                log.info("Network Model linksFile = "+netModel.network.getLinksFileName());
                log.info("Network Model locationType = "+netModel.network.locType);
                
            } catch (Exception e) {
                log.error("Couldnt find parameters for GENERAL Network type");
            }
        }
        
        if (netType == NetworkType.GRID) {
            // M parameter for GRID network
            try {
                int M      = this.getParameterValueAsInteger("M");
                netModel.setParameter("M", M);
            } catch (Exception e) {
            }
        }
        
        // leave the network initialisation until after the simulator instantiation
        // to preserve the random number generators
        
        
        
        // set up amount manager with SIR model and transition kernel
        amountManager = new SIRAmountManager( netModel, kernel );
        
        /////////////////////////////////////////////////////////////////
        // INITIALISE SIMULATOR
        /////////////////////////////////////////////////////////////////
                
        // read in tau leap parameter from xml file
        int tauStep = 0;
        try {
            tauStep = this.getParameterValueAsInteger("tauStep");
        } catch (Exception e) {
        }
        
        // set up simulator
        // with amount manager and transition kernel
        // choose between fixed step tau leap (approximate) or exact gillespie algorithms for stochastic simulation
        
        if (tauStep > 0) {
            simulator = new TauLeapingFixedStep(amountManager, kernel, tauStep);
        } else {
            simulator = new GillespieSimple(amountManager, kernel);
        }
    
        // the problem with doing the seed setting here is that the network has already used the random number generator
        // so now all this is messed up
        // need to do it earlier in the initialisation process
        
        // read in seed from xml file
        // if no seed present then generate one
        // and re-seed with this (so that you know what it is).
        // note that new RNG() will return the static random number generator as created in simulator
        
        try {
            int seed = this.getParameterValueAsInteger("seed");
            simulator.setRngSeed(seed);
            //log.info("Seed read in from xml = "+seed);
            //log.info("Seed from rng = "+(new RNG()).getSeed());
        } catch (Exception e) {
            log.info("Seed not set in xml, using own value");
            
        }
        random = new RNG();
        log.info("Seed from rng = "+random.getSeed());
        
        // add controller, this stops the simulation after a max time is reached, or no more infecteds, or all recovered        
        // read in maxTime from xml file
        double maxTime = Double.MAX_VALUE;
        try {
            maxTime = this.getParameterValueAsDouble("maxTime");
        } catch (Exception e) {
        }
        

        
        
        // FINISH INITIALISING THE NETWORK
        // initialise the contact network with the network parameters
        netModel.initialise();
        
        // now start off with an initial number of infecteds
        netModel.initialiseWithInfecteds(initI);
        
        log.info("Number of susceptibles in network = "+
            netModel.getNumberOfAgentsInState(IndividualStateType.SUSCEPTIBLE));
        log.info("Number of infecteds in network = "+
                netModel.getNumberOfAgentsInState(IndividualStateType.INFECTED));
        
        // ADD CONTROLLER TO SIMULATOR - INCLUDE MAX TIME AND NETWORK MODEL
        SimulationController controller = new IndividualSIRController(maxTime, netModel);
        simulator.setController(controller);
        
        
        //////////////////////////////////////////////////////////////////
        // INITIALISE OBSERVER
        //////////////////////////////////////////////////////////////////
        
        // set up screen and file logging
        try {
            // if filename in xml then write to file
            String outName  = this.getParameterValue("filename");
            observer        = new IndividualSIRObserver(simulator, outName);
            netObserver     = new IndividualNetworkObserver(simulator, outName);
        } catch (Exception e) {
            // if no filename present then just output to screen
            observer = new IndividualSIRObserver(simulator);
            ((IndividualSIRObserver)observer).setPrintToLogInfo(true);
        }
        
        try {
            // if verbose in xml then set this (log.info to screen)
            boolean ans = this.getParameterValueAsBoolean("verbose");
            ((IndividualSIRObserver)observer).setPrintToLogInfo(ans);
            ((SIRAmountManager)amountManager).setPrintToLogInfo(ans);
        } catch (Exception e) {
            ((SIRAmountManager)amountManager).setPrintToLogInfo(false);
        }
        
        // add observer to simulator
        simulator.addObserver(observer);
        simulator.addObserver(netObserver);
        
        
        /////////////////////////////////////////////////////////////////////////////////////
        
        // echo parameters that have read in - check that they are set in appropriate objects
        
        //simulation parameters
        
        log.info("seed\t= "+random.getSeed());
        log.info("maxTime\t= "+((IndividualSIRController)simulator.getController()).getMaxTime());
        log.info("tauStep\t= "+tauStep);
        
        // model paramters
        log.info("N\t= "+((SIRAmountManager)amountManager).getModel().getN());
        log.info("initI\t= "+((SIRAmountManager)amountManager).getModel().getNumberOfAgentsInState(IndividualStateType.INFECTED));
        //log.info("beta\t= "+((SIRAmountManager)amountManager).getModel().stateRates[0]);
        //log.info("gamma\t= "+((SIRAmountManager)amountManager).getModel().stateRates[1]);
        
        // add first events to kernel
        ((SIRAmountManager)amountManager).initialiseTransitionKernelWithFirstEvents();
    
    }

    @Override
    public void run() {
        log.info("IndividualNetworkModel - run");
        simulator.run(); 
    }

    @Override
    public void finalise() {
        log.info("IndividualNetworkModel - final simulation time = "+simulator.getCurrentTime());
        log.info("IndividualNetworkModel - "+amountManager.toVerboseString());
    }
    
    
    
    
}
