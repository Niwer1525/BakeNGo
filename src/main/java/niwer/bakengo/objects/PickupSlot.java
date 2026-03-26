package niwer.bakengo.objects;

import niwer.queryon.SQLSerializable;
import niwer.queryon.tables.api.IColumnField;

public class PickupSlot extends SQLSerializable<PickupSlot> {

    @IColumnField(name = "id", primaryKey = true, autoIncrement = true)
    private int id;

    @IColumnField(name = "day", notNull = true)
    private String day;

    @IColumnField(name = "start_time", notNull = true)
    private String startTime;

    @IColumnField(name = "end_time", notNull = true)
    private String endTime;

    @IColumnField(name = "capacity", notNull = true)
    private int capacity;

    @IColumnField(name = "is_enabled", notNull = true)
    private boolean isEnabled = true;

    public int id() { return id; }

    public String day() { return day; }

    public String startTime() { return startTime; }

    public String endTime() { return endTime; }

    public int capacity() { return capacity; }

    public boolean isEnabled() { return isEnabled; }
}
