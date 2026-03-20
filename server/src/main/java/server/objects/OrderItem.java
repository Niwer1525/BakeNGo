package server.objects;

import niwer.queryon.SQLSerializable;
import niwer.queryon.tables.api.IColumnField;

public class OrderItem extends SQLSerializable<OrderItem> {

    @IColumnField(name = "id", primaryKey = true, autoIncrement = true)
    private int id;

    @IColumnField(name = "order_id", notNull = true)
    private int orderId;

    @IColumnField(name = "product_id", notNull = true)
    private int productId;

    @IColumnField(name = "quantity", notNull = true)
    private int quantity;

    @IColumnField(name = "unit_price_cents", notNull = true)
    private int unitPriceCents;

    @IColumnField(name = "line_total_cents", notNull = true)
    private int lineTotalCents;

    public int id() { return id; }

    public int orderId() { return orderId; }

    public int productId() { return productId; }

    public int quantity() { return quantity; }

    public int unitPriceCents() { return unitPriceCents; }

    public int lineTotalCents() { return lineTotalCents; }
}