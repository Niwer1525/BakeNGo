package server.objects;

import niwer.queryon.SQLSerializable;
import niwer.queryon.tables.api.IColumnField;

public class Product extends SQLSerializable<Product> {

    @IColumnField(name = "id", primaryKey = true, autoIncrement = true)
    private int id;

    @IColumnField(name = "name", unique = true, notNull = true)
    private String name;

    @IColumnField(name = "description")
    private String description;

    @IColumnField(name = "price_cents", notNull = true)
    private int priceCents;

    @IColumnField(name = "stock", notNull = true)
    private int stock;

    @IColumnField(name = "is_active", notNull = true)
    private boolean isActive = true;

    public int id() { return id; }

    public String name() { return name; }

    public String description() { return description; }

    public int priceCents() { return priceCents; }

    public int stock() { return stock; }

    public boolean isActive() { return isActive; }
}