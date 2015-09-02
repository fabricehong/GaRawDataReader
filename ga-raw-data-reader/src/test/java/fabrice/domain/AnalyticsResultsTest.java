package fabrice.domain;

import com.google.api.services.analytics.model.GaData;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import fabrice.TestRowDefinition;
import fabrice.analytics.RequestedDimensions;
import fabrice.csv.CsvContent;
import org.junit.Test;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by fabrice on 16.08.15.
 */
public class AnalyticsResultsTest {

    private String[] idHeaders = new String[]{"ga:nthMinute", "ga:sessions"};
    private String[] infoHeaders = new String[]{};
    private String[] headerToIgnoreInCsv = new String[]{"ga:sessions"};
    private String[] headersToExcludeFromDimensionRequest = new String[]{};
    private RowDefinition rowDefinition = new TestRowDefinition(idHeaders, infoHeaders, headerToIgnoreInCsv, headersToExcludeFromDimensionRequest);
    private Statistics statistics = new Statistics();

    @Test
    public void testMergeDataWithSameId() throws Exception {
        RequestedDimensions requestedDimensions = new RequestedDimensions(rowDefinition, 4);
        AnalyticsResults analyticsResults = new AnalyticsResults(requestedDimensions, statistics);

        GaData gaData1 = createData(new String[]{"ga:nthMinute", "dim1", "ga:sessions"},
                new String[]{"1", "data1", "4"}
        );

        GaData gaData2 = createData(new String[]{"ga:nthMinute", "dim2", "ga:sessions"},
                new String[]{"1", "data2", "4"}
        );

        analyticsResults.addAllAbsent(gaData1);
        analyticsResults.addAllAbsent(gaData2);

        CsvContent csvContent = analyticsResults.createCsvContent();
        assertCsvContentId(csvContent, new String[]{"ga:nthMinute", "dim1", "dim2"},
                new String[]{"1", "data1", "data2"}
        );
    }

    @Test
    public void testDontMergeDataWithSameId() throws Exception {
        RequestedDimensions requestedDimensions = new RequestedDimensions(rowDefinition, 4);
        AnalyticsResults analyticsResults = new AnalyticsResults(requestedDimensions, statistics);

        GaData gaData1 = createData(new String[]{"ga:nthMinute", "dim1", "ga:sessions"},
                new String[]{"1", "data1", "4"}
        );

        GaData gaData2 = createData(new String[]{"ga:nthMinute", "dim2", "ga:sessions"},
                new String[]{"1", "data2", "5"}
        );

        analyticsResults.addAllAbsent(gaData1);
        analyticsResults.addAllAbsent(gaData2);

        CsvContent csvContent = analyticsResults.createCsvContent();
        assertCsvContentId(csvContent, new String[]{"ga:nthMinute", "dim1", "dim2"},
                new String[]{"1", "data1", ""}

        );
    }

    @Test
    public void testCrossRequestsBadMerges() throws Exception {
        RequestedDimensions requestedDimensions = new RequestedDimensions(rowDefinition, 4);
        AnalyticsResults analyticsResults = new AnalyticsResults(requestedDimensions, statistics);

        GaData gaData1 = createData(new String[]{"ga:nthMinute", "dim1", "ga:sessions"},
                new String[]{"1", "data1Users1", "3"},
                new String[]{"1", "data1Users2", "4"}
        );

        GaData gaData2 = createData(new String[]{"ga:nthMinute", "dim2", "ga:sessions"},
                new String[]{"1", "data2Users2", "3"}, // rien n'assure quand dans la request 2, les données ne soient en fait pas associées au user 2 pour un nombre de session 3
                new String[]{"1", "data2Users1", "4"}
        );

        analyticsResults.addAllAbsent(gaData1);
        analyticsResults.addAllAbsent(gaData2);

        CsvContent csvContent = analyticsResults.createCsvContent();
        assertCsvContentId(csvContent, new String[]{"ga:nthMinute", "dim1", "dim2"},
                new String[]{"1", "data1Users1", "data2Users1"},
                new String[]{"1", "data1Users2", "data2Users2"}

        );
    }

	@Test
	public void testInnerRequestsBadMerges() throws Exception {
		// if the same request there is multiple time the same key, then remove the line completely because we know it will be invalid anyway

		RequestedDimensions requestedDimensions = new RequestedDimensions(rowDefinition, 4);
		AnalyticsResults analyticsResults = new AnalyticsResults(requestedDimensions, statistics);

		GaData gaData1 = createData(new String[]{"ga:nthMinute", "dim1", "ga:sessions"},
				new String[]{"1", "data1Users1", "3"},
				new String[]{"1", "data1Users2", "3"}
		);

		analyticsResults.addAllAbsent(gaData1);

		CsvContent csvContent = analyticsResults.createCsvContent();
		assertCsvContentId(csvContent, new String[]{"ga:nthMinute", "dim1"}

		);
	}

    private void assertCsvContentId(CsvContent csvContent, String[] expectedHeaders, String[]... expectedLines) {
        ArrayList<String> headers = new ArrayList<String>(csvContent.getHeaders());
        assertThat(headers, equalTo(Arrays.asList(expectedHeaders)));

        List<String[]> lines = new ArrayList<String[]>(csvContent.getLines());
        assertThat(lines.size(), equalTo(expectedLines.length));

        for (int i=0; i<lines.size(); i++) {
            assertThat(lines.get(i), equalTo(expectedLines[i]));
        }
    }

    private GaData createData(String[] headers, String[]... lines) {
        GaData gaData = new GaData();
        List<GaData.ColumnHeaders> columnHeaders = new ArrayList<GaData.ColumnHeaders>(Collections2.transform(Arrays.asList(headers), new Function<String, GaData.ColumnHeaders>() {
            @Nullable
            @Override
            public GaData.ColumnHeaders apply(String s) {
                GaData.ColumnHeaders columnHeaders = new GaData.ColumnHeaders();
                columnHeaders.setName(s);
                return columnHeaders;
            }
        }));
        List<String[]> strings = Arrays.asList(lines);
        List<List<String>> lineResult = new ArrayList<List<String>>(Collections2.transform(strings, new Function<String[], List<String>>() {
            @Nullable
            @Override
            public List<String> apply(@Nullable String[] strings) {
                return Arrays.asList(strings);
            }
        }));
        gaData.setColumnHeaders(columnHeaders);
        gaData.setRows(lineResult);
        return gaData;

    }
}