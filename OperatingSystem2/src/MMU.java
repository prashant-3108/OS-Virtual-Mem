import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


public class MMU extends Thread {
    final List<Process> que = new ArrayList<>();
    final List<Process> blockedQue = new ArrayList<>();
    final List<Process> finishedProcess = new ArrayList<>();
    Os os;
    int numberOfProcesses;
    int minimumFramesPerProcess;
    int cycles;
    int timeQuanta = 10;
    int pageFaults = 0;
    Process[] processes;
    int pageReplacementType;

    public MMU(Os os) {
        this.os = os;

    }

    void switchContext(Process currentProcess) {
        synchronized (this) {
            currentProcess.onQue = true;
            if(!currentProcess.blocked){
                currentProcess.releaseMemory();
            }
            Process nextProcess = null;
            if (!que.isEmpty()) {
                nextProcess = que.remove(0);
                os.mainMemory.loadProcess(nextProcess);
                cycles += 5;
            }
            //confirm this is not the last process
            que.add(currentProcess);


            notifyAll();

        }
    }

    void blockProcess(Process process) {
        synchronized (this) {
            que.remove(process);
            blockedQue.add(process);
            process.faults++;
            process.blocked = true;
            process.onQue = true;
        }
    }

    void swapProcess() {
        synchronized (this) {
            if (!blockedQue.isEmpty()) {
                Process process = blockedQue.remove(0);
                Process oldProcess;
                if (pageReplacementType == 1) {
                    oldProcess = fifo();
                } else {
                    oldProcess = LRU();
                }
                if (oldProcess != null) {
                    oldProcess.onQue = true;
                    oldProcess.releaseMemory();
                    que.add(oldProcess);
                    os.mainMemory.loadProcess(process);
                    cycles += 5;
                }
            }
        }
    }


    int getJobQue() {
        return processes.length - finishedProcess.size();
    }

    Process fifo() {
        Process process = null;
        if (!que.isEmpty()) {
            process = que.remove(0);
        }
        return process;
    }


    Process LRU() {
        synchronized(this){
            Process leastUsedProcess = null;
            Map<Integer,Process> mapBySize = new HashMap<>();
            List<Integer> sizeList = new ArrayList<>();
            for(Process process : os.mainMemory.frameUsageByProcess.keySet()){
                int processUsage = os.mainMemory.frameUsageByProcess.get(process).size();
                mapBySize.put(processUsage, process);
                sizeList.add(processUsage);
            }
            Collections.sort(sizeList);  
            for(int usageSize : sizeList){
                leastUsedProcess = mapBySize.get(usageSize);
                if(leastUsedProcess.inMemory){
                    //System.out.println("}}}}}}}}}}Least used process "+leastUsedProcess);
                    return leastUsedProcess;
                }
            }
            
            return leastUsedProcess; 
        }
    }


    void sheduleProcesses(){
        for (Process process : processes) {
            //process = new Process(this);
            process.start();
        }
    }

    public void run() {
        processes = new Process[os.processes.length];
        for (int i = 0; i < processes.length; i++) {
            processes[i] = os.processes[i];
        }
        sheduleProcesses();
        synchronized (this) {
            while (finishedProcess.size() < processes.length) {
                try {
                    wait(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MMU.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        os.freeAllResource();
        System.out.println("All process execution completed");

    }
}
