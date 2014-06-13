/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package epic.network;

import broadwick.graph.Edge;
//import broadwick.graph.Vertex;
//import broadwick.rng.RNG;
import broadwick.stochastic.SimulationEvent;
import java.util.Collection;
import epic.sir.IndividualBasedModel;
import epic.sir.IndividualSimulationState;
import epic.sir.IndividualStateType;
import epic.sir.SIREventType;
import epic.sir.SIREventWithRate;
import epic.agents.Agent;
import epic.io.*;
import epic.sir.EventDone;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * NetworkOfIndividualsModel contains individuals (individual network agents) in a network,
 * and infection (SIR) or exposure (SEIR) may only occur between connected individuals. 
 * Otherwise the behaviour is the same as IndividualBasedModel (a stochastic SIR/SEI in epic.sir).
 * @author Samantha Lycett
 * @version 9 June 2014
 */
@Slf4j
public class NetworkOfIndividualsModel extends IndividualBasedModel {
 
    ContactNetwork< IndividualNetworkAgent, Edge<IndividualNetworkAgent> > network;
    
    /////////////////////////////////////////////////////////////////////////
    // constructor
    
    public NetworkOfIndividualsModel() {
        super();
        network = new ContactNetwork<IndividualNetworkAgent, Edge<IndividualNetworkAgent>>();
    }
    
    /////////////////////////////////////////////////////////////////////////
    
    public ContactNetwork getNetwork() {
        return network;
    }
    
    public void setParameter(String paramName, double value) {
        network.setParameter(paramName, value);
    }
    
    public void setNetworkType(NetworkType type) {
        network.setNetworkType(type);
    }
    
    public void setLocationsFileName(String filename) {
        network.setLocationsFileName(filename);
    }
    
    public void setLinksFileName(String filename) {
        network.setLinksFileName(filename);
    }
    
    public void setLocationType(String locType) {
        network.setLocationType(LocationType.valueOf(locType));
    }
    
    /////////////////////////////////////////////////////////////////////////
    // Network structure initialisation  methods
    
    private void initialiseGeneral() {
        
        ReadLocationsFile locFile             = new ReadLocationsFile( );
        List<Location>               locs     = locFile.readFile(network.getLocationsFileName(), Location.delim, network.getLocationType());
        List<IndividualNetworkAgent> vertices = new ArrayList<IndividualNetworkAgent>();
        for (Location loc : locs) {
            IndividualNetworkAgent v = new IndividualNetworkAgent( network.getNewNodeId() );
            v.setLocation(loc);
            vertices.add(v);
        }
        log.info(""+vertices.size()+" locations read from file");
    
        ReadSimpleLinksFile linksFile           = new ReadSimpleLinksFile(  );
        List<List<String>>            links     = linksFile.readFile(network.getLinksFileName(), Location.delim);
        log.info(""+links.size()+" links read from file");
        
        for (List<String> aLink : links) {
            Location dummy1 = new Location(aLink.get(0), 0, 0, network.getLocationType());
            Location dummy2 = new Location(aLink.get(1), 0, 0, network.getLocationType());
            int pos1        = locs.indexOf(dummy1);
            int pos2        = locs.indexOf(dummy2);
            IndividualNetworkAgent v1 = vertices.get(pos1);
            IndividualNetworkAgent v2 = vertices.get(pos2);
            Edge e = new Edge(v1, v2);
            network.addEdge(e, v1, v2);
        }
        
    }
    
    private void initialiseRandom() {
        int N           = (int)(double)network.params.get("N");
        double p        = (double)network.params.get("p");
        IndividualNetworkAgent[] vertices = new IndividualNetworkAgent[N];
        for (int i = 0; i < N; i++) {
            IndividualNetworkAgent v = new IndividualNetworkAgent( network.getNewNodeId() );
            vertices[i] = v;
            network.addVertex(  v );
        }
        
        for (int i = 0; i < (N-1); i++) {
            for (int j = (i+1); j < N; j++) {
                
                double x = ( IndividualNetworkModel.random ).getDouble();
                if (x < p) {
                    Edge e = new Edge(vertices[i], vertices[j]);
                    network.addEdge(e, vertices[i], vertices[j]);
                }
            }
        }
    }
    
