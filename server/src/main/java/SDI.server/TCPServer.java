package SDI.server;

import SDI.server.service.BookService;
import SDI.server.service.ClientService;
import SDI.server.service.PurchaseService;
import Service.BookServiceInterface;
import Service.ClientServiceInterface;
import Service.PurchaseServiceInterface;
import domain.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class TCPServer {
    private ExecutorService executorService;
    private Map<String, UnaryOperator<Message<String>>> methodHandlers; //for methods that return string

    private Map<String, Function<Message<?>,Message<?>>> clientHandlers; // methods that return message

    private ClientService clientService;
    private BookService bookService;
    private PurchaseService purchaseService;

    public TCPServer(ExecutorService executorService, ClientService clientService, BookService bookService, PurchaseService purchaseService) {
        this.executorService = executorService;
        methodHandlers = new HashMap<>();
        clientHandlers = new HashMap<>();

        this.clientService = clientService;
        this.purchaseService = purchaseService;
        this.bookService = bookService;
        initializeHandlers();
    }

    private void initializeHandlersClient(){

        addHandler(ClientServiceInterface.GET_ALL_SORT,
                (request)->{
                    try {
                        CompletableFuture<Iterable<Client>> clients = clientService.getAllClients(request.getBody());
                        return new Message("success",clients.get());
                    } catch (SQLException | InterruptedException | ExecutionException e) {
                        return new Message("error", e.getMessage());
                    }
                });

        //getAllClients function
        //return Set<Client> DONE
        addClientHandler(ClientServiceInterface.GET_ALL_CLIENTS,
                (request) -> {
                    try {
                        CompletableFuture<Set<Client>> clients = clientService.getAllClients();
                        return new Message("success", clients.get());
                    } catch (SQLException | InterruptedException | ExecutionException e) {
                        return new Message("error", e.getMessage());
                    }

                });
        addClientHandler(ClientServiceInterface.FILTER_NAME,
                (request)->{
                    try {
                        CompletableFuture<Set<Client>> clients = clientService.filterClientsByName((String) request.getBody());
                        return new Message("success", clients.get());
                    } catch (SQLException | InterruptedException | ExecutionException e) {
                        return new Message("Server-side error while filtering clients.", e.getMessage());
                    }
                });


        //input: Long output:Optional<Client>
        addHandler(ClientServiceInterface.REMOVE_CLIENT,
                (request) -> {
                    try {
                        Long id = Long.parseLong(request.getBody());
                        System.out.println("REMOVE");
                        CompletableFuture<Optional<Client>> client = clientService.removeClient(id);
                        if(client.get().isEmpty())
                            return new Message<>("No client matched this ID.","");
                        return new Message("success",client.get());
                    } catch (SQLException |InterruptedException | ExecutionException e) {
                        return new Message("Server-side error while deleting client.", e.getMessage());
                    }
                });
        addHandler(ClientServiceInterface.FIND_ONE,
                (ID)->{
                    try {
                        CompletableFuture<Optional<Client>> client = clientService.findOneClient(Long.parseLong(ID.getBody()));
                        System.out.println("FIND ONE: "+client.get());
                        return new Message("success.",client.get());
                    } catch (SQLException |InterruptedException | ExecutionException e) {
                        return new Message("Server-side error while finding client.", e.getMessage());
                    }
                });

        //input:Client output:Optional<Client> DONE
        addClientHandler(ClientServiceInterface.ADD_CLIENT,
                (entity)->{
                    try {
                        System.out.println("Adding client..");
                        CompletableFuture<Optional<Client>> client = clientService.addClient((Client) entity.getBody());
                        return new Message<Optional<Client>>("success",client.get());
                    } catch (InterruptedException | ExecutionException e) {
                        return new Message("Server-side error while deleting client.", e.getMessage());
                    }
                });
        addClientHandler(ClientServiceInterface.UPDATE_CLIENT,
                (entity)->{
                    try {
                        CompletableFuture<Optional<Client>> client = clientService.updateClient((Client) entity.getBody());
                        return new Message<Optional<Client>>("success",client.get());
                    } catch (SQLException | InterruptedException | ExecutionException e) {
                        return new Message("Server-side error while updating client.", e.getMessage());
                    }
                });
    }

    private void initializeHandlersBooks(){
        addClientHandler(BookServiceInterface.GET_ALL_BOOKS,
                (request) -> {
                    try {
                        CompletableFuture<Set<Book>> books = bookService.getAllBooks();
                        return new Message("success", books.get());
                    } catch (SQLException | InterruptedException | ExecutionException e ){
                        return new Message("error", e.getMessage());
                    }


                });
        addClientHandler(BookServiceInterface.FILTER_BOOKS,
                (request)->{
                    try {
                        CompletableFuture<Set<Book>> books = bookService.filterBooksByTitle((String) request.getBody());
                        return new Message("success", books.get());
                    } catch (SQLException | InterruptedException | ExecutionException e) {
                        return new Message("Server-side error while filtering books.", e.getMessage());
                    }
                });


        addHandler(BookServiceInterface.REMOVE_BOOK,
                (request) -> {
                    try {
                        Long id = Long.parseLong(request.getBody());
                        CompletableFuture<Optional<Book>> book = bookService.removeBook(id);
                        if(book.get().isEmpty())
                            return new Message<>("No book matched this ID.","");
                        return new Message<>("Book removed successfully", "");
                    } catch (SQLException | InterruptedException | ExecutionException e) {
                        return new Message<>("Server-side error while deleting book.", e.getMessage());
                    }

                });
        addHandler(BookServiceInterface.FIND_ONE,
                (ID)->{
                    try {
                        CompletableFuture<Optional<Book>> book = bookService.findOneBook(Long.parseLong((String) ID.getBody()));
                        if(book.get().isEmpty())
                            return new Message<>("No book with this ID.","");
                        return new Message<>("succes.", book.get().get().toString());
                    } catch (SQLException | InterruptedException | ExecutionException e) {
                        return new Message<>("Server-side error while finding book.", e.getMessage());
                    }
                });

        addClientHandler(BookServiceInterface.ADD_BOOK,
                (entity)->{
                    try {
                        System.out.println("Adding book");
                        CompletableFuture<Optional<Book>> book = bookService.addBook((Book) entity.getBody());
                        return new Message<Optional<Book>>("success", book.get());
                    } catch (InterruptedException | ExecutionException | ParserConfigurationException | TransformerException | SAXException | IOException | SQLException e ){
                        return new Message("Server-side error while deleting book.", e.getMessage());
                    }

                });
        addClientHandler(BookServiceInterface.UPDATE_BOOK,
                (entity)->{
                    try {
                        CompletableFuture<Optional<Book>> book = bookService.updateBook((Book) entity.getBody());
                        return new Message<Optional<Book>>("success",book.get());
                    } catch (SQLException | InterruptedException | ExecutionException e) {
                        return new Message("Server-side error while updating book.", e.getMessage());
                    }
                });
    }

    private void initializeHandlersPurchases(){
        //output: Set<Purchase> DONE
        addHandler(PurchaseServiceInterface.GET_ALL_PURCHASES,
                (request) -> {
                    try {
                        CompletableFuture<Set<Purchase>> purchases = purchaseService.getAllPurchases();
                        return new Message("success", purchases.get());
                    } catch (SQLException | InterruptedException | ExecutionException e) {
                        return new Message("error", e.getMessage());
                    }

                });
        addHandler(PurchaseServiceInterface.FILTER,
                (request)->{
                    try {
                        CompletableFuture<Set<Purchase>> purchases = purchaseService.filterPurchasesByClientID(Long.parseLong((String) request.getBody()));
                        return new Message("success", purchases.get());
                    } catch (SQLException | InterruptedException | ExecutionException e) {
                        return new Message("Server-side error while filtering purchases.", e.getMessage());
                    }
                });

        //input: Long output: Optional<Purchase>
        addHandler(PurchaseServiceInterface.REMOVE_PURCHASE,
                (request) -> {
                    try {
                        Long id = Long.parseLong(request.getBody());
                        CompletableFuture<Optional<Purchase>> p = purchaseService.removePurchase(id);
                        if(p.get().isEmpty())
                            return new Message<>("No purchase matched this ID.","");
                        return new Message("success",p.get());
                    } catch (SQLException | InterruptedException | ExecutionException e) {
                        return new Message("Server-side error while deleting purchase.", e.getMessage());
                    }
                });

        addHandler(PurchaseServiceInterface.FIND_ONE,
                (request)->{
                    try {
                        CompletableFuture<Optional<Purchase>> purchase = purchaseService.findOnePurchase(Long.parseLong(request.getBody()));
                        return new Message("success",purchase.get());
                    } catch (SQLException | InterruptedException | ExecutionException e) {
                        return new Message("Server-side error while finding client.", e.getMessage());
                    }
                });

        //input: Purchase output:Optional<Purchase> DONE
        addClientHandler(PurchaseServiceInterface.ADD_PURCHASE,
                (entity)->{
                    try {
                        CompletableFuture<Optional<Purchase>> purchase = purchaseService.addPurchase((Purchase) entity.getBody());
                        return new Message<>("succes", purchase.get());
                    } catch (InterruptedException | ExecutionException e) {
                        return new Message("fail",e.getMessage());
                    }
                });
        addClientHandler(PurchaseServiceInterface.UPDATE_PURCHASE,
                (entity)->{
                    try {
                        CompletableFuture<Optional<Purchase>> purchase = purchaseService.updatePurchase((Purchase) entity.getBody());
                        return new Message("success", purchase.get());
                    } catch (SQLException | InterruptedException | ExecutionException e) {
                        return new Message("Server-side error while updating client.", e.getMessage());
                    }
                });


    }

    private void initializeHandlers(){
        initializeHandlersClient();
        initializeHandlersBooks();
        initializeHandlersPurchases();
    }

    public void addHandler(String methodName, UnaryOperator<Message<String>> handler){
        methodHandlers.put(methodName,handler);
    }
    public void addClientHandler(String methodName, Function<Message<?>,Message<?>> handler){
        clientHandlers.put(methodName,handler);
    }
    public void startServer(){
        try(var serverSocket = new ServerSocket(Message.PORT)){
            while(true){
                Socket client = serverSocket.accept();
                executorService.submit(new ClientHandler(client));
            }
        }catch(IOException e){
            throw new ServerException("Error connecting clients",e);
        }
    }

    private class ClientHandler implements Runnable{
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        private Message getResponse(Message request){
            if(methodHandlers.get(request.getHeader())!=null)
                return methodHandlers.get(request.getHeader()).apply(request);
            else
                if (clientHandlers.get(request.getHeader()) != null)
                    return clientHandlers.get(request.getHeader()).apply(request);

            return null;
        }

        @Override
        public void run(){
            try(var is = socket.getInputStream();  var os = socket.getOutputStream()){
                ObjectInputStream ois = new ObjectInputStream(is);
                Message request = (Message)ois.readObject();
                //request.readFrom(is);
                System.out.println("Received request: "+request);
                //request header = method name
                //request body = method arguments
                Message response = getResponse(request);
                System.out.println("CLIENT_HANDLER_SERVER: "+response);
                ObjectOutputStream oos = new ObjectOutputStream(os);
                oos.writeObject(response);
                //response.writeTo(os);
                socket.close();
            }catch(IOException | ClassNotFoundException e){
                throw new ServerException("Error processing client",e);
            }
        }
    }

}

