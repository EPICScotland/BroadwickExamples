/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package epic.basic;

import broadwick.model.Model;
import broadwick.stochastic.*;
import broadwick.stochastic.algorithms.*;
import broadwick.rng.*;


import lombok.extern.slf4j.Slf4j;

/**
 * This is a Basic SIR model with stochastic simulation algorithm
 * it can be run under the Broadwick framework
 * 
 *  If you want to run with this model, remember to change the Broadwick.xml first
 *  Or use Broadwick_with_BasicSIRModel.xml
 *  to run in NetBean change Project Properties under Run to use Broadwick_with_BasicSIRModel.xml
 * 
 * @author Samantha Lycett
 * @version 9 April 2014
 */
@Slf4j
public class BasicSIRModel extends Model {
    
    StochasticSimulator simulator;
    TransitionKernel    kernel;
    AmountManager       amountManager;
    Observer            observer;
    
    @Override
    public void init() {
        
        log.info("BasicSIRModel - init");
        
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
        double[]    rates   = new double[2];
        rates[0]            = this.getParameterValueAsDouble("beta");
        rates[1]            = this.getParameterValueAsDouble("gamma");
        BasicSIR sirModel   = new BasicSIR(N, rates);
        sirModel.initialiseWithInfected( initI );
        
        // set up amount manager with SIR model and transition kernel
        amountManager = new BasicSIRAmountManager( sirModel, kernel );
        
        
        
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
        
        SimulationController controller = new BasicSIRController(maxTime, sirModel);
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
        } catch (Exception e) {
            log.info("Seed not set in xml, using own value");
            seed = (new RNG()).getInteger(0, 9999999);
            simulator.setRngSeed(seed);
        }
        
        //////////////////////////////////////////////////////////////////
        // INITIALISE OBSERVER
        //////////////////////////////////////////////////////////////////
        
        // set up screen and file logging
        try {
            // if filename in xml then write to file
            String outName = this.getParameterValue("filename") + ".txt";
            observer = new BasicSIRObserver(simulator, outName);
        } catch (Exception e) {
            // if no filename present then just output to screen
            observer = new BasicSIRObserver(simulator);
            ((BasicSIRObserver)observer).setPrintToLogInfo(true);
        }
        
        try {
            // if verbose in xml then set this (log.info to screen)
            boolean ans = this.getParameterValueAsBoolean("verbose");
            ((BasicSIRObserver)observer).setPrintToLogInfo(ans);
            ((BasicSIRAmountManager)amountManager).setPrintToLogInfo(ans);
        } catch (Exception e) {
            ((BasicSIRAmountManager)amountManager).setPrintToLogInfo(false);
        }
        
        // add observer to simulator
        simulator.addObserver(observer);
        
        
        /////////////////////////////////////////////////////////////////////////////////////
        
        // echo parameters that have read in - check that they are set in appropriate objects
        
        //simulation parameters
        
        log.info("seed\t= "+seed);
        log.info("maxTime\t= "+((BasicSIRController)simulator.getController()).getMaxTime());
        log.info("tauStep\t= "+tauStep);
        
        // model paramters
        log.info("N\t= "+((BasicSIRAmountManager)amountManager).getModel().getN());
        log.info("initI\t= "+((BasicSIRAmountManager)amountManager).getModel().getNumberI());
        log.info("beta\t= "+((BasicSIRAmountManager)amountManager).getModel().stateRates[0]);
        log.info("gamma\t= "+((BasicSIRAmountManager)amountManager).getModel().stateRates[1]);
        
        // add first events to kernel
        ((BasicSIRAmountManager)amountManager).initialiseTransitionKernelWithFirstEvents();
    }

    @Override
    public void run() {
        log.info("BasicSIRModel - run");
        simulator.run();    
    }

    @Override
    public void finalise() {
        log.info("BasicSIRModel - final simulation time = "+simulator.getCurrentTime());     
    }
    
}
