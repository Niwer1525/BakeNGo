package server.tables;

import java.time.Instant;
import java.util.List;

import niwer.queryon.DataBase;
import niwer.queryon.queries.interaction.InsertionManager;
import niwer.queryon.queries.interaction.SelectionManager;
import niwer.queryon.tables.Table;
import server.App;
import server.objects.AnalyticsMetric;

public class TableAnalyticsMetric extends Table {

    public TableAnalyticsMetric(DataBase db) {
        super(db);

        this.addColumnsFromClass(AnalyticsMetric.class).execute();
    }

    @Override public String name() { return "analytics_metrics"; }

    public static void addMetric(String metricKey, String metricValue, String metricDate) {
        metricKey = requireNonBlank(metricKey, "Metric key");
        metricValue = requireNonBlank(metricValue, "Metric value");
        metricDate = requireNonBlank(metricDate, "Metric date");

        InsertionManager.insert(App.DATA_BASE, TableAnalyticsMetric.class, "metric_key", "metric_value", "metric_date", "created_at")
            .row(metricKey, metricValue, metricDate, Instant.now().toString())
            .execute();
    }

    /**
     * Retrieves all analytics metrics from the database, ordered by creation date in descending order.
     * 
     * @return a list of all analytics metrics, or an empty list if no metrics are found
     */
    public static List<AnalyticsMetric> getAllMetrics() {
        final SelectionManager query = SelectionManager.select(App.DATA_BASE, TableAnalyticsMetric.class,
                "COALESCE(NULLIF(id, 0), rowid) AS id", "metric_key", "metric_value", "metric_date", "created_at")
            .orderBy("rowid", SelectionManager.EnumOrder.DESC);

        try {
            return query.executeList(AnalyticsMetric.class);
        } catch (IllegalStateException ignored) {
            final AnalyticsMetric single = query.executeSerializable(AnalyticsMetric.class);
            return single == null ? List.of() : List.of(single);
        }
    }

    private static String requireNonBlank(String value, String label) {
        if (value == null) throw new IllegalArgumentException(label + " cannot be null");
        final String normalizedValue = value.trim();
        if (normalizedValue.isEmpty()) throw new IllegalArgumentException(label + " cannot be empty");
        return normalizedValue;
    }
}
