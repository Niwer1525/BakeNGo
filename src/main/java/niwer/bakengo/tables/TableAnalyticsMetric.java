package niwer.bakengo.tables;

import java.time.Instant;
import java.util.List;

import niwer.bakengo.App;
import niwer.bakengo.objects.AnalyticsMetric;
import niwer.queryon.DataBase;
import niwer.queryon.queries.interaction.InsertionManager;
import niwer.queryon.queries.interaction.SelectionManager;
import niwer.queryon.tables.Table;

public class TableAnalyticsMetric extends Table {

    public TableAnalyticsMetric(DataBase db) {
        super(db);

        this.addColumnsFromClass(AnalyticsMetric.class).execute();
    }

    @Override public String name() { return "analytics_metrics"; }

    /**
     * Adds a new analytics metric to the database with the specified key, value, and date. The creation timestamp is automatically set to the current time.
     * 
     * @param metricKey The key or name of the metric to add
     * @param metricValue The value of the metric to add
     * @param metricDate The date associated with the metric (e.g., the date the metric was recorded or is relevant for)
     */
    public static synchronized void addMetric(String metricKey, String metricValue, String metricDate) {
        metricKey = App.requireNonBlank(metricKey, "Metric key");
        metricValue = App.requireNonBlank(metricValue, "Metric value");
        metricDate = App.requireNonBlank(metricDate, "Metric date");
        final int metricId = getNextMetricId();

        InsertionManager.insert(App.DATA_BASE, TableAnalyticsMetric.class, "id", "metric_key", "metric_value", "metric_date", "created_at")
            .row(metricId, metricKey, metricValue, metricDate, Instant.now().toString())
            .execute();
    }

    /**
     * Retrieves all analytics metrics from the database, ordered by creation date in descending order.
     * 
     * @return a list of all analytics metrics, or an empty list if no metrics are found
     */
    public static List<AnalyticsMetric> getAllMetrics() {
        final SelectionManager QUERY = SelectionManager.select(App.DATA_BASE, TableAnalyticsMetric.class,
                "COALESCE(NULLIF(id, 0), rowid) AS id", "metric_key", "metric_value", "metric_date", "created_at")
            .orderBy("rowid", SelectionManager.EnumOrder.DESC);

        try {
            return QUERY.executeList(AnalyticsMetric.class);
        } catch (IllegalStateException ignored) {
            final AnalyticsMetric single = QUERY.executeSerializable(AnalyticsMetric.class);
            return single == null ? List.of() : List.of(single);
        }
    }

    private static int getNextMetricId() {
        return getAllMetrics().stream()
            .mapToInt(AnalyticsMetric::id)
            .max()
            .orElse(0) + 1;
    }
}
