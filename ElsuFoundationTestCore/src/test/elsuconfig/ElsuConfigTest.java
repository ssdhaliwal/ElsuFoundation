/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.elsuconfig;

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
            
            for(String key : cl.getKeySet()) {
                System.out.println(key + "=" + cl.getProperty(key).toString());
            }

            System.out.println("---------------------------------------------");
            cl = new ConfigLoader(new String[]{"application.framework.", "application.groupExtensions."});
            for(String key : cl.getKeySet()) {
                System.out.println(key + "=" + cl.getProperty(key).toString());
            }
            
            System.out.println("---------------------------------------------");
            Log4JManager log4JManager = null;            
            try {
                String logPropertyFile = "config/log4j_1.properties";
                String logClass = "logTest45";
                String fileName = "ElsuConfigTest.log";
                
                log4JManager = ConfigLoader.initializeLogger(logPropertyFile, logClass, fileName);
                log4JManager.info("this is a test/info");
                log4JManager.error("this is a test/error");
                log4JManager.debug("this is a test/debug");
            } catch (Exception ex) { }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

}
