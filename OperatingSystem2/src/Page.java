/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Prashant-Jangid
 */
public class Page {
    short processId;
    short pageNo;

    public short getProcessId() {
        return processId;
    }

    public void setProcessId(short processId) {
        this.processId = processId;
    }

    public short getPageNo() {
        return pageNo;
    }

    public void setPageNo(short pageNo) {
        this.pageNo = pageNo;
    }

    public Page(short processId, short pageNo){
        this.processId = processId;
        this.pageNo = pageNo;
    }
}
