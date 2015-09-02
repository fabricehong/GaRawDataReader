package fabrice.domain;

import com.google.api.services.analytics.model.GaData;
import fabrice.exceptions.InvalidRowException;

import java.util.List;

/**
 * @author Fabrice Hong -- Liip AG
 * @date 17.08.15
 */
public class RequestResult {
	private RequestResult(GaData gaData) {
		for (List<String> row : gaData.getRows()) {
			AnalyticsRow analyticsRow = null;
			try {
				analyticsRow = AnalyticsRow.create(requestedDimensions, gaData.getColumnHeaders(), row, globalCsvColumnIndex);
				addRow(analyticsRow, idMustExist);
			} catch (InvalidRowException e) {
				String rowStr = analyticsRow!=null?analyticsRow.getId().toString():row.toString();
				statistics.rowWithoutId(rowStr);
				continue;
			}
		}
	}

	private void addRow(AnalyticsRow analyticsRow, boolean idMustExist) throws InvalidRowException {
		AnalyticsRow analyticsRowInMap = this.rows.get(analyticsRow.getId());
		if (analyticsRowInMap ==null) {
			if (idMustExist) {
				throw new InvalidRowException(String.format("Unknown row id : %s", analyticsRow.getId()));
			} else {
				this.rows.put(analyticsRow.getId(), analyticsRow);
			}
		} else {
			analyticsRowInMap.addAllAbsent(analyticsRow);
		}
	}
}
