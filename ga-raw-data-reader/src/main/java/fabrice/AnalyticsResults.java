package fabrice;

import com.google.api.services.analytics.model.GaData;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import java.util.*;

/**
 * Created by fabrice on 10.08.15.
 */
public class AnalyticsResults {
    private TreeMap<Long, AnalyticsRow> rows;
    private GlobalCsvColumnIndex globalCsvColumnIndex;

    public AnalyticsResults() {
        this.globalCsvColumnIndex = new GlobalCsvColumnIndex();
        this.rows = new TreeMap<Long, AnalyticsRow>();
    }

    public void addAllAbsent(GaData gaData) {
        for (List<String> row : gaData.getRows()) {
            addRow(new AnalyticsRow(gaData.getColumnHeaders(), row, globalCsvColumnIndex));
        }
    }

    private void addRow(AnalyticsRow AnalyticsRow) {
        AnalyticsRow analyticsRowInMap = this.rows.get(AnalyticsRow.getTime());
        if (analyticsRowInMap ==null) {
            this.rows.put(AnalyticsRow.getTime(), AnalyticsRow);
        } else {
            analyticsRowInMap.addAllAbsent(AnalyticsRow);
        }
    }

    public TreeMap<Long, AnalyticsRow> getRows() {
        return rows;
    }


    public Iterable<?> getHeaders() {
        return null;
    }

    public CsvContent createCsvContent() {
        final GaData.ColumnHeaders[] headers = this.globalCsvColumnIndex.getHeaders();
        Collection<String[]> csvRows = Collections2.transform(this.rows.entrySet(), new Function<Map.Entry<Long, AnalyticsRow>, String[]>() {
            public String[] apply(final Map.Entry<Long, AnalyticsRow> longRowEntry) {
                String[] row = new String[headers.length];
                for (GaData.ColumnHeaders header : headers) {
                    row[globalCsvColumnIndex.getHeaderIndex(header)] = CsvUtils.escape(longRowEntry.getValue().getValue(header));
                }

                return row;
            }
        });
        Collection<String> headerStrings = Collections2.transform(Arrays.asList(headers), new Function<GaData.ColumnHeaders, String>() {
            public String apply(GaData.ColumnHeaders columnHeaders) {
                return CsvUtils.escape(columnHeaders.getName());
            }
        });
        return new CsvContent(headerStrings, csvRows);
    }
}
