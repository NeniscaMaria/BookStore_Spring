package Service;

import TCP.TCPClient;
import domain.Book;
import domain.Message;
import domain.ValidatorException;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class BookService {
    private ExecutorService executorService;
    private TCPClient tcpClient;

    public BookService(ExecutorService executorService, TCPClient tcpClient) {
        this.executorService = executorService;
        this.tcpClient = tcpClient;
    }

    public CompletableFuture<Message<Set<Book>>> getAllBooks(){
        return CompletableFuture.supplyAsync(()->{
            //create request
            Message request  = new Message(BookServiceInterface.GET_ALL_BOOKS,"");
            //send request to server and receive answer
            return tcpClient.sendAndReceive(request);
        },executorService);
    }

    public CompletableFuture<Message<Optional<Book>>> removeBook(Long id){
        return CompletableFuture.supplyAsync(()->{
            String ID = String.valueOf(id);
            Message request = new Message(BookServiceInterface.REMOVE_BOOK,ID);
            return tcpClient.sendAndReceive(request);
        },executorService);
    }

    public CompletableFuture<Message<Optional<Book>>> addBook(Book book){
        return CompletableFuture.supplyAsync(()-> {
            Message request = new Message(BookServiceInterface.ADD_BOOK, book);
            return tcpClient.sendAndReceive(request);
        },executorService);
    }

    public CompletableFuture<Message<Optional<Book>>> updateBook(Book entity) throws SQLException, ValidatorException {
        return CompletableFuture.supplyAsync(()->{
            Message request = new Message(BookServiceInterface.UPDATE_BOOK, entity);
            return tcpClient.sendAndReceive(request);
        },executorService);
    }
    public CompletableFuture<Message<Optional<Book>>> findOneBook(Long id) throws SQLException{
        return CompletableFuture.supplyAsync(()->{
            String ID = String.valueOf(id);
            Message request = new Message(BookServiceInterface.FIND_ONE,ID);
            return tcpClient.sendAndReceive(request);
        },executorService);
    }
    public CompletableFuture<Message<Set<Book>>> filterBooksByTitle(String s) throws SQLException{
        return CompletableFuture.supplyAsync(()->{
            Message request = new Message(BookServiceInterface.FILTER_BOOKS,s);
            return tcpClient.sendAndReceive(request);
        },executorService);
    }
}
