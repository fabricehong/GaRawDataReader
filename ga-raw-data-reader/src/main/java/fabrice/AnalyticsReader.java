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
import fabrice.exceptions.TechnicalException;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class AnalyticsReader {
	private final String APPLICATION_NAME = "Hello Analytics";
	private final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private final String secretKeyFileLocation;
	private final String serviceAccountEmail;
	private Analytics analytics;
	private String profileId;

	public AnalyticsReader(String serviceAccountEmail, String secretKeyFileLocation) {
		this.secretKeyFileLocation = secretKeyFileLocation;
		this.serviceAccountEmail = serviceAccountEmail;
		analytics = initializeAnalytics();
		profileId = getFirstProfileId(analytics);
	}

	public AnalyticsResults readAnalyticsResults(String[] dimensions, String dateStart, String dateEnd) {
		Iterator<String> partitionedDimensions = getPartitionedDimensions(dimensions);
		int i = 0;
		AnalyticsResults analyticsResults = new AnalyticsResults();
		while (partitionedDimensions.hasNext()) {
			String next = partitionedDimensions.next();
			System.out.println("RAPPORT " + i++);
			GaData results = getResultsForMax7Dimensions(next, dateStart, dateEnd);
			printResults(results);
			analyticsResults.addAllAbsent(results);
		}
		return analyticsResults;
	}

	private void printResults(GaData results) {
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
	private Iterator<String> getPartitionedDimensions(String... dimensions) {
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

	private GaData getResultsForMax7Dimensions(String dimensions, String dateStart, String dateEnd) {
		// Query the Core Reporting API for the number of sessions
		// in the past seven days.
		try {
			Analytics.Data.Ga.Get today = analytics.data().ga()
					.get("ga:" + profileId, dateStart, dateEnd, "ga:sessions");

			today.setDimensions(dimensions);
			System.out.println(today.getDimensions());
			return today
					.execute();
		} catch (IOException e) {
			throw new TechnicalException(e);
		}
	}

	public String getProfileId() {
		return profileId;
	}

	private Analytics initializeAnalytics() throws TechnicalException {
		// Initializes an authorized analytics service object.

		// Construct a GoogleCredential object with the service account email
		// and p12 file downloaded from the developer console.
		HttpTransport httpTransport = null;
		GoogleCredential credential = null;
		try {
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			credential = new GoogleCredential.Builder()
					.setTransport(httpTransport)
					.setJsonFactory(JSON_FACTORY)
					.setServiceAccountId(serviceAccountEmail)
					.setServiceAccountPrivateKeyFromP12File(new File(secretKeyFileLocation))
					.setServiceAccountScopes(AnalyticsScopes.all())
					.build();
		} catch (GeneralSecurityException e) {
			throw new TechnicalException(e);
		} catch (IOException e) {
			throw new TechnicalException(e);
		}

		// Construct the Analytics service object.
		return new Analytics.Builder(httpTransport, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME).build();
	}

	private String getFirstProfileId(Analytics analytics) {
		// Get the first view (profileId) ID for the authorized user.
		String profileId = null;

		try {
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
						// Return the first (view) profileId associated with the property.
						profileId = profiles.getItems().get(0).getId();
					}
				}
			}

		} catch (IOException e) {
			throw new TechnicalException(e);
		}
		return profileId;
	}
}