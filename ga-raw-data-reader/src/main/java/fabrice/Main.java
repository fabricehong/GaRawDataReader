package fabrice;


import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analytics.model.Accounts;
import com.google.api.services.analytics.model.GaData;
import com.google.api.services.analytics.model.Profiles;
import com.google.api.services.analytics.model.Webproperties;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * A simple example of how to access the Google Analytics API using a service
 * account.
 */
public class Main {


    private static final String APPLICATION_NAME = "Hello Analytics";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String KEY_FILE_LOCATION = "/home/fabrice/Downloads/client_secrets.p12";
    private static final String SERVICE_ACCOUNT_EMAIL = "601649274311-dk5lfpjrng8d3kb9rtup4oavc4nv80mg@developer.gserviceaccount.com";
    public static void main(String[] args) {
        try {
            Analytics analytics = initializeAnalytics();

            String profile = getFirstProfileId(analytics);
            System.out.println("First Profile Id: "+ profile);

            Iterator<String> partitionnedDimensions = getPartitionnedDimensions("ga:keyword", "ga:sessionDurationBucket", "ga:daysSinceLastSession", "ga:userType", "ga:country", "ga:region", "ga:operatingSystem");
            int i = 0;
            Result result = new Result();
            while (partitionnedDimensions.hasNext()) {
                String next = partitionnedDimensions.next();
                System.out.println("RAPPORT " + i++);
                GaData results = getResults(analytics, profile, next);
                result.addAllAbsent(results);
                printResults(results);
            }
            saveResults(result, "/home/fabrice/tmp/file1.csv");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void saveResults(Result result, String filePath) throws IOException {
        GACsvPrinter csvPrinter = new GACsvPrinter(result.createCsvContent());
        FileWriter fileWriter = new FileWriter(filePath);
        csvPrinter.write(fileWriter);
        fileWriter.close();
    }

    private static Analytics initializeAnalytics() throws Exception {
        // Initializes an authorized analytics service object.

        // Construct a GoogleCredential object with the service account email
        // and p12 file downloaded from the developer console.
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
                .setServiceAccountPrivateKeyFromP12File(new File(KEY_FILE_LOCATION))
                .setServiceAccountScopes(AnalyticsScopes.all())
                .build();

        // Construct the Analytics service object.
        return new Analytics.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME).build();
    }


    private static String getFirstProfileId(Analytics analytics) throws IOException {
        // Get the first view (profile) ID for the authorized user.
        String profileId = null;

        // Query for the list of all accounts associated with the service account.
        Accounts accounts = analytics.management().accounts().list().execute();

        if (accounts.getItems().isEmpty()) {
            System.err.println("No accounts found");
        } else {
            String firstAccountId = accounts.getItems().get(0).getId();

            // Query for the list of properties associated with the first account.
            Webproperties properties = analytics.management().webproperties()
                    .list(firstAccountId).execute();

            if (properties.getItems().isEmpty()) {
                System.err.println("No Webproperties found");
            } else {
                String firstWebpropertyId = properties.getItems().get(0).getId();

                // Query for the list views (profiles) associated with the property.
                Profiles profiles = analytics.management().profiles()
                        .list(firstAccountId, firstWebpropertyId).execute();

                if (profiles.getItems().isEmpty()) {
                    System.err.println("No views (profiles) found");
                } else {
                    // Return the first (view) profile associated with the property.
                    profileId = profiles.getItems().get(0).getId();
                }
            }
        }
        return profileId;
    }

    private static GaData getResults(Analytics analytics, String profileId, String dimensions) throws IOException {
        // Query the Core Reporting API for the number of sessions
        // in the past seven days.
        Analytics.Data.Ga.Get today = analytics.data().ga()
                .get("ga:" + profileId, "2012-01-01", "today", "ga:sessions");

        today.setDimensions(dimensions);
        System.out.println(today.getDimensions());
        return today
                .execute();
    }

    private static Iterator<String> getPartitionnedDimensions(String... dimensions) {
        UnmodifiableIterator<List<String>> partition = Iterators.partition(Arrays.asList(dimensions).iterator(), 6);
        Iterator<String> transform = Iterators.transform(partition, new Function<List<String>, String>() {
            @Nullable
            @Override
            public String apply(@Nullable List<String> strings) {
                ImmutableList<Object> list = ImmutableList.builder().add("ga:nthMinute").addAll(strings).build();
                return Joiner.on(",").join(list);
            }
        });
        return transform;
    }

    private static void printResults(GaData results) {
        // Parse the response from the Core Reporting API for
        // the profile name and number of sessions.
        if (results != null && !results.getRows().isEmpty()) {
            System.out.println("View (Profile) Name: "
                    + results.getProfileInfo().getProfileName());
            Collection<String> rowsStr = Collections2.transform(results.getRows(), new Function<List<String>, String>() {
                @Override
                public String apply(List<String> strings) {
                    return Joiner.on(", ").join(strings);
                }
            });
            String join = Joiner.on("\n").join(rowsStr);
            System.out.println(join);

            System.out.println("Total Rows: " + results.getRows().size());
        } else {
            System.out.println("No results found");
        }
    }
}
