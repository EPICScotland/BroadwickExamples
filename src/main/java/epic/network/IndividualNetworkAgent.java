/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package epic.network;

import broadwick.graph.Vertex;
import epic.agents.Agent;
import epic.sir.IndividualStateType;

/**
 * Class to represent an individual which is also a network node.
 * @author Samantha Lycett
 * @version 11 June 2014
 */
public class IndividualNetworkAgent extends Vertex implements Agent {
    
    ////////////////////////////////////////////////////////////////
    // class methods to handle unique agent id numbers
    private static long nextID = 0;
    
    private static long getNextID() {
        nextID++;
        return nextID;
    }
    //////////////////////////////////////////////////////////////
    
   
    //////////////////////////////////////////////////////////////
    // instance variables and methods
    
    IndividualStateType agentState = IndividualStateType.SUSCEPTIBLE;
    final long          idnumber;
    Location            location;
    
    
    public IndividualNetworkAgent(String name) {
        super(name);
        idnumber = getNextID();
        location = new Location(name, 0, 0, LocationType.LATLONG);
    }
    
    ///////////////////////////////////////////////////////////////
    
    public void setLocation(Location loc) {
        this.location = loc;
    }
    
    public Location getLocation() {
        return location;
    }
    
    
    ///////////////////////////////////////////////////////////////
    // methods from Agent
    
    @Override
    public void setName(String name) {
        super.id = name;
    }

    @Override
    public String getName() {
        return super.id;
    }

    @Override
    public long getID() {
        return idnumber;
    }

    @Override
    public void setState(String state) {
        this.agentState = IndividualStateType.valueOf(state);
    }

    @Override
    public String getState() {
        return agentState.toString();
    }
    
    ///////////////////////////////////////////////////////////////////////
    // hashCode and equals generated on the unique idnumber

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (int) (this.idnumber ^ (this.idnumber >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IndividualNetworkAgent other = (IndividualNetworkAgent) obj;
        if (this.idnumber != other.idnumber) {
            return false;
        }
        return true;
    }
    
    
    //////////////////////////////////
    
    
    
    public String toNameLocationState() {
        
        String line = getName() + Location.delim + location.toString() + Location.delim + getState();
        return line;
    }
    
    public String toNameLocation() {
        String line = getName() + Location.delim + location.toString();
        return line;
    }
    
    
    public String toNameLocationHeader() {
        return "Name" + Location.delim + location.toHeader();
    }
    
    public String toNameLocationStateHeader() {
        return "Name" + Location.delim + location.toHeader() + Location.delim + "State"; 
    }
    
}
