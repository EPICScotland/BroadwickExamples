/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package epic.io;

import broadwick.io.*;
import epic.network.LocationType;
import epic.network.Location;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Samantha Lycett
 */
public class ReadLocationsFile {
 
    String          filename;
    String          delim   = Location.delim;
    LocationType    locType = LocationType.LATLONG;
    
    boolean         hasHeader = true;
    List<String>    header;
    
    FileInput       inFile;
    boolean         open    = false;
    
    public ReadLocationsFile() {
        
    }
    
    public ReadLocationsFile(String filename) {
        this.filename = filename;
        openFile();
    }
    
    
    public ReadLocationsFile(String filename, String delim, String locType) {
        this.filename   = filename;
        this.delim      = delim;
        this.locType    = LocationType.valueOf(locType);
        openFile();
    }
    
    
    public ReadLocationsFile(String filename, String delim, LocationType locType) {
        this.filename   = filename;
        this.delim      = delim;
        this.locType    = locType;
        openFile();
    }
    
    ////////////////////////////////////////////////////////////////////////
    
    /*
    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public void setDelim(String delim) {
        this.delim = delim;
    }
    
    public void setLocationType(LocationType locType) {
        this.locType = locType;
    }
    */
    
    ///////////////////////////////////////////////////////////////////////
    
    private void openFile() {
        try {
            //System.out.println(filename);
            inFile  = new FileInput(filename, delim);
            
            if (hasHeader) {
                header  = inFile.readLine();
            }
            
            open    = true;
            //return true;
        } catch (IOException ex) {
            
            Logger.getLogger(ReadLocationsFile.class.getName()).log(Level.SEVERE, null, ex);
            open    = false;
            //return false;
        }
    }
    
    private void closeFile() {
        open        = false;
        inFile.close();
    }
    
    ///////////////////////////////////////////////////////////////////////
    
    public List<String> getHeader() {
        return header;
    }
    
    /**
     * reads the next line in the file and returns a Location object or null.
     * Closes the file on IOException (e.g. end of file).
     * @return 
     */
    public Location nextLocation() {
        try {
            List<String> els = inFile.readLine();
            
            if (els.size() >= 3) { 
                String name = els.get(0);
                double x    = Double.parseDouble(els.get(1));
                double y    = Double.parseDouble(els.get(2));
            
                Location loc = new Location(name, x, y, locType);
                return loc;
            } else {
                closeFile();
                return null;
            }
            
        } catch (IOException ex) {
            //Logger.getLogger(ReadLocationsFile.class.getName()).log(Level.SEVERE, null, ex);
            closeFile();
            return null;
        } catch (NumberFormatException ex) {
            closeFile();
            Logger.getLogger(ReadLocationsFile.class.getName()).log(Level.WARNING, null, ex);
            return null;
        }
    }
    
    ////////////////////////////////////////////////////////////////////////
    
    public List<Location> readFile(String filename, String delim, LocationType locType) {
        
        this.filename   = filename;
        this.delim      = delim;
        this.locType    = locType;
        
        openFile();
        
        @SuppressWarnings("Convert2Diamond")
        List<Location> locs = new ArrayList<Location>();
        Location loc        = nextLocation();
        
        while (open) {
            if (loc != null) {
                locs.add(loc);
            }
            loc = nextLocation();
        }
        
        return locs;
    }
    
}
