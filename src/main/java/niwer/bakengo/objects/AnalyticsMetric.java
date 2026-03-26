package niwer.bakengo.objects;

import niwer.queryon.SQLSerializable;
import niwer.queryon.tables.api.IColumnField;

public class AnalyticsMetric extends SQLSerializable<AnalyticsMetric> {

    @IColumnField(name = "id", primaryKey = true, autoIncrement = true)
    private int id;

    @IColumnField(name = "metric_key", notNull = true)
    private String metricKey;

    @IColumnField(name = "metric_value", notNull = true)
    private String metricValue;

    @IColumnField(name = "metric_date", notNull = true)
    private String metricDate;

    @IColumnField(name = "created_at", notNull = true)
    private String createdAt;

    public int id() { return id; }

    public String metricKey() { return metricKey; }

    public String metricValue() { return metricValue; }

    public String metricDate() { return metricDate; }

    public String createdAt() { return createdAt; }
}