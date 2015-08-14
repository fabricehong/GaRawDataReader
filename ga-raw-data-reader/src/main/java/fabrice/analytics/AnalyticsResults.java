package fabrice.analytics;

import com.google.api.services.analytics.model.GaData;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import fabrice.csv.CsvContent;
import fabrice.csv.CsvUtils;
import fabrice.csv.GlobalCsvColumnIndex;
import fabrice.exceptions.InvalidRowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by fabrice on 10.08.15.
 */
public class AnalyticsResults {

    protected Logger logger;
    private TreeMap<Long, AnalyticsRow> rows;
    private GlobalCsvColumnIndex globalCsvColumnIndex;
    private IdDefinition idDefinition;

    public AnalyticsResults(IdDefinition idDefinition) {
        this.idDefinition = idDefinition;
        this.logger = LoggerFactory.getLogger(getClass());
        this.globalCsvColumnIndex = new GlobalCsvColumnIndex();
        this.rows = new TreeMap<Long, AnalyticsRow>();
    }

    public void addAllAbsent(GaData gaData) {
        for (List<String> row : gaData.getRows()) {
            AnalyticsRow analyticsRow = null;
            try {

                analyticsRow = AnalyticsRow.create(idDefinition, gaData.getColumnHeaders(), row, globalCsvColumnIndex);
            } catch (InvalidRowException e) {
                this.logger.debug(String.format("Impossible to construct row '%s'", row), e);
                continue;
            }
            try {
                addRow(analyticsRow);
            } catch (InvalidRowException e) {
                this.logger.debug(String.format("Impossible to add row '%s'", analyticsRow), e);
                continue;
            }
        }
    }

    private void addRow(AnalyticsRow analyticsRow) throws InvalidRowException {
        AnalyticsRow analyticsRowInMap = this.rows.get(analyticsRow.getId());
        if (analyticsRowInMap ==null) {
            this.rows.put(analyticsRow.getId(), analyticsRow);
        } else {
            if (analyticsRow.getSessionNumber()!=analyticsRowInMap.getSessionNumber()) {
                throw new InvalidRowException(
                        String.format(
                                "Cannot merge two rows with different session numbers. \nInitial row : %s\nNew row : %s",
                                analyticsRowInMap,
                                analyticsRow
                        )
                );
            }
            analyticsRowInMap.addAllAbsent(analyticsRow);
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
                String[] resultRow = new String[headers.length];
                for (GaData.ColumnHeaders header : headers) {
                    AnalyticsRow gaRow = longRowEntry.getValue();
                    resultRow[globalCsvColumnIndex.getHeaderIndex(header)] = CsvUtils.escape(gaRow.getValue(header));
                }

                return resultRow;
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
