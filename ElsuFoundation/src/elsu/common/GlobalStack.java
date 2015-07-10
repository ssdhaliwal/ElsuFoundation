package elsu.common;

/**
 *
 * @author ssd.administrator
 */
public class GlobalStack {

    // http://docs.oracle.com/javase/tutorial/essential/environment/sysprop.html
    public static String FILESEPARATOR = System.getProperty("file.separator");
    public static String PATHSEPARATOR = System.getProperty("path.separator");
    public static String LINESEPARATOR = System.getProperty("line.separator");

    public static String JAVACLASSPATH = System.getProperty("java.class.path");
    public static String JAVAENDORSEDDIRS = System.getProperty(
            "java.endorsed.dirs");
    public static String JAVAHOME = System.getProperty("java.home");
    public static String JAVAIOTMPDDIR = System.getProperty("java.io.tmpdir");
    public static String SUNBOOTLIBRARYPATH = System.getProperty(
            "sub.boot.library.path");
    public static String USERDIR = System.getProperty("user.dir");
    public static String USERHOME = System.getProperty("user.home");

    public static String JAVACLASSVERSION = System.getProperty(
            "java.class.version");
    public static String JAVARUNTIMEVERSION = System.getProperty(
            "java.runtime.version");
    public static String JAVASPECIFICATIONVERSION = System.getProperty(
            "java.specification.version");
    public static String JAVAVERSION = System.getProperty("java.version");
    public static String JAVAVMVERSION = System.getProperty("java.vm.version");
    public static String JAVAVMSPECIFICATIONVERSION = System.getProperty(
            "java.vm.specification.version");
    public static String OSARCHITECTURE = System.getProperty("os.arch");
    public static String OSNAME = System.getProperty("os.arch");
    public static String OSVERSION = System.getProperty("os.version");
    public static String SUNARCHITECTUREDATAMODEL = System.getProperty(
            "sun.arch.data.model");

    public static String JAVAVENDOR = System.getProperty("java.vendor");
    public static String JAVAVENDORURL = System.getProperty("java.vendor.url");
    public static String JAVAVVMVENDOR = System.getProperty("java.vm.vendor");
    public static String JAVAVVMNAME = System.getProperty("java.vm.name");

    public static String JAVACOMMAND = System.getProperty("sun.java.command");
    public static String USERNAME = System.getProperty("user.name");

}
