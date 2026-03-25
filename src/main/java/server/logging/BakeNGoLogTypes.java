package server.logging;

import niwer.lumen.EnumLogColor;
import niwer.lumen.types.BasicLogType;
import niwer.lumen.types.ILogType;

/**
 * @author Niwer
 */
public class BakeNGoLogTypes {

    public static final ILogType WEB_SERVER = new BasicLogType("WEB SERVER", EnumLogColor.CYAN);
}
