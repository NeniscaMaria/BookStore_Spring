package Service;

import domain.Book;
import domain.ValidatorException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface BookServiceInterface {
    public String GET_ALL_BOOKS = "getAllBooks";
    public String REMOVE_BOOK = "removeBook";
    public String ADD_BOOK = "addBook";
    public String UPDATE_BOOK = "updateBook";
    public String FIND_ONE = "findOneBook";
    public String FILTER_BOOKS = "filterBooks";

    CompletableFuture<Set<Book>> getAllBooks() throws SQLException;
    CompletableFuture<Optional<Book>> removeBook(Long id) throws SQLException;
    CompletableFuture<Optional<Book>> addBook(Book entity) throws SQLException, ValidatorException, ParserConfigurationException, TransformerException, SAXException, IOException;

    CompletableFuture<Optional<Book>> updateBook(Book entity) throws SQLException, ValidatorException;
    CompletableFuture<Optional<Book>> findOneBook(Long clientID) throws SQLException;
    CompletableFuture<Set<Book>> filterBooksByTitle(String s) throws SQLException;
}
