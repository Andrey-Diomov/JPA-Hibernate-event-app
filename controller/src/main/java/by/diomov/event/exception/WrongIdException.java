package by.diomov.event.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WrongIdException extends RuntimeException {
    private static final String ERROR_MESSAGE = "wrongIdMessage";
    private final long id;

    public String getErrorMessage() {
        return ERROR_MESSAGE;
    }
}