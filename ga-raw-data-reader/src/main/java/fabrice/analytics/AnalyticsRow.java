package fabrice.analytics;

import com.google.api.services.analytics.model.GaData;
import com.google.common.base.Joiner;
import fabrice.csv.GlobalCsvColumnIndex;
import fabrice.exceptions.InvalidRowException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fabrice on 10.08.15.
 */
public class AnalyticsRow {
    public static final String GA_NTH_MINUTE = "ga:nthMinute";

    private LinkedHashMap<GaData.ColumnHeaders, String> columnValues;
    private RowDefinition rowDefinition;
    private int sessionNumber;
    private Infos rowId;

    public static AnalyticsRow create(RowDefinition rowDefinition, List<GaData.ColumnHeaders> columnHeaders, List<String> row, GlobalCsvColumnIndex globalCsvColumnIndex) throws InvalidRowException {
        AnalyticsRow analyticsRow = new AnalyticsRow(rowDefinition);
        analyticsRow.initialize(columnHeaders, row, globalCsvColumnIndex);
        return analyticsRow;
    }

    private AnalyticsRow(RowDefinition rowDefinition) {

        this.rowDefinition = rowDefinition;
    }

    private void initialize(List<GaData.ColumnHeaders> columnHeaders, List<String> row, GlobalCsvColumnIndex globalCsvColumnIndex) throws InvalidRowException {
        checkForGA_NT_MINUTE(columnHeaders);
        this.columnValues = new LinkedHashMap<GaData.ColumnHeaders, String>();
        if (columnHeaders.size()!=row.size()) {
            throw new InvalidRowException("Not the same : " + Joiner.on(",").join(columnHeaders) + " (col. headers), " + Joiner.on(",").join(row) + " (row)");
        }

        RowIdCollector rowInformationCollector = rowDefinition.createRowInformationCollector();
        RowIdCollector rowIdCollector = rowDefinition.createRowIdCollector();

        for (int i=1; i<columnHeaders.size(); i++) {
            GaData.ColumnHeaders header = columnHeaders.get(i);
            String value = row.get(i);
            rowInformationCollector.collect(header, value);
            rowIdCollector.collect(header, value);
            if (rowDefinition.isHeaderAllowedInCsv(header)) {
                globalCsvColumnIndex.newHeader(header);
                this.columnValues.put(header, value);
            }
        }

        Infos infos = rowInformationCollector.create();
        sessionNumber = Integer.parseInt(infos.getHeaderValue(GaHeader.GA_SESSIONS));
        this.rowId = rowIdCollector.create();
    }

    boolean canBeConsideredAsData(RowDefinition rowDefinition, GaData.ColumnHeaders header) {

    }

    private void checkForGA_NT_MINUTE(List<GaData.ColumnHeaders> columnHeaders) {
        if (columnHeaders.size()<1) {
            throw new RuntimeException(String.format("Provided column header should be at least of size 1. Size of the one provided : %s", columnHeaders.size()));
        }
        if (!columnHeaders.get(0).getName().equals(GA_NTH_MINUTE)) {
            throw new RuntimeException(String.format("The first column header should be '%s'. Provided column headers : %s", GA_NTH_MINUTE, Joiner.on(", ").join(columnHeaders)));
        }
    }

    public Infos getId() {
        return rowId;
    }

    public void addAllAbsent(AnalyticsRow AnalyticsRow) {
        for (GaData.ColumnHeaders header : AnalyticsRow.getColumnHeaders()) {
            setIfNotExist(header, AnalyticsRow.getValue(header));
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

    public int getSessionNumber() {
        return sessionNumber;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        sb.append("[");
        sb.append(String.format("id=%s, ", rowId));
        sb.append(String.format("sessions nb=%s, ", sessionNumber));
        sb.append("data=");
        sb.append("{");
        for (Map.Entry<GaData.ColumnHeaders, String> entry : this.columnValues.entrySet()) {
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }
            sb.append(entry.getKey().getName()).append(":").append(entry.getValue());

        }
        sb.append("}");
        sb.append("]");
        return sb.toString();
    }
}
