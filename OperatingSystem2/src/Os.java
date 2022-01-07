
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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
public class Os {
    
    MMU mmu;
    MainMemory mainMemory;
    SecondaryStorage secondaryStorage;
    int numberOfProcesses;
    int memorySize;
    int minimumFrameSize;
    Process processes[] ;
    FileWriter fileWriter;
    
    
    public Os(){
        
        secondaryStorage = new SecondaryStorage();
        try {
            fileWriter = new FileWriter("output.txt");
            fileWriter.write("cycles;jobque;readyque;processOnThread;availableFrames\n"); 
        } catch (IOException ex) {
            Logger.getLogger(Os.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void writeToOutput(Process process){
        String output = mmu.cycles+";"+mmu.getJobQue()+";"+mainMemory.getReadyQue()+";"+process.id+";"+mainMemory.availableFrames();
        synchronized(this){
            try { 
                fileWriter.write(output+"\n");
            } catch (IOException ex) {
                Logger.getLogger(Os.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    void freeAllResource(){
        try {
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(Os.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void readConfig(){
        FileReader reader = null;
        BufferedReader buf = null;
        try {
            reader = new FileReader("config.txt");
            char[] buffer = new char[1024];
            buf = new BufferedReader(reader);
            String line;
            int counter = 0;
            int processCounter = 0;
            while((line = buf.readLine()) != null){
                System.out.println(line);
                if(counter == 0){
                    numberOfProcesses = Integer.parseInt(line.trim());
                    processes = new Process[numberOfProcesses];
                    mmu = new MMU(this); 
                }else if(counter == 1){
                    memorySize = Integer.parseInt(line.trim());
                    mainMemory = new MainMemory(this,memorySize);
                }else if(counter == 2){
                    minimumFrameSize = Integer.parseInt(line.trim());
                }else if(counter > 2){
                    String pDetails[] = line.split("\\s+");
                    int pageSize = Integer.parseInt(pDetails[3].trim());
                    Process process = new Process(mmu,pageSize);
                    process.id = Short.parseShort(pDetails[0].trim());
                    process.start = Integer.parseInt(pDetails[1].trim());
                    process.duration = Short.parseShort(pDetails[2].trim());  
                    processes[processCounter] = process;
                    for(int i = 4; i < pDetails.length; i++){
                        int trace = Integer.decode("0x"+pDetails[i].trim());
                        process.traces.add(trace);
                        
                    }
                    processCounter++;
                    
                }
                
                
                counter++;
            }
            
             Scanner scanner = new Scanner(System.in);
             System.out.println("Please input page replacement type");
             System.out.println("press 1 for fifo and 2 for lru");
             mmu.pageReplacementType = scanner.nextInt();
             mmu.minimumFramesPerProcess = this.minimumFrameSize;
            
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Os.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Os.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(Os.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    
    }
    
    public static void main(String args[]){
        Os os = new Os();
        os.readConfig();
        System.out.println(os.processes.length);
        for(Process process: os.processes){
            System.out.println(process+"-"+process.mmu);
        }
        os.mmu.start();
    }
    
}
