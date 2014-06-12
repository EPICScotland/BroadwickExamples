/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package epic.sir;


import lombok.extern.slf4j.Slf4j;
import java.util.*;

/**
 * Class to represent the possible allowed states of individuals, and the transition rates between these states.
 * @author Samantha Lycett
 * @version 9 June 2014
 */
@Slf4j
public class SIRRates {
    
    // possible states
    // SUSCEPTIBLE, EXPOSED, INFECTED, RECOVERED, IMMUNE;
    
    String                          modelType;
    List<IndividualStateType>       allowedStates;
    SIREventType[][]                eventNames;
    
@SuppressWarnings("UseOfObsoleteCollectionType")
    Hashtable<SIREventType,Double>  individualRates;
    
    public SIRRates(String modelType) {
        setModelType(modelType);
    }
    
    //////////////////////////////////////////////////////////////////////
    
    private void setModelType(String modelType) {
        this.modelType = modelType;
        
        if (modelType.equals("SI")) {
            setSI();
        } else if (modelType.equals("SEI")) {
            setSEI();
        } else if (modelType.equals("SIR")) {
            setSIR();
        } else if (modelType.equals("SEIR")) {
            setSEIR();
        } else if (modelType.equals("SIRM")) {
            setSIRM();
        } else if (modelType.equals("SEIRM")) {
            setSEIRM();
        } else {
            log.error("Sorry cant set model type = "+modelType+" but note for SIS use SI etc");
        }
    
        individualRates = new Hashtable<SIREventType,Double>();
        
        
    }
    
    public String getModelType() {
        return modelType;
    }
    
    
    public List<IndividualStateType> getAllowedStates() {
        return allowedStates;
    }
    
    /**
     * returns the next state in the sequence,
     * e.g. if input S then get E or I depending on the model
     * also allows return of S in e.g. SI(S) models
     * @param s1
     * @return state
     */
    public IndividualStateType getNextAllowedState(IndividualStateType s1) {
        int i = allowedStates.indexOf(s1);
        int j = i++;
        if (j > allowedStates.size()) {
            j = 0;
        }
        
        IndividualStateType s2 = allowedStates.get(j);
        return s2;
    }
    
    public IndividualStateType whoTheInfectedInfect() {
        int i = allowedStates.indexOf(IndividualStateType.INFECTED);
        if (i > 0) {
            return allowedStates.get(i-1);
        } else {
            return null;
        }
    }
    
    public IndividualStateType getLastState() {
        return allowedStates.get( allowedStates.size() - 1);
    }
     
    //////////////////////////////////////////////////////////////////////
  
@SuppressWarnings("Convert2Diamond")  
    private void setSI() {
        allowedStates = new ArrayList<IndividualStateType>();
        allowedStates.add(IndividualStateType.SUSCEPTIBLE);
        allowedStates.add(IndividualStateType.INFECTED);
        
        eventNames       = new SIREventType[2][2];
        eventNames[0][1] = SIREventType.INFECTION;
        eventNames[1][0] = SIREventType.SUSCEPTIBILITY;
        //eventNames[1][1] = SIREventType.INFECTION;
        eventNames[0][0] = null;
    }
    
@SuppressWarnings("Convert2Diamond")
    private void setSEI() {
        allowedStates = new ArrayList<IndividualStateType>();
        allowedStates.add(IndividualStateType.SUSCEPTIBLE);
        allowedStates.add(IndividualStateType.EXPOSED);
        allowedStates.add(IndividualStateType.INFECTED);
        
        eventNames      = new SIREventType[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                eventNames[i][j] = null;
            }
        }
        eventNames[0][1] = SIREventType.EXPOSURE;
        eventNames[1][2] = SIREventType.BECOME_INFECTIOUS;
        eventNames[2][1] = SIREventType.INFECTION;
        eventNames[2][0] = SIREventType.SUSCEPTIBILITY;
    }
    
    
@SuppressWarnings("Convert2Diamond")
    private void setSIR() {
        allowedStates = new ArrayList<IndividualStateType>();
        allowedStates.add(IndividualStateType.SUSCEPTIBLE);
        allowedStates.add(IndividualStateType.INFECTED);
        allowedStates.add(IndividualStateType.RECOVERED);
        
        eventNames      = new SIREventType[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                eventNames[i][j] = null;
            }
        }
        eventNames[0][1] = SIREventType.INFECTION;
        eventNames[1][2] = SIREventType.RECOVERY;
        //eventNames[1][1] = SIREventType.INFECTION;
        eventNames[2][0] = SIREventType.SUSCEPTIBILITY;
    }
    
