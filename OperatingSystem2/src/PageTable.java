
import java.util.HashMap;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Judah-Steve
 */
public class PageTable {
    Process process;
    Map<Short,Short> map;
    Map<Page,Frame> objectMap;
    
    public PageTable(Process process){
        this.process = process;
        map = new HashMap<>();
        objectMap = new HashMap<>();
    }
    
}
