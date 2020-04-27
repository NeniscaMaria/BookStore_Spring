package SDI.server.service;

import SDI.server.repository.DataBase.BookDataBaseRepository;
import domain.Sort;
import SDI.server.repository.Repository;
import Service.BookServiceInterface;
import domain.ValidatorException;
import domain.Book;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class BookService implements BookServiceInterface {
    private Repository<Long, Book> repository;
    private ExecutorService executorService;

    public BookService(Repository<Long, Book> repository, ExecutorService executorService) {
        this.repository = repository;
        this.executorService = executorService;
    }

    public synchronized CompletableFuture<Optional<Book>> addBook(domain.Book book) throws ValidatorException, ParserConfigurationException, TransformerException, SAXException, IOException, SQLException {
        return CompletableFuture.supplyAsync(()->{
            try {
                return repository.save(book);
            } catch (ParserConfigurationException | IOException | SAXException | TransformerException | SQLException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        }, executorService);
    }

    public CompletableFuture<Set<Book>> getAllBooks() throws SQLException {
        return CompletableFuture.supplyAsync(()->{
            Iterable<Book> books = null;
            try {
                books = repository.findAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            assert books != null;
            return StreamSupport.stream(books.spliterator(), false).collect(Collectors.toSet());
//            return (HashSet)StreamSupport.stream(books.spliterator(), false).collect(Collectors.toSet());

        }, executorService);
    }



    public Iterable<Book> getAllBooks(String ...a) throws SQLException {
        // Return all books from the repository
        Iterable<domain.Book> books;
        if (repository instanceof BookDataBaseRepository){
            books = ((BookDataBaseRepository)repository).findAll(new Sort(a).and(new Sort(a)));
            return StreamSupport.stream(books.spliterator(), false).collect(Collectors.toList());
        }
        else throw new ValidatorException("Too many parameters");

    }

    public CompletableFuture<Set<Book>> filterBooksByTitle(String s) throws SQLException {
        return CompletableFuture.supplyAsync(()-> {
            Iterable<Book> books = null;
            try {
                books = repository.findAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Set<Book> bookSet = new HashSet<>();
            books.forEach(bookSet::add);
            bookSet.removeIf(book -> !book.getTitle().contains(s));
            return bookSet;
        }, executorService);
    }

    public CompletableFuture<Optional<Book>> updateBook(Book book) throws SQLException {
        return CompletableFuture.supplyAsync(()->{
            try {
                return repository.update(book);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        }, executorService);
    }

    public CompletableFuture<Optional<Book>> removeBook(Long bookID) throws ValidatorException, SQLException {
        return CompletableFuture.supplyAsync(()->{
            try {
                return repository.delete(bookID);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        }, executorService);
    }

    public CompletableFuture<Optional<Book>> findOneBook(Long bookID) throws SQLException {
        return CompletableFuture.supplyAsync(()->{
            try {
                return repository.findOne(bookID);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        }, executorService);
    }


}
