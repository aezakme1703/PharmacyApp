package MyExceptions;
/** Исключение при вводе отрицательного значения*/
public class NegativeIntException extends Exception {
    public NegativeIntException(String message) {
        super(message);
    }
}
