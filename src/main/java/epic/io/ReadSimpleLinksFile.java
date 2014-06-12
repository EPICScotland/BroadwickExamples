/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package epic.io;

import broadwick.io.FileInput;
import epic.network.Location;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


// TO DO NOT SURE ABOUT THIS CLASS

/**
 *
 * @author Samantha Lycett
 */
public class ReadSimpleLinksFile {
    
    String          filename;
    String          delim   = Location.delim;
    boolean         open    = false;
    
    boolean         hasHeader = true;
    List<String>    header;
    
    FileInput       inFile;
    
    public ReadSimpleLinksFile() {
        
    }
   
    public ReadSimpleLinksFile(String filename) {
        this.filename = filename;
    }
    
    public ReadSimpleLinksFile(String filename, String delim) {
        this.filename = filename;
        this.delim    = delim;
    }
    
    /////////////////////////////////////////////////////////////////////
    
    private void openFile() {
        try {
            inFile  = new FileInput(filename, delim);
            open    = true;
            
            if (hasHeader) {
                header = inFile.readLine();
            }
            
        } catch (IOException ex) {
            open = false;
            Logger.getLogger(ReadSimpleLinksFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void closeFile() {
        if (open) {
            open = false;
            inFile.close();
        }
    }
    
    ////////////////////////////////////////////////////////////////////////
    
    public List<String> getHeader() {
        return header;
    }
    
    public List<String> getNextLink() {
        
        if (inFile == null) {
            openFile();
        }
        
        if (open) {
        
            try {
                return inFile.readLine();
            } catch (IOException ex) {
                closeFile();
                Logger.getLogger(ReadSimpleLinksFile.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        
        } else {
            return null;
        }
        
    }
    
    public List<List<String>> readFile(String filename, String delim) {
        this.filename = filename;
        this.delim    = delim;
        openFile();
        
        List<List<String>> links = new ArrayList<>();
        List<String> aLink       = getNextLink();
        while (aLink.size() >= 2) {
            links.add( aLink );
            aLink = getNextLink();
        }
        closeFile();
        return links;
    }
   
    
}
