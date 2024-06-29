package ma.ac.uit.ensa.ssi.Booku.storage;

public class DatabaseError extends Exception {
    public enum ExceptionType {
        Constraint,
        GeneralFailure,
        NoMatch
    }

    private final ExceptionType type;

    public DatabaseError(ExceptionType type, String message) {
        super(message);
        this.type = type;
    }

    public ExceptionType getType() {
        return type;
    }
}
