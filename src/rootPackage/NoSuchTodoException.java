package rootPackage;

@SuppressWarnings("serial")
public class NoSuchTodoException extends RuntimeException {

    public NoSuchTodoException(long id) {
        super("no such todo: id=" + id);
    }
}
