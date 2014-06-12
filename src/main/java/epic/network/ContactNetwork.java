/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package epic.network;

import broadwick.graph.*;
import broadwick.utils.Pair;
import edu.uci.ics.jung.graph.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;

/**
 * ContactNetwork is an undirected graph.  
 * This class is the undirected version of broadwick.graph.DirectedGraph
 * but it also contains methods to initialise with certain common network shapes
 * @author Samantha Lycett
 * @version 4 June 2014
 */
public class ContactNetwork<V extends Vertex, E extends Edge<V>> implements broadwick.graph.Graph<V, E> {

    private UndirectedSparseMultigraph<V, E> graph;
    
    NetworkType             type        = NetworkType.GENERAL;
    String                  locationsFileName;
    String                  linksFileName;
    LocationType            locType     = LocationType.LATLONG;
    Hashtable<String,Double> params     = new Hashtable<String,Double>();
    private int             nodeid      = 0;
    
    public ContactNetwork() {
        graph = new edu.uci.ics.jung.graph.UndirectedSparseMultigraph<>();
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Getters and Setters
    
    public void setNetworkType(NetworkType nt) {
        this.type = nt;
        if (type == NetworkType.GENERAL) {
            this.locType = LocationType.LATLONG;
        } else {
            this.locType = LocationType.XY;
        }
    }
    
    public NetworkType getNetworkType() {
        return type;
    }
    
    public String[] getAllowedParameters() {
        
        String[] allowed;
        
        switch (type) {
            case GENERAL: allowed = new String[]{"locationsFile","linksFile"};
                break;
            case RANDOM:  allowed = new String[]{"N","p"};
                break;
            case LINE:    allowed = new String[]{"N"};
                break;
            case STAR:    allowed= new String[]{"N"};
                break;
            case RING:    allowed= new String[]{"N"};
                break;
            case GRID:    allowed = new String[]{"N","M"};
                break;
            default:      allowed = new String[]{"N"};
                
        }
        
        return allowed;
    }
    
    // Parameter setting and getting
    public boolean isParameterAllowed(String paramName) {
        String[] allowed = getAllowedParameters();
        return (Arrays.asList(allowed).contains(paramName) );
    }
    
    public void setParameter(String paramName, double paramValue) {
        if (isParameterAllowed(paramName)) {
            params.put(paramName, paramValue);
        }
    }
    
    public Double getParameter(String paramName) {
        return ( params.get(paramName) );
    }
    
    public void setLocationsFileName(String filename) {
        if (isParameterAllowed("locationsFile")) {
            this.locationsFileName = filename;
        }
    }
    
    public String getLocationsFileName() {
        return locationsFileName;
    }
    
    public void setLinksFileName(String filename) {
        if (isParameterAllowed("linksFile")) {
            this.linksFileName = filename;
        }
    }
    
    public String getLinksFileName() {
        return linksFileName;
    }
    
    public void setLocationType(LocationType locType) {
        this.locType = locType;
    }
    
    public LocationType getLocationType() {
        return this.locType;
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    String getNewNodeId() {
        String name = "CN" + String.format("%06d", nodeid);
        nodeid++;
        return (name);
    }
    
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
     @Override
    public final Collection<E> getInEdges(final V vertex) {
        return graph.getInEdges(vertex);
    }

    @Override
    public final Collection<E> getOutEdges(final V vertex) {
        return graph.getOutEdges(vertex);
    }

    @Override
    public final Collection<V> getPredecessors(final V vertex) {
        return graph.getPredecessors(vertex);
    }

    @Override
    public final Collection<V> getSuccessors(final V vertex) {
        return graph.getSuccessors(vertex);
    }

    @Override
    public final int inDegree(final V vertex) {
        return graph.inDegree(vertex);
    }

    @Override
    public final int outDegree(final V vertex) {
        return graph.outDegree(vertex);
    }

    @Override
    public final boolean isPredecessor(final V v1, final V v2) {
        return graph.isPredecessor(v1, v2);
    }

    @Override
    public final boolean isSuccessor(final V v1, final V v2) {
        return graph.isSuccessor(v1, v2);
    }

    @Override
    public final int getPredecessorCount(final V vertex) {
        return graph.getPredecessorCount(vertex);
    }

    @Override
    public final int getSuccessorCount(final V vertex) {
        return graph.getSuccessorCount(vertex);
    }

    @Override
    public final V getSource(final E directedEdge) {
        return graph.getSource(directedEdge);
    }

    @Override
    public final V getDest(final E directedEdge) {
        return graph.getDest(directedEdge);
    }

    @Override
    public final boolean isSource(final V vertex, final E edge) {
        return graph.isSource(vertex, edge);
    }

    @Override
    public final boolean isDest(final V vertex, final E edge) {
        return graph.isDest(vertex, edge);
    }

    @Override
    public final boolean addVertex(final V vertex) {
        return graph.addVertex(vertex);
    }

    @Override
    /**
     * always adds and undirected edge type
     */
    public final boolean addEdge(final E e, final V v1, final V v2) {
        return graph.addEdge(e, v1, v2, edu.uci.ics.jung.graph.util.EdgeType.UNDIRECTED);
    }

    @Override
    public final boolean addEdge(final E e, final V v1, final V v2, final EdgeType edgeType) {
        if (edgeType.equals(EdgeType.UNDIRECTED)) {
            return graph.addEdge(e, v1, v2, edu.uci.ics.jung.graph.util.EdgeType.UNDIRECTED);
        } else {
            return graph.addEdge(e, v1, v2, edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
        }
        
    }

    @Override
    public final Pair<V, V> getEndpoints(final E edge) {
        final edu.uci.ics.jung.graph.util.Pair<V> endpoints = graph.getEndpoints(edge);
        return new Pair<>(endpoints.getFirst(), endpoints.getSecond());
    }

    @Override
    public final V getOpposite(final V vertex, final E edge) {
        return graph.getOpposite(vertex, edge);
    }

    @Override
    public final int getVertexCount() {
        return graph.getVertexCount();
    }

    @Override
    public final Collection<V> getVertices() {
        return graph.getVertices();
    }

    @Override
    public final Collection<E> getEdges() {
        return graph.getEdges();
    }

    @Override
    public final boolean removeVertex(final V vertex) {
        return graph.removeVertex(vertex);
    }

    @Override
    public final boolean removeEdge(final E edge) {
        return graph.removeEdge(edge);
    }

    @Override
    public EdgeType getEdgeType() {
        return EdgeType.UNDIRECTED;
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    
}
