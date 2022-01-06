
import java.util.ArrayList;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/** 
 *
 * @author Judah-Steve
 */
public class MainMemory {
     
    Os os;
    int totalFrameSize;
    Frame[] frames;
    List<PageTable> pageTables = new ArrayList<>();
    
    public MainMemory(Os os){
        this.os = os;
    }
    
    void loadProcess(Process process){
        pageTables.add(process.pageTable);
        
    }
    
    public MainMemory(int totalFrameSize){
        frames = new Frame[totalFrameSize];
        
    }
    
    
    
}
