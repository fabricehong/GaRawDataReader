package fabrice.analytics;

import fabrice.exceptions.TechnicalException;

import java.util.LinkedHashSet;

/**
 * Created by fabrice on 14.08.15.
 */
public class CollectionUtils {

    public static void checkNoDuplicates(String... dimensions) {
        LinkedHashSet<String> askedDimensions = new LinkedHashSet<String>();
        for (String dimension : dimensions) {
            if (askedDimensions.contains(dimension)) {
                throw new TechnicalException(String.format("You can't specify more than one time the dimension '%s'. Provided dimensions : %s", dimension, dimensions));
            }
            askedDimensions.add(dimension);
        }
    }
}
