package gpalta.clustering;

import gpalta.core.Common;

import java.util.List;

/**
 * Created by IntelliJ IDEA. User: nvn Date: 12-09-2006 Time: 12:34:33 PM To change this template
 * use File | Settings | File Templates.
 */
public class TreeGroupOperator
{
    public void operate(List<TreeGroup> population)
    {
        for (TreeGroup g: population)
        {
            double op = Common.globalRandom.nextDouble();
            if (op < 0.4)
            {
                //do nothing
            }
            else if (op < 0.9)
            {
                g.oneMoreCluster();
            }
            else
            {
                //g.oneLessCluster();
            }
        }
    }
}
