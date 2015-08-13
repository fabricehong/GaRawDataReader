package fabrice;


import fabrice.exceptions.TechnicalException;

import java.io.FileWriter;
import java.io.IOException;


/**
 * A simple example of how to access the Google Analytics API using a service
 * account.
 */
public class Main {
    static final String secretKeyFileLocation = "/home/fabrice/datamining/GaRawDataReader/client_secret.p12";
    static final String serviceAccountEmail = "601649274311-dk5lfpjrng8d3kb9rtup4oavc4nv80mg@developer.gserviceaccount.com";

    private AnalyticsReader analyticsReader;

    public static void main(String[] args) {
        Main main = new Main();

        try {
            main.doJob();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void doJob() {
        analyticsReader = new AnalyticsReader(serviceAccountEmail, secretKeyFileLocation);
        System.out.println("First Profile Id: "+ analyticsReader.getProfileId());
        AnalyticsResults analyticsResults = analyticsReader.readAnalyticsResults(new String[]{
                "ga:keyword",
                "ga:sessionDurationBucket",
                "ga:daysSinceLastSession",
                "ga:userType",
                "ga:country",
                "ga:region",
                "ga:operatingSystem"
        }, "2012-01-01", "today");
        saveResults(analyticsResults, "/home/fabrice/tmp/file1.csv");
    }

    private void saveResults(AnalyticsResults analyticsResults, String filePath) {
        try {
            GACsvPrinter csvPrinter = new GACsvPrinter(analyticsResults.createCsvContent());
            FileWriter fileWriter = new FileWriter(filePath);
            csvPrinter.write(fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            throw new TechnicalException(e);
        }
    }


}
