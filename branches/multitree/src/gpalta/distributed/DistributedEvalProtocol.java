package gpalta.distributed;

/**
 * Created by IntelliJ IDEA. User: nvn Date: 18-04-2007 Time: 12:32:38 PM To change this template
 * use File | Settings | File Templates.
 */
public interface DistributedEvalProtocol
{
    String INIT_STRING = "BEGIN";
    String EVAL_STRING = "EVAL";
    String END_STRING = "END";
    String OK_STRING = "OK";
}
