package fabrice.domain;

/**
 * @author Fabrice Hong -- Liip AG
 * @date 14.08.15
 */
public enum GaHeader {
	GA_NTH_MINUTE("ga:nthMinute"),
	GA_SESSIONS("ga:sessions");

	private final String headerCode;

	GaHeader(String headerCode) {
		this.headerCode = headerCode;
	}

	public String getHeaderCode() {
		return headerCode;
	}
}
