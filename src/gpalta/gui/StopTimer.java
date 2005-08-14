/*
 * StopTimer.java
 *
 * Created on 6 de junio de 2005, 04:34 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package gpalta.gui;
import java.io.IOException;
import java.util.*;
import gpalta.core.*;

/**
 * A Timer Task to schedule the the Evolution to stop at a certain time
 * @author DSP
 */
public class StopTimer extends TimerTask
{
    GPaltaGUI gui;
    
    /** Creates a new instance of StopTimer */
    public StopTimer(GPaltaGUI gui)
    {
        this.gui = gui;
    }
    
    public void run()
    {
        gui.stopSaveQuit = true;
        Logger.log("Setting to save and quit at the end of this generation");
    }
    
}
