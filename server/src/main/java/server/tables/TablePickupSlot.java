package server.tables;

import java.util.List;

import niwer.queryon.DataBase;
import niwer.queryon.queries.Expression;
import niwer.queryon.queries.interaction.DeletionManager;
import niwer.queryon.queries.interaction.InsertionManager;
import niwer.queryon.queries.interaction.SelectionManager;
import niwer.queryon.queries.interaction.UpdateManager;
import niwer.queryon.tables.Table;
import server.App;
import server.objects.PickupSlot;

public class TablePickupSlot extends Table {

    public TablePickupSlot(DataBase db) {
        super(db);

        this.addColumnsFromClass(PickupSlot.class).execute();
    }

    @Override public String name() { return "pickup_slots"; }

    public static void addPickupSlot(String label, String startTime, String endTime, int capacity, boolean isEnabled) {
        label = requireNonBlank(label, "Label");
        startTime = requireNonBlank(startTime, "Start time");
        endTime = requireNonBlank(endTime, "End time");
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be greater than 0");

        InsertionManager.insert(App.DATA_BASE, TablePickupSlot.class, "label", "start_time", "end_time", "capacity", "is_enabled")
            .row(label, startTime, endTime, capacity, isEnabled)
            .execute();
    }

    public static List<PickupSlot> getAllPickupSlots() {
        final SelectionManager query = SelectionManager.select(App.DATA_BASE, TablePickupSlot.class,
            "COALESCE(NULLIF(id, 0), rowid) AS id", "label", "start_time", "end_time", "capacity", "is_enabled")
            .orderBy("rowid", SelectionManager.EnumOrder.ASC);

        try {
            return query.executeList(PickupSlot.class);
        } catch (IllegalStateException ignored) {
            final PickupSlot single = query.executeSerializable(PickupSlot.class);
            return single == null ? List.of() : List.of(single);
        }
    }

    public static List<PickupSlot> getEnabledPickupSlots() {
        final SelectionManager query = SelectionManager.select(App.DATA_BASE, TablePickupSlot.class,
            "COALESCE(NULLIF(id, 0), rowid) AS id", "label", "start_time", "end_time", "capacity", "is_enabled")
            .where(Expression.of("is_enabled").isEqualTo(true))
            .orderBy("rowid", SelectionManager.EnumOrder.ASC);

        try {
            return query.executeList(PickupSlot.class);
        } catch (IllegalStateException ignored) {
            final PickupSlot single = query.executeSerializable(PickupSlot.class);
            return single == null ? List.of() : List.of(single);
        }
    }

    public static PickupSlot getPickupSlotByLabel(String label) {
        label = requireNonBlank(label, "Label");

        return SelectionManager.select(App.DATA_BASE, TablePickupSlot.class,
            "COALESCE(NULLIF(id, 0), rowid) AS id", "label", "start_time", "end_time", "capacity", "is_enabled")
            .where(Expression.of("label").isEqualTo(label))
            .executeSerializable(PickupSlot.class);
    }

    public static void updateCapacity(String label, int capacity) {
        label = requireNonBlank(label, "Label");
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be greater than 0");
        if (getPickupSlotByLabel(label) == null) throw new IllegalArgumentException("Pickup slot with label " + label + " does not exist");

        UpdateManager.update(App.DATA_BASE, TablePickupSlot.class)
            .set("capacity", capacity)
            .where(Expression.of("label").isEqualTo(label))
            .execute();
    }

    public static void deletePickupSlot(String label) {
        label = requireNonBlank(label, "Label");
        if (getPickupSlotByLabel(label) == null) throw new IllegalArgumentException("Pickup slot with label " + label + " does not exist");

        DeletionManager.delete(App.DATA_BASE, TablePickupSlot.class)
            .where(Expression.of("label").isEqualTo(label))
            .execute();
    }

    private static String requireNonBlank(String value, String label) {
        if (value == null) throw new IllegalArgumentException(label + " cannot be null");
        final String normalizedValue = value.trim();
        if (normalizedValue.isEmpty()) throw new IllegalArgumentException(label + " cannot be empty");
        return normalizedValue;
    }
}
