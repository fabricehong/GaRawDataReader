package fabrice.app;


import fabrice.analytics.AnalyticsReader;
import fabrice.domain.AnalyticsResults;
import fabrice.csv.GACsvPrinter;
import fabrice.domain.Statistics;
import fabrice.exceptions.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;


/**
 * A simple example of how to access the Google Analytics API using a service
 * account.
 */
public class Main {
    private final Logger logger;
    private AnalyticsReader analyticsReader;

    static final String serviceAccountEmail = "403956333252-1ffjiiemc69q8fbf820troj72b5p2vor@developer.gserviceaccount.com";   // liip account
    static final String secretKeyFileLocation = "/media/Reservoir/Dropbox/machine learning/analytics analyzer/liip/machineLearningWeka-f629013b3332.p12"; // liip account

//    static final String serviceAccountEmail = "601649274311-dk5lfpjrng8d3kb9rtup4oavc4nv80mg@developer.gserviceaccount.com"; // my account
//    static final String secretKeyFileLocation = "/home/fabrice/datamining/GaRawDataReader/client_secret.p12"; // my account

    public static void main(String[] args) {
        Main main = new Main();

        try {
            main.doJob();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Main() {
        this.logger = LoggerFactory.getLogger(getClass());
    }

    public void doJob() {
        Statistics statistics = new Statistics();
        analyticsReader = new AnalyticsReader(serviceAccountEmail, secretKeyFileLocation, statistics);
        System.out.println("First Profile Id: " + analyticsReader.getProfileId());
        AnalyticsResults analyticsResults = analyticsReader.readAnalyticsResults(new String[]{
                //"ga:keyword",
                //"ga:sessionDurationBucket",
                //"ga:medium",
                "sessions::condition::ga:medium"
        }, "2012-01-01", "2013-01-01");
        saveResults(analyticsResults, "/home/fabrice/tmp/file1.csv", statistics);
        this.logger.info(statistics.toString());
    }

    private void saveResults(AnalyticsResults analyticsResults, String filePath, Statistics statistics) {
        try {
            GACsvPrinter csvPrinter = new GACsvPrinter(analyticsResults.createCsvContent(), statistics);
            FileWriter fileWriter = new FileWriter(filePath);
            csvPrinter.write(fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            throw new TechnicalException(e);
        }
    }


}
