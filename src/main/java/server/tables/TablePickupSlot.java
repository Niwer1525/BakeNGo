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

    /**
     * Adds a new pickup slot to the database with the specified day, start time, end time, capacity, and enabled status.
     * 
     * @param day The day of the pickup slot to add (must be non-blank)
     * @param startTime The start time of the pickup slot (must be non-blank)
     * @param endTime The end time of the pickup slot (must be non-blank)
     * @param capacity The maximum number of items that can be stored in the pickup slot (must be positive)
     * @param isEnabled Whether the pickup slot is enabled and available for use
     */
    public static synchronized void addPickupSlot(String day, String startTime, String endTime, int capacity, boolean isEnabled) {
        day = App.requireNonBlank(day, "Day");
        startTime = App.requireNonBlank(startTime, "Start time");
        endTime = App.requireNonBlank(endTime, "End time");
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be greater than 0");
        final int PICKUP_SLOT_ID = getNextPickupSlotId();

        InsertionManager.insert(App.DATA_BASE, TablePickupSlot.class, "id", "day", "start_time", "end_time", "capacity", "is_enabled")
            .row(PICKUP_SLOT_ID, day, startTime, endTime, capacity, isEnabled)
            .execute();
    }

    /**
     * Gets all pickup slots from the database, ordered by id ascending.
     * 
     * @return A list of all pickup slots in the database, ordered by id ascending. If there are no pickup slots, returns an empty list.
     */
    public static List<PickupSlot> getAllPickupSlots() {
        final SelectionManager QUERY = SelectionManager.select(App.DATA_BASE, TablePickupSlot.class,
            "COALESCE(NULLIF(id, 0), rowid) AS id", "day", "start_time", "end_time", "capacity", "is_enabled")
            .orderBy("rowid", SelectionManager.EnumOrder.ASC);

        try {
            return QUERY.executeList(PickupSlot.class);
        } catch (IllegalStateException ignored) {
            final PickupSlot SINGLE = QUERY.executeSerializable(PickupSlot.class);
            return SINGLE == null ? List.of() : List.of(SINGLE);
        }
    }

    /**
     * Gets all enabled pickup slots from the database, ordered by id ascending.
     * 
     * @return A list of all enabled pickup slots in the database, ordered by id ascending. If there are no enabled pickup slots, returns an empty list.
     */
    public static List<PickupSlot> getEnabledPickupSlots() {
        final SelectionManager QUERY = SelectionManager.select(App.DATA_BASE, TablePickupSlot.class,
            "COALESCE(NULLIF(id, 0), rowid) AS id", "day", "start_time", "end_time", "capacity", "is_enabled")
            .where(Expression.of("is_enabled").isEqualTo(true))
            .orderBy("rowid", SelectionManager.EnumOrder.ASC);

        try {
            return QUERY.executeList(PickupSlot.class);
        } catch (IllegalStateException ignored) {
            final PickupSlot SINGLE = QUERY.executeSerializable(PickupSlot.class);
            return SINGLE == null ? List.of() : List.of(SINGLE);
        }
    }

    /**
     * Gets a pickup slot from the database by its id.
     * 
     * @param id The id of the pickup slot to get
     * @return The pickup slot with the given id, or null if no such pickup slot exists
     */
    public static PickupSlot getPickupSlotById(int id) {

        return SelectionManager.select(App.DATA_BASE, TablePickupSlot.class,
            "COALESCE(NULLIF(id, 0), rowid) AS id", "day", "start_time", "end_time", "capacity", "is_enabled")
            .where(Expression.of("COALESCE(NULLIF(id, 0), rowid)").isEqualTo(id))
            .executeSerializable(PickupSlot.class);
    }

    /**
     * Updates the capacity of a pickup slot in the database.
     * 
     * @param id The id of the pickup slot to update
     * @param capacity The new capacity for the pickup slot (must be positive)
     */
    public static void updateCapacity(int id, int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be greater than 0");
        if (getPickupSlotById(id) == null) throw new IllegalArgumentException("Pickup slot with id " + id + " does not exist");

        UpdateManager.update(App.DATA_BASE, TablePickupSlot.class)
            .set("capacity", capacity)
            .where(Expression.of("COALESCE(NULLIF(id, 0), rowid)").isEqualTo(id))
            .execute();
    }

    /**
     * Updates the enabled status of a pickup slot.
     *
     * @param id The id of the pickup slot to update
     * @param isEnabled The new enabled status
     */
    public static void updateEnabled(int id, boolean isEnabled) {
        if (getPickupSlotById(id) == null) throw new IllegalArgumentException("Pickup slot with id " + id + " does not exist");

        UpdateManager.update(App.DATA_BASE, TablePickupSlot.class)
            .set("is_enabled", isEnabled)
            .where(Expression.of("COALESCE(NULLIF(id, 0), rowid)").isEqualTo(id))
            .execute();
    }

    /**
     * Deletes a pickup slot from the database by its id.
     * 
     * @param id The id of the pickup slot to delete
     */
    public static void deletePickupSlot(int id) {
        if (getPickupSlotById(id) == null) throw new IllegalArgumentException("Pickup slot with id " + id + " does not exist");

        DeletionManager.delete(App.DATA_BASE, TablePickupSlot.class)
            .where(Expression.of("COALESCE(NULLIF(id, 0), rowid)").isEqualTo(id))
            .execute();
    }

    private static int getNextPickupSlotId() {
        return getAllPickupSlots().stream()
            .mapToInt(PickupSlot::id)
            .max()
            .orElse(0) + 1;
    }
}
