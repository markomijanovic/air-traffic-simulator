package exceptions;

public class FileException extends AppException {

	private final String file;

    public FileException(String file, String message ) {
        super( Severity.ERROR,"File error at '" + file + "': " + message );
        this.file = file;
    }

    public String getFilePath() {
        return file;
    }
}
