package fabrice;

import java.util.Collection;

/**
 * Created by fabrice on 10.08.15.
 */
public class CsvContent {
    private Collection<String> headers;
    private Collection<String[]> content;

    CsvContent(Collection<String> headers, Collection<String[]> content) {
        this.headers = headers;
        this.content = content;

    }

    public Collection<String> getHeaders() {
        return headers;
    }

    public Collection<String[]> getLines() {
        return content;
    }
}
