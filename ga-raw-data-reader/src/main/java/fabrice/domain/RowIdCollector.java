package fabrice.domain;

import com.google.api.services.analytics.model.GaData;
import fabrice.exceptions.InvalidRowException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Fabrice Hong -- Liip AG
 * @date 14.08.15
 */
public class RowIdCollector {
	private Map<String, String> collectedValues;
	public RowIdCollector(Collection<String> gaHeaders) {
		this.collectedValues = new HashMap<String, String>();
		for (String header : gaHeaders) {
			this.collectedValues.put(header, null);
		}
	}

	public void collect(GaData.ColumnHeaders header, String value) {
		String headerName = header.getName();
		if(this.collectedValues.containsKey(headerName)) {
			this.collectedValues.put(headerName, value);
		}
	}

	public Infos create() throws InvalidRowException {
		for (Map.Entry<String, String> entry : this.collectedValues.entrySet()) {
			if (entry.getValue()==null || "".equals(entry.getValue().trim())) {
				throw new InvalidRowException(String.format("Mandatory field '%s' have not been collected", entry.getKey()));
			}
		}
		return new Infos(this.collectedValues);
	}
}
