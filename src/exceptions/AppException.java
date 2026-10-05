package exceptions;

public class AppException extends Exception {

	private final Severity severity;
	
	public AppException(Severity s,String message) {
		super(message);
		severity=s;
	}

	public Severity getSeverity() {
		return severity;
	}
	
	@Override
	public String toString() {
		
		return ("[" + getSeverity() + "] " + getMessage());
	}
	
}
