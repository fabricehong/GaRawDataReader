package fabrice;

import fabrice.domain.RowDefinition;

/**
 * Created by fabrice on 16.08.15.
 */
public class TestRowDefinition extends RowDefinition {

    public TestRowDefinition(String[] idHeaders, String[] infoHeaders) {
        this(idHeaders, infoHeaders, null);
    }

    public TestRowDefinition(String[] idHeaders, String[] infoHeaders, String[] headerToIgnoreInCsv) {
        super(idHeaders, infoHeaders, headerToIgnoreInCsv);
    }
}