package niwer.bakengo.objects;

import niwer.queryon.SQLSerializable;
import niwer.queryon.tables.api.IColumnField;

public class CustomerOrder extends SQLSerializable<CustomerOrder> {

    @IColumnField(name = "id", primaryKey = true, autoIncrement = true)
    private int id;

    @IColumnField(name = "user_id")
    private Integer userId;

    @IColumnField(name = "customer_email", notNull = true)
    private String customerEmail;

    @IColumnField(name = "status", notNull = true)
    private String status = "PENDING";

    @IColumnField(name = "pickup_slot", notNull = true)
    private String pickupSlot;

    @IColumnField(name = "pickup_date", notNull = true)
    private String pickupDate;

    @IColumnField(name = "total_cents", notNull = true)
    private int totalCents;

    @IColumnField(name = "created_at", notNull = true)
    private String createdAt;

    @IColumnField(name = "updated_at", notNull = true)
    private String updatedAt;

    public int id() { return id; }

    public Integer userId() { return userId; }

    public String customerEmail() { return customerEmail; }

    public String status() { return status; }

    public String pickupSlot() { return pickupSlot; }

    public String pickupDate() { return pickupDate; }

    public int totalCents() { return totalCents; }

    public String createdAt() { return createdAt; }

    public String updatedAt() { return updatedAt; }
}