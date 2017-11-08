package test.elsufoundation;

import elsu.common.*;

public class ElsuFoundationTest {

    public static void main(String[] args) throws Exception {
    	String pAbsolutePath = "/home/development/opt/OWF-bundle-7.17.1/apache-tomcat/webapps/symbology/tracks/svg_track_cluster_l1.svg";
    	
    	System.out.println(FileUtils.extractFilePath(pAbsolutePath));
    	System.out.println(FileUtils.extractFileName(pAbsolutePath));
    }
}
