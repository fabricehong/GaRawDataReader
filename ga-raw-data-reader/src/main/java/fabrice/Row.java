package fabrice;

import com.google.api.services.analytics.model.GaData;
import com.google.common.base.Joiner;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by fabrice on 10.08.15.
 */
public class Row {
    public static final String GA_NTH_MINUTE = "ga:nthMinute";
    private final GlobalCsvColumnIndex globalCsvColumnIndex;
    private Long time;
    private LinkedHashMap<GaData.ColumnHeaders, String> columnValues;

    public Row(List<GaData.ColumnHeaders> columnHeaders, List<String> row, GlobalCsvColumnIndex globalCsvColumnIndex) {
        this.globalCsvColumnIndex = globalCsvColumnIndex;
        if (columnHeaders.size()<1) {
            throw new RuntimeException(String.format("Provided column header should be at least of size 1. Size of the one provided : %s", columnHeaders.size()));
        }
        if (!columnHeaders.get(0).getName().equals(GA_NTH_MINUTE)) {
            throw new RuntimeException(String.format("The first column header should be '%s'. Provided column headers : %s", GA_NTH_MINUTE, Joiner.on(", ").join(columnHeaders)));
        }
        this.columnValues = new LinkedHashMap<GaData.ColumnHeaders, String>();
        if (columnHeaders.size()!=row.size()) {
            throw new RuntimeException("Not the same : " + Joiner.on(",").join(columnHeaders) + " (col. headers), " + Joiner.on(",").join(row) + " (row)");
        }
        this.time = Long.parseLong(row.get(0));
        for (int i=1; i<columnHeaders.size(); i++) {
            GaData.ColumnHeaders header = columnHeaders.get(i);
            globalCsvColumnIndex.newHeader(header);
            this.columnValues.put(header, row.get(i));
        }
    }

    public LinkedHashMap<GaData.ColumnHeaders, String> getValues() {
        return this.columnValues;
    }

    public Long getTime() {
        return time;
    }

    public void addAllAbsent(Row row) {
        for (GaData.ColumnHeaders header : row.getColumnHeaders()) {
            setIfNotExist(header, row.getValue(header));
        }
    }

    public String getValue(GaData.ColumnHeaders header) {
        return columnValues.get(header);
    }

    private void setIfNotExist(GaData.ColumnHeaders header, String value) {
        String v = getValue(header);
        if (v==null) {
            columnValues.put(header, value);
        }
    }

    private Iterable<? extends GaData.ColumnHeaders> getColumnHeaders() {
        return columnValues.keySet();
    }
}
