package fabrice.domain;

import com.google.api.services.analytics.model.GaData;
import com.google.common.base.Joiner;
import fabrice.analytics.RequestedDimensions;
import fabrice.app.GaHeader;
import fabrice.csv.GlobalCsvColumnIndex;
import fabrice.exceptions.InvalidRowException;
import fabrice.exceptions.TechnicalException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fabrice on 10.08.15.
 */
public class AnalyticsRow {

    private LinkedHashMap<GaData.ColumnHeaders, String> columnValues;
    private RowDefinition rowDefinition;
    private Infos rowId;

    public static AnalyticsRow create(RequestedDimensions requestedDimensions, List<GaData.ColumnHeaders> columnHeaders, List<String> row, GlobalCsvColumnIndex globalCsvColumnIndex) throws InvalidRowException {
        AnalyticsRow analyticsRow = new AnalyticsRow(requestedDimensions.getRowDefinition());
        analyticsRow.initialize(columnHeaders, row, globalCsvColumnIndex);
        return analyticsRow;
    }

    private AnalyticsRow(RowDefinition rowDefinition) {

        this.rowDefinition = rowDefinition;
    }

    private void initialize(List<GaData.ColumnHeaders> columnHeaders, List<String> row, GlobalCsvColumnIndex globalCsvColumnIndex) throws InvalidRowException {
        this.columnValues = new LinkedHashMap<GaData.ColumnHeaders, String>();
        if (columnHeaders.size()!=row.size()) {
            throw new TechnicalException("Not the same : " + Joiner.on(",").join(columnHeaders) + " (col. headers), " + Joiner.on(",").join(row) + " (row)");
        }

        RowIdCollector rowInformationCollector = rowDefinition.createRowInformationCollector();
        RowIdCollector rowIdCollector = rowDefinition.createRowIdCollector();

        for (int i=0; i<columnHeaders.size(); i++) {
            GaData.ColumnHeaders header = columnHeaders.get(i);
            String value = row.get(i);
            rowInformationCollector.collect(header, value);
            rowIdCollector.collect(header, value);
            if (rowDefinition.outputHeaderForCsv(header.getName())) {
                globalCsvColumnIndex.newHeader(header);
                this.columnValues.put(header, value);
            }
        }

        this.rowId = rowIdCollector.create();
    }

    boolean canBeConsideredAsData(RowDefinition rowDefinition, GaData.ColumnHeaders header) {
        return false;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        sb.append("[");
        sb.append(String.format("id=%s, ", rowId));
//        sb.append(String.format("sessions nb=%s, ", sessionNumber));
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
