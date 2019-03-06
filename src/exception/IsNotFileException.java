package exception;

public class IsNotFileException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IsNotFileException() {
		super();
	}

	public IsNotFileException(String msg) {
		super(msg);
	}
}
