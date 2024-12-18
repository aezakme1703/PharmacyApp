package MyExceptions;
/** Исключение при вводе не на кириллице*/
public class OnlyLettersException extends Exception {
    public OnlyLettersException(String message) {
        super(message);
    }
}
