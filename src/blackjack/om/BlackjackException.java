package blackjack.om;

/**
 * Exception lancée si on fait une utilisation inappropriée des classes du package blackjack.om
 *  
 * @author Fabrice PELLEAU
 *
 */
@SuppressWarnings("serial")
public class BlackjackException extends RuntimeException {

	public BlackjackException(String message) {
		super(message);
	}

	public BlackjackException(String message, Throwable cause) {
		super(message, cause);
	}

}
