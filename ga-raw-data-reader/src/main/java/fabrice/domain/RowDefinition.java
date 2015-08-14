package fabrice.domain;

import com.google.api.services.analytics.model.GaData;
import com.google.common.base.Joiner;
import fabrice.analytics.AnalyticsReader;
import fabrice.analytics.CollectionUtils;
import fabrice.exceptions.TechnicalException;

import java.util.*;

/**
 * Created by fabrice on 14.08.15.
 */
public abstract class RowDefinition {
	public static final int MAX_ID_SIZE = AnalyticsReader.GA_MAX_DIMENSIONS - 1;
	private final SortedSet<String> idHeaders;
	private final List<String> infoHeaders;

	public RowDefinition(String[] idHeaders, String[] infoHeaders) {
        checkForGA_NT_MINUTE(idHeaders);
        CollectionUtils.checkNoDuplicates(idHeaders);
        CollectionUtils.checkNoDuplicates(infoHeaders);
		this.idHeaders = new TreeSet<String>(Arrays.asList(idHeaders));
		this.infoHeaders = Arrays.asList(infoHeaders);
		if (idHeaders.length>= MAX_ID_SIZE) {
			throw new TechnicalException(String.format("Impossible to instantiate a row definition id of size %s. Max is %s", idHeaders.length, MAX_ID_SIZE));
		}
	}

    private void checkForGA_NT_MINUTE(String... headers) {
        if (headers.length<1) {
            throw new RuntimeException(String.format("Provided column header should be at least of size 1. Size of the one provided : %s", headers.length));
        }
        if (!headers[0].equals(GaHeader.GA_NTH_MINUTE.getHeaderCode())) {
            throw new RuntimeException(String.format("The first column header should be '%s'. Provided column headers : %s", GaHeader.GA_NTH_MINUTE.getHeaderCode(), Joiner.on(", ").join(headers)));
        }
    }

	public RowIdCollector createRowInformationCollector() {
		return new RowIdCollector(infoHeaders);
	}

	public RowIdCollector createRowIdCollector() {
		return new RowIdCollector(idHeaders);
	}

	public int getIdSize() {
		return this.idHeaders.size();
	}

	public boolean isHeaderAllowedInCsv(GaData.ColumnHeaders header) {
		return false;
	}

	public Collection<String> getIdHeaders() {
		return idHeaders;
	}

    public boolean isIdHeader(String header) {
        return this.idHeaders.contains(header);
    }
}
