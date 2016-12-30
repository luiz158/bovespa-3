package br.com.destrosoft.indicator.importer;

public class ImporterException extends Exception {

    private static final long serialVersionUID = -6991573442384216701L;

    public ImporterException() {
        super();
    }

    public ImporterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ImporterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImporterException(String message) {
        super(message);
    }

    public ImporterException(Throwable cause) {
        super(cause);
    }

}
