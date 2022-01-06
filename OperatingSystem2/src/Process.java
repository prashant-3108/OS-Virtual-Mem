
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Judah-Steve
 */
public class Process extends Thread {

    short id;
    int start;
    int duration;
    int pageSize; 
    int timeUsed;
    int completedPageIndex;
    List<Page> pages;
    List<Integer> traces; 
    PageTable pageTable;
    MMU mmu;
    boolean completed = false;
    boolean onQue = false;
    
    public Process(MMU mmu){
        pages = new ArrayList<>();
        traces = new ArrayList<>();
        pageTable = new PageTable(this);
        
        for(short i = 1; i < pageSize; i++){
            pages.add(new Page(id,i));
        } 
        this.mmu = mmu;
        
        
    }
    
    @Override
    public void run() {
        while(!completed){
            for(int i = 0; i < mmu.timeQuanta; i++){
               
            }
            mmu.switchContext(this); 
            synchronized(mmu){ 
                while(onQue){
                    try {
                        mmu.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Process.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
    
}
