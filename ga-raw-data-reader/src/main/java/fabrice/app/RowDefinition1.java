package fabrice.app;

import fabrice.domain.GaHeader;
import fabrice.domain.RowDefinition;

/**
 * Created by fabrice on 14.08.15.
 */
public class RowDefinition1 extends RowDefinition {
	private static String[] idHeaders = new String[]{
			GaHeader.GA_NTH_MINUTE.getHeaderCode()
	};

	private static String[] infoHeaders = new String[]{
			GaHeader.GA_SESSIONS.getHeaderCode()
	};

	public RowDefinition1() {
		super(idHeaders, infoHeaders);
	}
}
