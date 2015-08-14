package fabrice.csv;

import com.google.api.services.analytics.model.GaData;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by fabrice on 10.08.15.
 */
public class GlobalCsvColumnIndex {
    private LinkedHashMap<GaData.ColumnHeaders, Integer> headerIndexes;

    public GlobalCsvColumnIndex() {
        this.headerIndexes = new LinkedHashMap<GaData.ColumnHeaders, Integer>();
    }

    public void newHeader(GaData.ColumnHeaders header) {
        if (!this.headerIndexes.containsKey(header)) {
            this.headerIndexes.put(header, this.headerIndexes.size());
        }
    }

    public int getHeaderIndex(GaData.ColumnHeaders header) {
        Integer index = this.headerIndexes.get(header);
        if (index==null) {
            throw new RuntimeException(String.format("Impossible to find index of heaer '%s'", header));
        }
        return index;
    }

    public GaData.ColumnHeaders computeHeaders() {
        return null;
        //return this.headerIndexes.size();
    }

    public GaData.ColumnHeaders[] getHeaders() {
        return this.headerIndexes.keySet().toArray(new GaData.ColumnHeaders[0]);
    }
}
