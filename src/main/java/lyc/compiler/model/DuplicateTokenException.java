package lyc.compiler.model;

public class DuplicateTokenException extends CompilerException{

    public DuplicateTokenException(String message) {
        super(message);
    }
}