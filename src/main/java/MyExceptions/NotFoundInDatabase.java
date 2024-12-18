package MyExceptions;
/** Исключение при отсутствии в БД*/
public class NotFoundInDatabase extends RuntimeException {
    public NotFoundInDatabase(String message) {
        super(message);
    }
}
