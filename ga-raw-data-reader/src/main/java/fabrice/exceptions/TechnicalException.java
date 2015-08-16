package fabrice.exceptions;

/**
 * @author Fabrice Hong -- Liip AG
 * @date 13.08.15
 */
public class TechnicalException extends RuntimeException {
	public TechnicalException(Throwable e) {
		super(e);
	}

	public TechnicalException(String message) {
		super(message);
	}

	public TechnicalException(String message, InvalidRowException exception) {
		super(message, exception);
	}
}
