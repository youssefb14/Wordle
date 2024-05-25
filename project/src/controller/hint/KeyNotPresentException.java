package controller.hint;

public class KeyNotPresentException extends GensimException {
    public KeyNotPresentException(String errorMessage) {
        super(errorMessage);
    }
}
