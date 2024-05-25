package controller.hint;

public class ServerClosedException extends Exception {
	public ServerClosedException(String errorMessage) {
		super(errorMessage);
	}
}
