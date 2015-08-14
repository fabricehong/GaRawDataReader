package fabrice.csv;

/**
 * Created by fabrice on 10.08.15.
 */
public class CsvUtils {
    public static String escape(String str) {
        return str.replaceAll("\"", "\"\"");
    }
}
