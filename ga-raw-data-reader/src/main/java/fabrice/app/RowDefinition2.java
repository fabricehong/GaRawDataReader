package fabrice.app;

import fabrice.domain.RowDefinition;

/**
 * Created by fabrice on 14.08.15.
 */
public class RowDefinition2 extends RowDefinition {
	private static String[] idHeaders = new String[]{
			GaHeader.GA_SESSIONS.getHeaderCode()
//			GaHeader.GA_REGION.getHeaderCode()
	};

	private static String[] headersToExcludeFromDimensionRequest = new String[]{
			GaHeader.GA_SESSIONS.getHeaderCode()
	};

	private static String[] infoHeaders = new String[]{
			GaHeader.GA_SESSIONS.getHeaderCode()
	};

	private static String[] headerToIgnoreOnCsv = new String[]{
			GaHeader.GA_SESSIONS.getHeaderCode()
	};

	public RowDefinition2() {
		super(idHeaders, infoHeaders, headerToIgnoreOnCsv, headersToExcludeFromDimensionRequest);
	}
}
