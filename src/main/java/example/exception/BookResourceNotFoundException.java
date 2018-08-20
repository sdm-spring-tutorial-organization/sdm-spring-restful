package example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // "message": "Not Found"
public class BookResourceNotFoundException extends RuntimeException {
    public BookResourceNotFoundException(String bookId) {
        super("Book is not found ( bookId = " + bookId + " )");
    }
}