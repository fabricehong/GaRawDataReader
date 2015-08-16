package fabrice.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabrice on 16.08.15.
 */
public class Statistics {
    private int nbIncompleteRows = 0;
    private List<String> rowsMergeFailed;
    private int gaRequestNb = 0;

    public Statistics() {
        this.rowsMergeFailed = new ArrayList<String>();
    }

    public void incrementNbIncompleteRows() {
        this.nbIncompleteRows++;
    }

    public void rowWithoutId(String rowStr) {
        this.rowsMergeFailed.add(rowStr);
    }

    public void incrementGaRequestNb() {
        this.gaRequestNb++;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nReport:\n");
        sb.append("-------\n");
        sb.append(String.format("Number of ga request merged: %s\n", gaRequestNb));
        sb.append(String.format("Number row merge failed: %s\n", rowsMergeFailed));
        sb.append(String.format("Number of incomplete rows: %s\n", nbIncompleteRows));
        return sb.toString();
    }
}