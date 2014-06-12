/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 *  If you want to run with this model, remember to change the Broadwick.xml first
 *  Or use Broadwick_with_DummyModel.xml - do this in Project Properties under Run
 */

package epic.broadwickexamples;

import broadwick.model.Model;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Samantha Lycett
 */
@Slf4j
public class DummyModel extends Model {
    
    // paramters of the model
    @Getter
    @Setter
    private String stringParam;
    
    @Getter
    @Setter
    private int    intParam;
    
    @Getter
    @Setter
    private double doubleParam;
    
    //////////////////////////////////////////////////////////////////////
    
    private void setParametersFromXMLFile() {
        this.setStringParam(this.getParameterValue("stringParam"));
        this.setIntParam(this.getParameterValueAsInteger("intParam"));
        this.setDoubleParam(this.getParameterValueAsDouble("doubleParam"));
    }
    
    private void logParameters() {
        log.info("stringParam="+stringParam);
        log.info("intParam="+intParam);
        log.info("doubleParam="+doubleParam);
    }
    
    //////////////////////////////////////////////////////////////////////

    @Override
    public void init() {
        log.info("Initialise Dummy Model");
        setParametersFromXMLFile();
    }

    @Override
    public void run() {    
        log.info("Run Dummy Model");
        logParameters();
        
    }

    @Override
    public void finalise() {
        log.info("Finalise Dummy Model");
        
        
        log.info("END");
        System.out.println("** END **");
    }
    
}
