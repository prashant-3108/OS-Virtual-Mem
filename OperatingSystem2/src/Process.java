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
 * @author Judah-Steve
 */
public class Process extends Thread {

    short id;
    int start;
    int duration;
    int pageSize;
    int timeUsed;
    short pageLocation;
    int allocatedFrames;
    List<Page> pages;
    List<Integer> traces;
    List<Integer> completedTraces;
    PageTable pageTable;
    MMU mmu;
    boolean completed = false;
    boolean onQue = false;

    public Process(MMU mmu, int pageSize) {
        pages = new ArrayList<>();
        traces = new ArrayList<>();
        pageTable = new PageTable(this);
        completedTraces = new ArrayList<>();
        this.pageSize = pageSize;

        for (short i = 0; i < pageSize; i++) {
            pages.add(new Page(id, i));
        }
        this.mmu = mmu;


    }

    void releaseMemory() {
        for (Frame frame : pageTable.objectMap.values()) {
            frame.page = null;
        }

    }

    @Override
    public void run() {
        mmu.os.mainMemory.loadProcess(this);
        while (!completed) {
            for (int i = 0; i < traces.size(); i++) {
                int trace = traces.get(i);
                short pageNo = (short) (trace >> 4);

                mmu.cycles++;
                if (pageNo < pages.size()) {
                    Page page = pages.get(pageNo);
                    if (mmu.os.mainMemory.pageExistInMemory(page)) {
                        completedTraces.add(trace);
                    } else {
                        mmu.blockProcess(this);
                    }
                } else {
                    mmu.blockProcess(this);
                }

            }
            mmu.switchContext(this);
            synchronized (mmu) {
                int counter = 0;
                while (onQue) {
                    try {
                        System.out.println(this + "Waiting");
                        mmu.wait(duration);
                        if (mmu.que.size() == 1 && mmu.que.contains(this)) {
                            onQue = false;
                            mmu.os.mainMemory.loadProcess(this);
                        }
                        counter++;
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Process.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            System.out.println(this + "resumed");
            //System.out.println("page location : "+pageLocation+" : page size "+pageSize);
        }
        synchronized (this.mmu) {
            this.mmu.finishedProcess.add(this);
            this.mmu.que.remove(this);
            System.out.println(this + "finished");
            mmu.os.writeToOutput(this);
        }
    }

}
