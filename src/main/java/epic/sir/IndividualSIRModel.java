/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package epic.sir;

import broadwick.model.Model;
import broadwick.rng.RNG;
import broadwick.stochastic.AmountManager;
import broadwick.stochastic.Observer;
import broadwick.stochastic.SimulationController;
import broadwick.stochastic.StochasticSimulator;
import broadwick.stochastic.TransitionKernel;
import broadwick.stochastic.algorithms.GillespieSimple;
import broadwick.stochastic.algorithms.TauLeapingFixedStep;
import java.util.List;

import lombok.extern.slf4j.Slf4j;


/**
 * This is an Individual Based SIR type model with stochastic simulation algorithm, 
 * it can be run under the Broadwick framework
 * 
 * If you want to run with this model, remember to change the Broadwick.xml first. 
 * Or use Broadwick_with_IndividualSIRModel.xml  
 * To run in NetBeans change Project Properties under Run to use Broadwick_with_IndividualSIRModel.xml
 * 
 * @author Samantha Lycett
 * @version 11 April 2014
 * @version 9 June 2014 - included SEIR etc in initialisation
 */
@Slf4j
public class IndividualSIRModel extends Model {
    
    
    
    StochasticSimulator simulator;
    TransitionKernel    kernel;
    AmountManager       amountManager;
    Observer            observer;
    
    @Override
    public void init() {
        
        log.info("IndividualSIRModel - init");
        
        /////////////////////////////////////////////////////////////////
        // INITIALISE TRANSITION KERNEL
        /////////////////////////////////////////////////////////////////
        
        // an empty kernel for now
        kernel = new TransitionKernel();
        kernel.clear();
        
        
        /////////////////////////////////////////////////////////////////
        // INITIALISE AMOUNT MANAGER
        /////////////////////////////////////////////////////////////////
        
        // set up SIR model
        
        // read in parameters from xml file
        int         N       = this.getParameterValueAsInteger("N");
        int         initI   = this.getParameterValueAsInteger("initI");
        String      modelType=this.getParameterValue("modelType");
        
        /*
        SIRRates    rates   = new SIRRates(modelType);
            double      beta    = this.getParameterValueAsDouble("beta");
            rates.setRate(IndividualStateType.SUSCEPTIBLE, IndividualStateType.INFECTED, beta);
            
            double      gamma   = this.getParameterValueAsDouble("gamma");
            rates.setRate(IndividualStateType.INFECTED, IndividualStateType.RECOVERED, gamma);
        */
        
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
                //rates.setRate(IndividualStateType.INFECTED, IndividualStateType.EXPOSED, beta);
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
        
        IndividualBasedModel sirModel   = new IndividualBasedModel();
        sirModel.setRates(rates);
        sirModel.intialiseWithInfected(N, initI);
        
        // set up amount manager with SIR model and transition kernel
        amountManager = new SIRAmountManager( sirModel, kernel );
        
        
        
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
    
        // add controller, this stops the simulation after a max time is reached, or no more infecteds, or all recovered        
        // read in maxTime from xml file
        double maxTime = Double.MAX_VALUE;
        try {
            maxTime = this.getParameterValueAsDouble("maxTime");
        } catch (Exception e) {
        }
        
        SimulationController controller = new IndividualSIRController(maxTime, sirModel);
        simulator.setController(controller);
        
        // read in seed from xml file
        // if no seed present then generate one
        // and re-seed with this (so that you know what it is).
        int seed;
        try {
            seed = this.getParameterValueAsInteger("seed");
            simulator.setRngSeed(seed);
            //log.info("Seed read in from xml = "+seed);
            //log.info("Seed from rng = "+(new RNG()).getSeed());
            IndividualBasedModel.random = new RNG();
            IndividualBasedModel.random.seed(seed);
        } catch (Exception e) {
            log.info("Seed not set in xml, using own value");
            IndividualBasedModel.random  = new RNG();
            seed    = (new RNG()).getInteger(0, 9999999);
            IndividualBasedModel.random.seed(seed);
            simulator.setRngSeed(seed);
        }
        
        //////////////////////////////////////////////////////////////////
        // INITIALISE OBSERVER
        //////////////////////////////////////////////////////////////////
        
        // set up screen and file logging
        try {
            // if filename in xml then write to file
            String outName = this.getParameterValue("filename");
            observer = new IndividualSIRObserver(simulator, outName);
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
        
        
        /////////////////////////////////////////////////////////////////////////////////////
        
        // echo parameters that have read in - check that they are set in appropriate objects
        
        //simulation parameters
        
        log.info("seed\t= "+seed);
        log.info("maxTime\t= "+((IndividualSIRController)simulator.getController()).getMaxTime());
        log.info("tauStep\t= "+tauStep);
        
        // model paramters
        log.info("N\t= "+((SIRAmountManager)amountManager).getModel().getN());
        log.info("initI\t= "+((SIRAmountManager)amountManager).getModel().getNumberOfAgentsInState(IndividualStateType.INFECTED));
        
        // add first events to kernel
        ((SIRAmountManager)amountManager).initialiseTransitionKernelWithFirstEvents();
    }

    @Override
    public void run() {
        log.info("IndividualSIRModel - run");
        simulator.run();    
    }

    @Override
    public void finalise() {
        log.info("IndividualSIRModel - final simulation time = "+simulator.getCurrentTime());
        log.info("IndividualSIRModel - "+amountManager.toVerboseString());
    }
    
}