@SuppressWarnings("Convert2Diamond")
    private void setSIRM() {
        allowedStates = new ArrayList<IndividualStateType>();
        allowedStates.add(IndividualStateType.SUSCEPTIBLE);
        allowedStates.add(IndividualStateType.INFECTED);
        allowedStates.add(IndividualStateType.RECOVERED);
        allowedStates.add(IndividualStateType.IMMUNE);
        
        eventNames      = new SIREventType[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                eventNames[i][j] = null;
            }
        }
        eventNames[0][1] = SIREventType.INFECTION;      // S -> I
        eventNames[1][2] = SIREventType.RECOVERY;       // I -> R
        //eventNames[1][1] = SIREventType.INFECTION;      // I -> I (diff individuals)
        //eventNames[2][0] = SIREventType.SUSCEPTIBILITY; // R -> S
        eventNames[2][3] = SIREventType.IMMUNITY;       // R -> M
        eventNames[3][0] = SIREventType.SUSCEPTIBILITY; // M -> S
    }
        
@SuppressWarnings("Convert2Diamond")
    private void setSEIR() {
        allowedStates = new ArrayList<IndividualStateType>();
        allowedStates.add(IndividualStateType.SUSCEPTIBLE);
        allowedStates.add(IndividualStateType.EXPOSED);
        allowedStates.add(IndividualStateType.INFECTED);
        allowedStates.add(IndividualStateType.RECOVERED);
        
        eventNames      = new SIREventType[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                eventNames[i][j] = null;
            }
        }
        eventNames[0][1] = SIREventType.EXPOSURE;               // S -> E
        eventNames[1][2] = SIREventType.BECOME_INFECTIOUS;      // E -> I
        eventNames[2][1] = SIREventType.INFECTION;              // I -> E (diff individuals)
        eventNames[2][3] = SIREventType.RECOVERY;               // I -> R
        eventNames[3][0] = SIREventType.SUSCEPTIBILITY;         // R -> S
    }
    

@SuppressWarnings("Convert2Diamond")    
    private void setSEIRM() {
        allowedStates = new ArrayList<IndividualStateType>();
        allowedStates.add(IndividualStateType.SUSCEPTIBLE);
        allowedStates.add(IndividualStateType.EXPOSED);
        allowedStates.add(IndividualStateType.INFECTED);
        allowedStates.add(IndividualStateType.RECOVERED);
        allowedStates.add(IndividualStateType.IMMUNE);
        
        eventNames      = new SIREventType[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                eventNames[i][j] = null;
            }
        }
        eventNames[0][1] = SIREventType.EXPOSURE;               // S -> E
        eventNames[1][2] = SIREventType.BECOME_INFECTIOUS;      // E -> I
        eventNames[2][1] = SIREventType.INFECTION;              // I -> E (diff individuals)
        eventNames[2][3] = SIREventType.RECOVERY;               // I -> R
        //eventNames[3][0] = SIREventType.SUSCEPTIBILITY;         // R -> S
        eventNames[3][4] = SIREventType.IMMUNITY;               // R -> M
        eventNames[4][0] = SIREventType.SUSCEPTIBILITY;         // M -> S
    }
    
    ///////////////////////////////////////////////////////////////////////
    
    public void setRate(IndividualStateType s1, IndividualStateType s2, double rr) {
        
        int i = allowedStates.indexOf(s1);
        int j = allowedStates.indexOf(s2);
        
        SIREventType ename = eventNames[i][j];
        if (ename != null) {
            individualRates.put(ename, rr);
        } else {
            // this is not allowed and probably a mistake
            log.error("Sorry cant set rate for "+s1+" to "+s2);
        }
                
    }
    
    public SIREventType getEventType(IndividualStateType s1, IndividualStateType s2) {
        int i = allowedStates.indexOf(s1);
        int j = allowedStates.indexOf(s2);
        
        SIREventType ename = eventNames[i][j];
        return ename;
    }
    
    public double getRate(IndividualStateType s1,  IndividualStateType s2) {
        int i = allowedStates.indexOf(s1);
        int j = allowedStates.indexOf(s2);
        
        SIREventType ename = eventNames[i][j];
        
        if ( (ename != null) && individualRates.containsKey(ename)  ) {
            return individualRates.get(ename);
        } else {
            log.error("Sorry no rate exists for "+s1+" to "+s2);
            return 0.0;
        }
    }
    
    public double getRate(SIREventType etype) {
        
        if ((etype != null) && individualRates.containsKey(etype)) {
            return individualRates.get(etype);
        } else {
            log.error("Sorry no rate exists for "+etype);
            return 0.0;
        }
        
    }
    
    public Collection<SIREventType> getEventTypes() {
        return individualRates.keySet();
    }
    
}
