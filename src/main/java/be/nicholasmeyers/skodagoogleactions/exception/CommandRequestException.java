package be.nicholasmeyers.skodagoogleactions.exception;

public class CommandRequestException extends RuntimeException {
    public CommandRequestException(String message) {
        super(message);
    }
}
