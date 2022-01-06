
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
public class MMU extends Thread {
    Os os;
    int numberOfProcesses;
    int minimumFramesPerProcess;
    int cycles;
    int timeQuanta = 10;
    Process processes[] = new Process[numberOfProcesses];
    final List<Process> que = new ArrayList<>();
    int pageReplacementType;
    
    public MMU(Os os){
        this.os = os;
    }
    
    void switchContext(Process currentProcess){
      synchronized(que){
          que.add(currentProcess);
          Process nextProcess = que.remove(0);
          os.mainMemory.loadProcess(nextProcess); 
          que.notifyAll();
      }
    }
    
    void swapProcess(Process currentProcess){
        synchronized(que){

        }
    }
  
  
    Process fifo(){
       
        return null;
    }
    
    
    Process LRU(){
        return null;
    }
  
  
  
    
    void sheduleProcesses(){
        for(Process process: processes){
            process = new Process(this);
            process.start();
        }
    }
    
    
}