    private void initialiseGrid() {
        int N           = (int)(double)network.params.get("N");
        int M           = (int)(double)network.params.get("M");
        IndividualNetworkAgent[][] vertices = new IndividualNetworkAgent[N][M];
        
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                IndividualNetworkAgent v = new IndividualNetworkAgent( network.getNewNodeId() );
                Location loc             = new Location(v.getName(), i, j, LocationType.XY);
                v.setLocation(loc);
                network.addVertex(  v );
                vertices[i][j] = v;
            }
        }
        
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                IndividualNetworkAgent vij0 = vertices[i][j];
                if (i < (N-1)) {
                    IndividualNetworkAgent vij1 = vertices[i+1][j];
                    Edge e = new Edge(vij0, vij1);
                    network.addEdge( e, vij0, vij1);
                }
                if (j < (N-1)) {
                    IndividualNetworkAgent vij1 = vertices[i][j+1];
                    Edge e = new Edge(vij0, vij1);
                    network.addEdge( e, vij0, vij1);
                }
            }
        }
        
        
    }
    
    
    private void initialiseLine() {
        
        int N       = (int)(double)network.params.get("N");
        
        IndividualNetworkAgent v0   = new IndividualNetworkAgent( network.getNewNodeId() );
        Location l0 = new Location(v0.getName(), 0, 0, LocationType.XY);
        v0.setLocation(l0);
        
        network.addVertex( v0 );
        for (int i  = 1; i < N; i++) {
            IndividualNetworkAgent v1 = new IndividualNetworkAgent( network.getNewNodeId() );
            Location li = new Location(v1.getName(), i, 0, LocationType.XY);
            v1.setLocation(li);
            
            network.addVertex( v1 );
            
            Edge e = new Edge(v0, v1);
            network.addEdge(e, v0, v1);
            
            v0 = v1;
        }
    }
    
    private void initialiseStar() {
        
        int N       = (int)(double)network.params.get("N");
        IndividualNetworkAgent v0   = new IndividualNetworkAgent( network.getNewNodeId() );
        Location l0 = new Location(v0.getName(), 0, 0, LocationType.XY);
        v0.setLocation(l0);
        
        network.addVertex( v0 );
        
        double r = 1.0;
        
        for (int i  = 1; i < N; i++) {
            IndividualNetworkAgent v1 = new IndividualNetworkAgent( network.getNewNodeId() );
            
            double phi = (2.0*Math.PI/360.0)*(double)(i-1)/(double)(N-1);
            double x   = r*Math.cos(phi);
            double y   = r*Math.sin(phi);
            Location li = new Location(v1.getName(), x, y, LocationType.XY);
            v1.setLocation(li);
            
            network.addVertex( v1 );
            
            Edge e = new Edge(v0, v1);
            network.addEdge(e, v0, v1);
        }
    }
    
    private void initialiseRing() {
        double r    = 1.0;
        
        int N       = (int)(double)network.params.get("N");
        IndividualNetworkAgent v0   = new IndividualNetworkAgent( network.getNewNodeId() );
        Location l0 = new Location(v0.getName(), r, 0, LocationType.XY);
        v0.setLocation(l0);
        
        network.addVertex( v0 );
        IndividualNetworkAgent first = v0;
        
        for (int i  = 1; i < N; i++) {
            IndividualNetworkAgent v1 = new IndividualNetworkAgent( network.getNewNodeId() );
            double phi = (2.0*Math.PI/360.0)*(double)i/(double)(N);
            double x   = r*Math.cos(phi);
            double y   = r*Math.sin(phi);
            Location li = new Location(v1.getName(), x, y, LocationType.XY);
            v1.setLocation(li);
            
            
            network.addVertex( v1 );
            
            Edge e = new Edge(v0, v1);
            network.addEdge(e, v0, v1);
            
            v0 = v1;
            
            if (i == (N-1) ) {
                e = new Edge(v1, first);
                network.addEdge(e, v1, first);
            }
        }
    }
    
    public void initialise() {
        
        switch( network.type ) {
            case GENERAL: initialiseGeneral();
                break;
            case RANDOM:
                initialiseRandom();
                break;
            case LINE:  initialiseLine();
                break;
            case STAR:  initialiseStar();
                break;
            case RING:  initialiseRing();
                break;
            case GRID:  initialiseGrid();
                break;
        }
        
    }
    
    public void initialiseWithInfecteds(int initI) {
        
        // this is very inefficient, but it is only done once at the start of the whole thing
        Collection nodes    = network.getVertices();
        List chosen         = ( IndividualNetworkModel.random ).selectManyOf(nodes, initI);
        
        List<Agent> agents = new ArrayList();
        for (IndividualNetworkAgent a : network.getVertices()) {
            if (chosen.contains(a)) {
                a.setState((IndividualStateType.INFECTED).toString());    
            }
            agents.add(a);
        }
        
        super.initialiseWithAgents(agents);
        
        //List nodes = super.getAgentsInState(IndividualStateType.SUSCEPTIBLE);
        //for (Object a : (new RNG()).selectManyOf(nodes, initI) ) {
        //    super.updateAgent((IndividualNetworkAgent)a, IndividualStateType.SUSCEPTIBLE, IndividualStateType.INFECTED);
        //}
    }
    
    /*
     public void initialise(int N, NetworkType type) {
        
        network.setNetworkType(type);
        network.setParameter("N", N);
         
        switch( network.type ) {
            case GENERAL: initialiseGeneral();
                break;
            case RANDOM:
                network.setParameter( "p", (double)10/(double)N ) ;
                initialiseRandom();
                break;
            case LINE:  initialiseLine();
                break;
            case STAR:  initialiseStar();
                break;
            case RING:  initialiseRing();
                break;
            case GRID:  initialiseGrid();
                break;
        }
        
    }
     
    public void initialise(int N, double p, NetworkType type) {
        
        if (type.equals(NetworkType.RANDOM)) {
            network.setParameter("N", N);
            network.setParameter("p", p);
            initialiseRandom();
        } else {
            initialise(N, type);
        }
        
    }
    */
    
    /////////////////////////////////////////////////////////////////////////////////
    
    public List<Agent> getIncomingNeighboursWithState(IndividualNetworkAgent node, IndividualStateType stateType) {
        List<Agent> neighbsInState                 = new ArrayList<Agent>();
        
        Collection<IndividualNetworkAgent> neighbs = network.getPredecessors(node);
        for (IndividualNetworkAgent nn : neighbs) {
            if (nn.getState().equals(stateType.toString())) {
                neighbsInState.add(nn);
            }
        }
        
        return neighbsInState;
    }
    
    // should be the same as getIncomingNeighboursWithState
    public List<Agent> getOutgoingNeighboursWithState(IndividualNetworkAgent node, IndividualStateType stateType) {
        List<Agent> neighbsInState                 = new ArrayList<Agent>();
        
        Collection<IndividualNetworkAgent> neighbs = network.getSuccessors(node);
        for (IndividualNetworkAgent nn : neighbs) {
            if (nn.getState().equals(stateType.toString())) {
                neighbsInState.add(nn);
            }
        }
        
        return neighbsInState;
    }
    
    
    // eventDone method is OK before from and to agents are specified
    
    
    
    @Override
    /**
     * generates events depending on model state, specifies the to agents for INFECTION (SIR) or EXPOSURE (SEIR)
     * @return list of events with rates
     */
    public List<SIREventWithRate> generateEvents() {
        
        List<SIREventWithRate> events = new ArrayList<>();
        
        for ( SIREventType etype : getRates().getEventTypes() ) {
            
        
            if ((etype == SIREventType.INFECTION) || (etype == SIREventType.EXPOSURE) ) {
                
                // for each infected, ask who their outgoing neighbours are
                
                List<Agent> alreadyInfected     = getAgentsInState(IndividualStateType.INFECTED);
                List<Agent> potentialInfectees  = new ArrayList<Agent>();
                for (Agent a : alreadyInfected) {
                    
                    /*
                    // list my susceptible neighbours
                    List<Agent> nn = getOutgoingNeighboursWithState( (IndividualNetworkAgent)a,  IndividualStateType.SUSCEPTIBLE );
                    
                    for (Agent pp : nn) {
                        // add to list of agents that could become infected
                        if (!potentialInfectees.contains(pp)) {
                            potentialInfectees.add(pp);
                        }
                    }
                    */
                    
                    // this might be abit faster
                    // list my neighbours
                    Collection<IndividualNetworkAgent> nn = network.getSuccessors((IndividualNetworkAgent)a);
                    
                    for (IndividualNetworkAgent pp : nn) {
                        if ( pp.getState().equals( (IndividualStateType.SUSCEPTIBLE).toString() )  
                                && (!potentialInfectees.contains(pp)) ) {
                            potentialInfectees.add(pp);
                        }
                    }
                    
                    
                }
                
                // for each of these potentially infectees, calculate infection hazard
                
                // set up for generating events
                IndividualStateType fromState   = IndividualStateType.INFECTED;
                IndividualStateType toState     = IndividualStateType.INFECTED;
                if (etype == SIREventType.EXPOSURE) {
                    toState = IndividualStateType.EXPOSED;
                }
                
                for (Agent toAgent : potentialInfectees) {
                    
                    // how many infected contacts do I have ?
                    List<Agent> nn          = getIncomingNeighboursWithState( (IndividualNetworkAgent)toAgent, IndividualStateType.INFECTED);
                    int numInfectedContacts = nn.size();
                    //int numContacts         = network.getPredecessorCount((IndividualNetworkAgent)toAgent );
                    
                    // what is the force of infection pointing to me ?
                    
                    // this is the formula for complete mixing (non-network)
                    //rr        = rates.getRate(etype) * (double)numS * (double)numI / (double)getN();
                    
                    double rr   = getRates().getRate(etype) * (double)numInfectedContacts;
                    
                    // do not specify the from agent yet, this is done in performEvent
                    IndividualSimulationState infector = new IndividualSimulationState(null, fromState);
                    IndividualSimulationState infectee = new IndividualSimulationState(toAgent, toState);
                    
                    SIREventWithRate ev  = new SIREventWithRate(etype, 
                                                infector, 
                                                infectee,
                                                rr);  
           
                    events.add(ev);
                }
                
                
                
            } else {
            
                // EXPOSURE, BECOME_INFECTIOUS, INFECTION, RECOVERY, IMMUNITY, SUSCEPTIBILITY;
                IndividualStateType fromState = null;
                IndividualStateType toState   = null;
                double              rr        = 0.0;
                
                // these rates can be calculated in bulk because the from's can be randomly chosen from the population
                switch (etype) {
                case EXPOSURE: {
                    
                        System.out.println("NetworkOfIndividualsModel.generateEvents - this statement should be unreachable");
                        /*
                        int numS  = getNumberOfAgentsInState(IndividualStateType.SUSCEPTIBLE);
                        int numI  = getNumberOfAgentsInState(IndividualStateType.INFECTED);
                        rr        = rates.getRate(etype) * (double)numS * (double)numI / (double)getN();
                        fromState = IndividualStateType.INFECTED;
                        toState   = IndividualStateType.EXPOSED;
                        */
                    }
                    break;
                    
                case BECOME_INFECTIOUS: {
                        // from and to not specified, they will be chosen if the event is performed
                        int numE  = getNumberOfAgentsInState(IndividualStateType.EXPOSED);
                        rr        = getRates().getRate(etype) * (double)numE;
                        fromState = IndividualStateType.EXPOSED;
                        toState   = IndividualStateType.INFECTED;
                    }
                    break;
                    
                case INFECTION: {
                    
                        System.out.println("NetworkOfIndividualsModel.generateEvents - this statement should be unreachable");
                        /*
                        int numS  = getNumberOfAgentsInState(IndividualStateType.SUSCEPTIBLE);
                        int numI  = getNumberOfAgentsInState(IndividualStateType.INFECTED);
                        rr        = rates.getRate(etype) * (double)numS * (double)numI / (double)getN();
                        fromState = IndividualStateType.INFECTED;
                        toState   = IndividualStateType.INFECTED;
                        */
                    }
                    break;
                    
                case RECOVERY: {
                        // from and to not specified, they will be chosen if event is performed
                        int numI  = getNumberOfAgentsInState(IndividualStateType.INFECTED);
                        rr        = getRates().getRate(etype) * (double)numI;
                        fromState = IndividualStateType.INFECTED;
                        toState   = IndividualStateType.RECOVERED;
                    }
                    break;
                    
                case IMMUNITY: {
                        // from and to not specified they will be chosen if event is performed
                        int numR  = getNumberOfAgentsInState(IndividualStateType.RECOVERED);
                        rr        = getRates().getRate(etype) * (double)numR;
                        fromState = IndividualStateType.RECOVERED;
                        toState   = IndividualStateType.IMMUNE;
                    }
                    break;
               
                case SUSCEPTIBILITY: {
                        // from and to not specified they will be chosen if event is performed
                        fromState = getRates().getLastState();
                        int num   = getNumberOfAgentsInState(fromState);
                        rr        = getRates().getRate(etype) * (double)num;
                        toState   = IndividualStateType.SUSCEPTIBLE;
                    }
                    break;
                }    
                
                if (rr > 0) {
                    SIREventWithRate ev  = new SIREventWithRate(etype, 
                                        new IndividualSimulationState(fromState), 
                                        new IndividualSimulationState(toState),
                                        rr);  
           
                    events.add(ev);
                }
            
            }
          
        }
        
        return events;
        
    }
    
    
    @Override
    /**
     * performs the SimulationEvent
     * @return EventDone - the event that was performed with flag set to true if the event was actually performed
     */
    public EventDone performEvent(SimulationEvent reaction) {
        
         // specify the fromAgent in the case of infection or exposure
         // then use the performEvent method in the super class
        
         if ( reaction instanceof SIREventWithRate) {
            SIREventWithRate event = ((SIREventWithRate) reaction);
            SIREventType    etype  = event.getEventType();
            
            if ( (etype == SIREventType.INFECTION) || ( etype == SIREventType.EXPOSURE ) ) {
                Agent fromAgent             = event.getFromAgent();
                Agent toAgent               = event.getToAgent();
                
                if ((toAgent  !=  null) && (fromAgent == null)) {
                    // choose an infected neighbour to be the infector
                    List<Agent> nn = getIncomingNeighboursWithState((IndividualNetworkAgent)toAgent, IndividualStateType.INFECTED);
                    
                    fromAgent = (Agent)( IndividualNetworkModel.random ).selectOneOf(nn);
                    
                    IndividualSimulationState infector = new IndividualSimulationState(fromAgent, IndividualStateType.INFECTED );
                    reaction.setInitialState(infector);
                } else {
                    System.out.println("NetworkOfIndividualsModel.performEvent - from and to agents not set correctly, why are you at this statement ?");
                }
            }
             
         }
         
         return (super.performEvent(reaction));
    }
    
    ///////////////////////////////////////////////////////////////////////////////////
    
    /**
     * returns the names and locations of the individuals
     * @return 
     */
    public String toNameLocationsList() {
        StringBuffer txt = new StringBuffer();
        String      eol  = System.getProperty("line.separator"); //"\n";
        
        boolean incHeader = true;
        
        for ( IndividualNetworkAgent a : network.getVertices() ) {
            
            if (incHeader) {
                String header = a.toNameLocationHeader() + eol;
                txt.append(header);
                incHeader = false;
            }
            
            String line = a.toNameLocation() + eol;
            txt.append(line);
        }
        
        return txt.toString();
    }
    
    /**
     * returns the names, locations, and state of the individuals
     * @return 
     */
    public String toIndividualStateList() {
        StringBuffer txt = new StringBuffer();
        String      eol  = System.getProperty("line.separator"); //"\n";
        
        boolean incHeader = true;
        
        for ( IndividualNetworkAgent a : network.getVertices() ) {
            
            if (incHeader) {
                String header = a.toNameLocationStateHeader() + eol;
                txt.append(header);
                incHeader = false;
            }
            
            String line = a.toNameLocationState() + eol;
            txt.append(line);
        }
        
        return txt.toString();
    }
    
    /**
     * returns the edge list of individuals with name and location
     * @return 
     */
    public String toNameLocationsEdgeList() {
        StringBuffer txt    = new StringBuffer();
        String      eol     = System.getProperty("line.separator"); //"\n";
        String      edelim  = ",";
        
        boolean incHeader = true;
        
        for (Edge<IndividualNetworkAgent> e : network.getEdges()) {
            IndividualNetworkAgent fromA = e.getSource();
            IndividualNetworkAgent toA   = e.getDestination();
            
            if (incHeader) {
                String header = fromA.toNameLocationHeader() + edelim + toA.toNameLocationHeader() + eol;
                txt.append( header );
                incHeader = false;
            }
            
            String line = fromA.toNameLocation() + edelim + toA.toNameLocation() + eol;
            txt.append(line);
        }
        
        return txt.toString();
    }
}
