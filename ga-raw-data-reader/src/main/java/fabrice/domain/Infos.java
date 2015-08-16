package fabrice.domain;

import fabrice.app.GaHeader;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Fabrice Hong -- Liip AG
 * @date 14.08.15
 */
public class Infos extends HashMap<String, String> {
	public Infos(Map<String, String> collectedValues) {
		super(collectedValues);
	}

	public String getHeaderValue(GaHeader header) {
		return get(header.getHeaderCode());
	}
}
