package server.logging;

import niwer.lumen.EnumLogColor;
import niwer.lumen.types.BasicLogType;
import niwer.lumen.types.ILogType;

public class CroissantFlowLogTypes {

    public static final ILogType WEB_SERVER = new BasicLogType("WEB SERVER", EnumLogColor.CYAN);
    public static final ILogType SQL = new BasicLogType("SQL", EnumLogColor.RED);
}
