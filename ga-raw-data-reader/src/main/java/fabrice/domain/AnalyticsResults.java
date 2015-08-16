package fabrice.domain;

import com.google.api.services.analytics.model.GaData;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import fabrice.analytics.RequestedDimensions;
import fabrice.csv.CsvContent;
import fabrice.csv.CsvUtils;
import fabrice.csv.GlobalCsvColumnIndex;
import fabrice.exceptions.InvalidRowException;
import fabrice.exceptions.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by fabrice on 10.08.15.
 */
public class AnalyticsResults {

    protected Logger logger;
    private LinkedHashMap<Infos, AnalyticsRow> rows;
    private GlobalCsvColumnIndex globalCsvColumnIndex;
    private RequestedDimensions requestedDimensions;

    public AnalyticsResults(RequestedDimensions requestedDimensions) {
        this.requestedDimensions = requestedDimensions;
        this.logger = LoggerFactory.getLogger(getClass());
        this.globalCsvColumnIndex = new GlobalCsvColumnIndex();
        this.rows = new LinkedHashMap<Infos, AnalyticsRow>();
    }

    public void addAllAbsent(GaData gaData) {
        boolean idMustExist = !this.rows.isEmpty();
        for (List<String> row : gaData.getRows()) {
            AnalyticsRow analyticsRow = null;
            try {
                analyticsRow = AnalyticsRow.create(requestedDimensions, gaData.getColumnHeaders(), row, globalCsvColumnIndex);
                addRow(analyticsRow, idMustExist);
            } catch (InvalidRowException e) {
                String rowStr = analyticsRow != null ? analyticsRow.toString() : row.toString();
                this.logger.debug(String.format("Impossible to add row '%s'", rowStr), e);
                continue;
            }
        }
    }

    private void addRow(AnalyticsRow analyticsRow, boolean idMustExist) throws InvalidRowException {
        AnalyticsRow analyticsRowInMap = this.rows.get(analyticsRow.getId());
        if (analyticsRowInMap ==null) {
            if (idMustExist) {
                throw new TechnicalException(String.format("Unknown row id : %s", analyticsRow.getId()));
            } else {
                this.rows.put(analyticsRow.getId(), analyticsRow);
            }
        } else {
            analyticsRowInMap.addAllAbsent(analyticsRow);
        }
    }

    public CsvContent createCsvContent() {
        final GaData.ColumnHeaders[] headers = this.globalCsvColumnIndex.getHeaders();
        Collection<String[]> csvRows = Collections2.transform(this.rows.entrySet(), new Function<Map.Entry<Infos, AnalyticsRow>, String[]>() {
            public String[] apply(final Map.Entry<Infos, AnalyticsRow> longRowEntry) {
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
