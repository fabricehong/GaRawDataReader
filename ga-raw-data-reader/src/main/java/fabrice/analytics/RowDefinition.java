package fabrice.analytics;

import com.google.api.services.analytics.model.GaData;
import fabrice.exceptions.TechnicalException;

/**
 * Created by fabrice on 14.08.15.
 */
public abstract class RowDefinition {
	public static final int MAX_ID_SIZE = AnalyticsReader.GA_MAX_DIMENSIONS - 1;
	private final String[] idHeaders;
	private final String[] infoHeaders;

	public RowDefinition(String[] idHeaders, String[] infoHeaders) {
		this.idHeaders = idHeaders;
		this.infoHeaders = infoHeaders;
		if (idHeaders.length>= MAX_ID_SIZE) {
			throw new TechnicalException(String.format("Impossible to instantiate a row definition id of size %s. Max is %s", idHeaders.length, MAX_ID_SIZE));
		}
	}

	public RowIdCollector createRowInformationCollector() {
		return new RowIdCollector(infoHeaders);
	}

	public RowIdCollector createRowIdCollector() {
		return new RowIdCollector(idHeaders);
	}

	public int getIdSize() {
		return this.idHeaders.length;
	}

	public boolean isHeaderAllowedInCsv(GaData.ColumnHeaders header) {
		return false;
	}

	public String[] getIdHeaders() {
		return idHeaders;
	}
}
