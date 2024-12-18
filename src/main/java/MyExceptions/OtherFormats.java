package MyExceptions;
/** Исключение при сохранении файла в неверном формате*/
public class OtherFormats extends Exception {
  public OtherFormats(String message) {
    super(message);
  }
}
