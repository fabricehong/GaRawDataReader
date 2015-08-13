package fabrice.exceptions;

/**
 * @author Fabrice Hong -- Liip AG
 * @date 13.08.15
 */
public class TechnicalException extends RuntimeException {
	public TechnicalException(Throwable e) {
		super(e);
	}
}
