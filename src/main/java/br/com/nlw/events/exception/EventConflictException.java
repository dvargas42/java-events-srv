package br.com.nlw.events.exception;

public class EventConflictException extends RuntimeException {
    public EventConflictException(String message) {
        super(message);
    }
}
