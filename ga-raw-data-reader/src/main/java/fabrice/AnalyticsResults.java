package fabrice;

import com.google.api.services.analytics.model.GaData;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by fabrice on 10.08.15.
 */
public class AnalyticsResults {
    private GaData gaData;
    private TreeMap<Long, Row> rows;
    private GlobalCsvColumnIndex globalCsvColumnIndex;

    public AnalyticsResults() {
        this.globalCsvColumnIndex = new GlobalCsvColumnIndex();
        this.rows = new TreeMap<Long, Row>();
    }

    public void addAllAbsent(GaData gaData) {
        for (List<String> row : gaData.getRows()) {
            addRow(new Row(gaData.getColumnHeaders(), row, globalCsvColumnIndex));
        }
    }

    private void addRow(Row row) {
        Row rowInMap = this.rows.get(row.getTime());
        if (rowInMap==null) {
            this.rows.put(row.getTime(), row);
        } else {
            rowInMap.addAllAbsent(row);
        }
    }

    public TreeMap<Long, Row> getRows() {
        return rows;
    }


    public Iterable<?> getHeaders() {
        return null;
    }

    public CsvContent createCsvContent() {
        final GaData.ColumnHeaders[] headers = this.globalCsvColumnIndex.getHeaders();
        Collection<String[]> csvRows = Collections2.transform(this.rows.entrySet(), new Function<Map.Entry<Long, Row>, String[]>() {
            public String[] apply(final Map.Entry<Long, Row> longRowEntry) {
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
