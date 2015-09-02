package fabrice.domain;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabrice on 16.08.15.
 */
public class Statistics {
    private int nbIncompleteRows = 0;
    private List<String> rowsMergeFailed;
    private int gaRequestNb = 0;
    private int printedCsvLines = 0;

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
        sb.append(String.format("Number row merge failed: %s\n", rowsMergeFailed.size()));
        sb.append(String.format("Failed row merge id's:\n%s\n", Joiner.on("\n").join(rowsMergeFailed)));
        sb.append(String.format("Number of incomplete rows: %s\n", nbIncompleteRows));
        sb.append(String.format("Number of printed csv lines: %s\n", printedCsvLines));
        return sb.toString();
    }

    public void incrementPrintedCsvLines() {
        this.printedCsvLines++;
    }
}