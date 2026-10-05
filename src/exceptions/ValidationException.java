package exceptions;
//
public class ValidationException extends AppException {
	private final  String field;

	public ValidationException(String field, String message) {
		super(Severity.WARNING,"Invalid value for '"+ field + "': "+ message);
		this.field=field;
	}

	public String getField() {
		return field;
	}
	
}
