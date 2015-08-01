/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elsuconfigtest;

import elsu.support.*;

/**
 *
 * @author ss.dhaliwal
 */
public class ElsuConfigTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            ConfigLoader cl = new ConfigLoader();
            
            for(String key : cl.getProperties().keySet()) {
                System.out.println(key + "=" + cl.getProperties().get(key).toString());
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

}
