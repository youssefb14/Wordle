package controller.hint;

public class InvalidNumberOfWordsException extends GensimException {
    public InvalidNumberOfWordsException(String errorMessage) {
        super(errorMessage);
    }
}
