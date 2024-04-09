package data;


public class DataBaseException extends Exception {
    public DataBaseException() {
        super("Erreur base de données");
    }

    public DataBaseException(String message) {
        super(message);
    }

    public DataBaseException(Throwable ex) {
        super(ex);
    }

    public DataBaseException(String message, Throwable ex) {
        super(message, ex);
    }
}


