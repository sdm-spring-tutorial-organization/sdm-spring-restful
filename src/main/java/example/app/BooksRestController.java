package example.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.exception.BookResourceNotFoundException;
import example.model.BookResource;
import example.model.BookResourceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("books")
public class BooksRestController {

    @Autowired
    ObjectMapper bookMapper;

    @Autowired
    BookService bookService;

    // == getBook ==
    @CrossOrigin(maxAge = 900)
    @RequestMapping(path="{bookId}", method=RequestMethod.GET)
    public BookResource getBook(@PathVariable String bookId) {
        BookResource book = bookService.find(bookId);
        if(book == null) throw new BookResourceNotFoundException(bookId);

        BookResource resource = new BookResource();
        resource.setBookId(book.getBookId());
        resource.setName(book.getName());
        resource.setPublishedDate(book.getPublishedDate());
        resource.setAuthors(book.getAuthors());
        resource.setPublisher(book.getPublisher());

        return resource;
    }

    // == createBook ==
    @RequestMapping(method=RequestMethod.POST)
    public ResponseEntity<Void> createBook(
            @Validated @RequestBody BookResource newResource,
            UriComponentsBuilder uriBuilder
    ) {
        BookResource newBook = new BookResource();
        newBook.setName(newResource.getName());
        newBook.setPublishedDate(newResource.getPublishedDate());
        newBook.setAuthors(newResource.getAuthors());
        newBook.setPublisher(newResource.getPublisher());
        BookResource createBook = bookService.create(newBook);

        // == URI조립 (기존방식) ==
        // String resourceUri = "http://localhost:8080/books/" + createBook.getBookId();

        // == URI조립 (MvcUriComponentBuilder방식) ==
        // URI resourceUri = MvcUriComponentsBuilder.relativeTo(uriBuilder)
        //         .withMethodCall(on(BooksRestController.class))
        //         .getBook(createBook.getBookId()).build().encode().toUri();

        // == URI조립 (UriComponentsBuilder방식) ==
        URI resourceUri = uriBuilder.path("books/{bookId}")
                .buildAndExpand(createBook.getBookId()).encode().toUri();

        return ResponseEntity.created(resourceUri).build();
    }

    // == updateBook ==
    @RequestMapping(path="{bookId}", method=RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT) // (?) - 응답할 HTTP 상태코드지정
    public void updateBook(
            @PathVariable String bookId,
            @Validated @RequestBody BookResource resource
    ) {
        BookResource book = new BookResource();
        book.setBookId(bookId);
        book.setName(resource.getName());
        book.setPublishedDate(resource.getPublishedDate());
        book.setAuthors(resource.getAuthors());
        book.setPublisher(resource.getPublisher());
        bookService.update(book);
    }

    // == deleteBook ==
    @RequestMapping(path="{bookId}", method=RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT) // 응답할 HTTP 상태코드
    public void deleteBook(@PathVariable String bookId) {
        bookService.delete(bookId);
    }

    // == searchBooks ==
    @RequestMapping(method = RequestMethod.GET)
    public List<BookResource> searchBooks(@Validated BookResourceQuery query) {
        BookResourceQuery criteria = new BookResourceQuery();
        criteria.setName(query.getName());
        criteria.setPublishedDate(query.getPublishedDate());
        List<BookResource> books = bookService.findAllByCriteria(criteria);

        return books.stream().map(book -> {
            BookResource resource = new BookResource();
            resource.setBookId(book.getBookId());
            resource.setName(book.getName());
            resource.setPublishedDate(book.getPublishedDate());
            resource.setAuthors(book.getAuthors());
            resource.setPublisher(book.getPublisher());
            return resource;
        }).collect(Collectors.toList());
    }



}
