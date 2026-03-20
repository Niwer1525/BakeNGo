package server.logging;

import niwer.lumen.EnumLogColor;
import niwer.lumen.types.BasicLogType;
import niwer.lumen.types.ILogType;

/**
 * Log types used in Croissant Flow.
 * 
 * @author Niwer
 */
public class CroissantFlowLogTypes {

    public static final ILogType WEB_SERVER = new BasicLogType("WEB SERVER", EnumLogColor.CYAN);
}
