package fabrice.analytics;

import com.google.common.collect.Iterators;
import fabrice.TestRowDefinition;
import fabrice.app.GaHeader;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


/**
 * Created by fabrice on 14.08.15.
 */
public class RequestedDimensionsTest {

    public static final String ID_HEADER2 = "idHeader2";
    public static final String ID_HEADER1 = GaHeader.GA_NTH_MINUTE.getHeaderCode();
    private static final String[] idHeaders = new String[] {
            ID_HEADER1,
            ID_HEADER2
    };

    private static final String[] infoHeaders = new String[] {};
    public static final int MAX_DIMENSIONS_IN_REQUEST = 4;
    private TestRowDefinition testRowDefinition;

    @Before
    public void setUp() {
        testRowDefinition = new TestRowDefinition(idHeaders, infoHeaders);
    }

    @Test
    public void testNoPartitionning() throws Exception {
        String dimension1 = "lolo";
        String dimension2 = "lulu";
        RequestedDimensions requestedDimensions = new RequestedDimensions(testRowDefinition, MAX_DIMENSIONS_IN_REQUEST,
                dimension1, dimension2
        );
        Iterator<String> partitionedValidDimensions = requestedDimensions.getPartitionedDimensions();
        assertDimensions(partitionedValidDimensions,
                new String[][]{
                        new String[]{
                                ID_HEADER1, ID_HEADER2, dimension1, dimension2
                        }
                }
        );
    }

    @Test
    public void testPartitionning() throws Exception {
        String dimension1 = "lolo";
        String dimension2 = "lulu";
        String dimension3 = "lili";
        RequestedDimensions requestedDimensions = new RequestedDimensions(testRowDefinition, MAX_DIMENSIONS_IN_REQUEST,
                dimension1, dimension2, dimension3
        );
        Iterator<String> partitionedValidDimensions = requestedDimensions.getPartitionedDimensions();
        assertDimensions(partitionedValidDimensions,
                new String[][]{
                    new String[]{
                            ID_HEADER1, ID_HEADER2, dimension1, dimension2
                    },
                    new String[]{
                            ID_HEADER1, ID_HEADER2, dimension3
                    }
                }

        );
    }

    @Test
    public void testPartitionningHeaderInFirstPartition() throws Exception {
        String dimension1 = "lolo";
        String dimension2 = "lulu";
        String dimension3 = "lili";
        RequestedDimensions requestedDimensions = new RequestedDimensions(testRowDefinition, MAX_DIMENSIONS_IN_REQUEST,
                dimension1, dimension2, ID_HEADER2, dimension3
        );
        Iterator<String> partitionedValidDimensions = requestedDimensions.getPartitionedDimensions();
        assertDimensions(partitionedValidDimensions,
                new String[][]{
                        new String[]{
                                ID_HEADER1, ID_HEADER2, dimension1, dimension2
                        },
                        new String[]{
                                ID_HEADER1, ID_HEADER2, dimension3
                        }
                }

        );
    }

    @Test
    public void testPartitionningHeaderInSecondPartition() throws Exception {
        String dimension1 = "lolo";
        String dimension2 = "lulu";
        String dimension3 = "lili";
        RequestedDimensions requestedDimensions = new RequestedDimensions(testRowDefinition, MAX_DIMENSIONS_IN_REQUEST,
                dimension1, dimension2, dimension3, ID_HEADER2
        );
        Iterator<String> partitionedValidDimensions = requestedDimensions.getPartitionedDimensions();
        assertDimensions(partitionedValidDimensions,
                new String[][]{
                        new String[]{
                                ID_HEADER1, ID_HEADER2, dimension1, dimension2
                        },
                        new String[]{
                                ID_HEADER1, ID_HEADER2, dimension3
                        }
                }

        );
    }

    private <T> void assertDimensions(Iterator<String> partitionedValidDimensions, String[]... expectedPartitions) {
        String[] strings = Iterators.toArray(partitionedValidDimensions, String.class);
        assertThat(strings.length, equalTo(expectedPartitions.length));
        for (int i = 0; i<strings.length; i++) {
            String[] splittedDimensions = strings[i].split(",");
            assertThat(splittedDimensions, equalTo(expectedPartitions[i]));
        }
    }
}

