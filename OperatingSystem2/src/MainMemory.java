import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


public class MainMemory {

    Os os;
    int totalFrameSize;
    Frame[] frames;
    List<PageTable> pageTables = new ArrayList<>();
    Map<Process,List<Frame>> frameUsageByProcess = new HashMap<>();


    public MainMemory(Os os, int totalFrameSize) {
        frames = new Frame[totalFrameSize];
        this.totalFrameSize = totalFrameSize;
        this.os = os;
        for (short i = 0; i < totalFrameSize; i++) {
            frames[i] = new Frame(i, null);
        }
    }

    /**
    boolean pageExistInMemory(Page page) {
        synchronized (os.mmu) {
            for (PageTable pageTable : pageTables) {
                if (pageTable.map.containsKey(page.pageNo)) {
                    return true;
                }
            }
            return false;
        }
    }
    */
    
      boolean pageExistInMemory(Page page) {
        synchronized (os.mmu) {
            for (Frame frame: frames) {
                if (frame.page == page) {
                    return true;
                }
            }
            return false;
        }
    }


    void loadProcess(Process process) {
        synchronized (os.mmu) {
            process.blocked = false;
            if (!pageTables.contains(process.pageTable)) {
                pageTables.add(process.pageTable);
            }
            int allocatedFrames = 0;
            for (int i = 0; i < frames.length; i++) {
                Frame frame = frames[i];
                if (frame.page == null) {
                    if (process.pageLocation < process.pageSize) {
                        //System.out.println(process.pageSize+"---"+process.pages.size());
                        Page page = process.pages.get(process.pageLocation);
                        process.allocatedFrames++;
                        process.pageTable.map.put(process.pageLocation, frame.frameNo);
                        process.pageLocation++;
                        process.pageTable.objectMap.put(page, frame);
                        frame.page = page;
                        //System.out.println("==="+process+"__"+process.pageLocation+"=== ("+os.mmu.minimumFramesPerProcess+")");
                        os.secondaryStorage.pages.remove(page);
                        os.mmu.cycles += 300;
                        process.inMemory = true;
                        allocatedFrames++;
                        if(frameUsageByProcess.get(process) == null){
                            frameUsageByProcess.put(process, new ArrayList<>());
                        }else{
                            frameUsageByProcess.get(process).add(frame);
                        }
                        os.writeToOutput(process);
                    }
                }
                if (allocatedFrames == os.mmu.minimumFramesPerProcess) {
                    //Resetting the frame 
                    process.onQue = false;
                    process.allocatedFrames = 0;
                    System.out.println("Frames allocated for " + process);
                    break;
                }

            }


            //System.out.println(process + " --" + process.pageLocation + "  " + process.pageSize);
            if (process.pageLocation < process.pageSize) {
                for (int i = process.pageLocation; i < process.pageSize; i++) {
                    //System.out.println(i);1

                    Page page = process.pages.get(i);
                    if (!os.secondaryStorage.pages.contains(page)) {
                        os.secondaryStorage.pages.add(page);
                    }

                }
            }


            //System.out.println(process+"pagelocation-"+process.pageLocation+"-page size"+process.pageSize);
            if (process.pageLocation == process.pageSize) {
                process.onQue = false;
                process.completed = true;
            }

            if (!os.mmu.blockedQue.isEmpty()) {
                os.mmu.swapProcess();
            }

        }
    }


    int getReadyQue() {
        synchronized(os.mmu){
            Map<Short, Frame> map = new HashMap<>();
            for (int i = 0; i < frames.length; i++) {
                if (frames[i].page != null) {
                    map.put(frames[i].page.processId, frames[i]);
                }
            }
            return map.keySet().size();
        }
    }

    int availableFrames() {
        synchronized(os.mmu){
            int availableFrames = 0;
            for (int i = 0; i < frames.length; i++) {
                if (frames[i].page == null) {
                    availableFrames++;
                }
            }
            return availableFrames;
        }
    }
}
