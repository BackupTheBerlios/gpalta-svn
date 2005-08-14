/*
 * Logger.java
 *
 * Created on 7 de junio de 2005, 01:56 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package gpalta.core;
import java.io.*;

/**
 *
 * @author DSP
 */

//TODO: investigate about Java Logging
public abstract class Logger
{
    
    private static PrintWriter writer;
    private static boolean initialized = false;
    
    public static void init()
    {
        //TODO: this exception should be caught here?
        try
        {
            writer = new PrintWriter(new BufferedWriter (new FileWriter(Config.logFileName)));
        }
        catch (IOException e)
        {
            e.printStackTrace(writer);
        }
    }
    
    public static void log(String s)
    {
        if (!initialized)
        {
            init();
            initialized = true;
        }
        writer.println(s);
        System.out.println(s);
        //TODO: should we alway flush?
        writer.flush();
    }
    
    public static void log(Exception e)
    {
        if (!initialized)
        {
            init();
            initialized = true;
        }
        e.printStackTrace(writer);
        e.printStackTrace();
        //TODO: should we alway flush?
        writer.flush();
    }
    
}
