package Exception;

public class InvalidNoteNameException extends Exception {
    public InvalidNoteNameException(String msg) {
        super(msg);
    }

    public InvalidNoteNameException() {
        super();
    }
}
