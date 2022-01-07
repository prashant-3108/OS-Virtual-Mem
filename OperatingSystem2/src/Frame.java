/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author Judah-Steve
 */
public class Frame {
    short frameNo;

    public short getFrameNo() {
        return frameNo;
    }

    public void setFrameNo(short frameNo) {
        this.frameNo = frameNo;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    Page page;

    public Frame(short frameNo, Page page) {
        this.frameNo = frameNo;
        this.page = page;
    }
}
