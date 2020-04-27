import SDI.server.TCPServer;
import SDI.server.repository.DataBase.BookDataBaseRepository;
import SDI.server.repository.DataBase.ClientDBRepository;
import SDI.server.repository.DataBase.PurchaseDataBaseRepository;
import SDI.server.repository.Repository;
import SDI.server.service.BookService;
import SDI.server.service.ClientService;
import SDI.server.service.PurchaseService;
import SDI.server.validators.BookValidator;
import SDI.server.validators.ClientValidator;
import SDI.server.validators.PurchaseValidator;
import SDI.server.validators.Validator;
import domain.Book;
import domain.Client;
import domain.Purchase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        try {
            System.out.println("Starting server...");
            ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            Validator<Client> clientValidator = new ClientValidator();
            Repository<Long, Client> clientRepository = new ClientDBRepository(clientValidator);
            ClientService clientService = new ClientService(clientRepository, executorService);

            Validator<Book> bookValidator = new BookValidator();
            Repository<Long, Book> bookRepository = new BookDataBaseRepository(bookValidator);
            BookService bookService = new BookService(bookRepository,executorService);

            Validator<Purchase> purchaseValidator = new PurchaseValidator(clientService,bookService);
            Repository<Long, Purchase> purchaseRepository = new PurchaseDataBaseRepository(purchaseValidator, bookRepository);
            PurchaseService purchaseService = new PurchaseService(purchaseRepository,executorService);

            TCPServer server = new TCPServer(executorService,clientService,bookService,purchaseService);
            System.out.println("Server started.");
            server.startServer();
            executorService.shutdown();
        }catch(RuntimeException e){
            System.out.println(e);
        }
    }
}
