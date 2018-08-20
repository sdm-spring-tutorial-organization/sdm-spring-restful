package example.app;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Service
public class BookService {
    private final Map<String, BookResource> bookRepository = new ConcurrentHashMap<>();

    // == init ==
    @PostConstruct
    public void loadDummyData() {
        BookResource book = new BookResource();
        book.setBookId("1234");
        book.setName("무한도전");
        book.setAuthors(Arrays.asList("유재석, 박명수, 정준하, 정형돈, 노홍철, 하하"));
        book.setPublishedDate(LocalDate.of(2018, 8, 20));
        BookResource.BookPublisher publisher = new BookResource.BookPublisher();
        publisher.setName("MBC");
        publisher.setTel("02-1234-5678");
        book.setPublisher(publisher);
        bookRepository.put(book.getBookId(), book);
    }

    // == find ==
    public BookResource find(String bookId) {
        BookResource bookResource = bookRepository.get(bookId);
        return bookResource;
    }

    // == create ==
    public BookResource create(BookResource book) {
        String bookId = UUID.randomUUID().toString();
        book.setBookId(bookId);
        bookRepository.put(bookId, book);
        return book;
    }

    // == update ==
    public BookResource update(BookResource book) {
        return bookRepository.put(book.getBookId(), book);
    }

    // == delete ==
    public BookResource delete(String bookId) {
        return bookRepository.remove(bookId);
    }

    // == findAllByCriteria ==
    public List<BookResource> findAllByCriteria(BookResourceQuery criteria) {
        return bookRepository.values().stream().filter(book ->
            (
                criteria.getName() == null
                ||
                book.getName().contains(criteria.getName())
            )
            &&
            (
                criteria.getPublishedDate() == null
                ||
                book.getPublishedDate().equals(criteria.getPublishedDate())
            )
        ).sorted((o1, o2) -> o1.getPublishedDate().compareTo(o2.getPublishedDate()))
            .collect(Collectors.toList());
    }
}
