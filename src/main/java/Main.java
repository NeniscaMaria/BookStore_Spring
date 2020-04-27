import domain .Book;
import domain.Client;
import domain.Purchase;
import domain.validators.*;
import org.xml.sax.SAXException;
import repository.*;
import repository.DataBase.BookDataBaseRepository;
import repository.DataBase.ClientDBRepository;
import repository.DataBase.PurchaseDataBaseRepository;
import service.BookService;
import service.ClientService;
import service.PurchaseService;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    private static void runInMemory(){
        //DESCR: runs the program and saves everything in the memory
        Validator<Client> clientValidator = new ClientValidator();
        Repository<Long, Client> clientRepository = new InMemoryRepository<>(clientValidator);
        ClientService clientService = new ClientService(clientRepository);

        Validator<Book> bookValidator = new BookValidator();
        Repository<Long, Book> bookRepository = new InMemoryRepository<>(bookValidator);
        BookService bookService = new BookService(bookRepository);

        Validator<Purchase> purchaseValidator = new PurchaseValidator(clientService,bookService);
        Repository<Long, Purchase> purchaseRepository = new InMemoryRepository<>(purchaseValidator);
        PurchaseService purchaseService = new PurchaseService(purchaseRepository);

        ui.Console console = new ui.Console(clientService, bookService, purchaseService);
        console.runConsole();
    }

    private static void runInFiles(){
        //DESCR: runs program and saves the clients and the books in files
        Validator<Client> clientValidator = new ClientValidator();
        Repository<Long, Client> clientRepository = new ClientFileRepository(clientValidator, "clients.txt");
        ClientService clientService = new ClientService(clientRepository);

        Validator<Book> bookValidator = new BookValidator();
        Repository<Long, Book> bookRepository = new BookFileRepository(bookValidator, "books.txt");
        BookService bookService = new BookService(bookRepository);

        Validator<Purchase> purchaseValidator = new PurchaseValidator(clientService,bookService);
        Repository<Long, Purchase> purchaseRepository = new PurchaseFileRepository(purchaseValidator, "purchase.txt");
        PurchaseService purchaseService = new PurchaseService(purchaseRepository);

        ui.Console console = new ui.Console(clientService, bookService, purchaseService);
        console.runConsole();
    }

    private static void runWithXML() throws ParserConfigurationException, SAXException, IOException {
        Validator<Client> clientValidator = new ClientValidator();
        Repository<Long, Client> clientRepository = new ClientXMLRepository(clientValidator, "src/clients.xml");
        ClientService clientService = new ClientService(clientRepository);

        Validator<Book> bookValidator = new BookValidator();
        Repository<Long, Book> bookRepository = new BookXMLRepository(bookValidator, "books.xml");
        BookService bookService = new BookService(bookRepository);

        Validator<Purchase> purchaseValidator = new PurchaseValidator(clientService,bookService);
        Repository<Long, Purchase> purchaseRepository = new PurchaseXMLRepository(purchaseValidator,"purchases.xml");
        PurchaseService purchaseService = new PurchaseService(purchaseRepository);

        ui.Console console = new ui.Console(clientService, bookService, purchaseService);
        console.runConsole();
    }
    private static void runJDBC(){
        Validator<Client> clientValidator = new ClientValidator();
        Repository<Long, Client> clientRepository = new ClientDBRepository(clientValidator);
        ClientService clientService = new ClientService(clientRepository);

        Validator<Book> bookValidator = new BookValidator();
        Repository<Long, Book> bookRepository = new BookDataBaseRepository(bookValidator);
        BookService bookService = new BookService(bookRepository);

        Validator<Purchase> purchaseValidator = new PurchaseValidator(clientService,bookService);
        Repository<Long, Purchase> purchaseRepository = new PurchaseDataBaseRepository(purchaseValidator, bookRepository);
        PurchaseService purchaseService = new PurchaseService(purchaseRepository);

        ui.Console console = new ui.Console(clientService, bookService, purchaseService);
        console.runConsole();
    }


    public static void main(String args[]) {
        System.out.println("Choose storage option:");
        System.out.println("1.In memory");
        System.out.println("2.In .txt files");
        System.out.println("3.In XML files");
        System.out.println("4.With JDBC");

        boolean finished = false;
        while(!finished) {
            try {
                Scanner keyboard = new Scanner(System.in);
                System.out.println("Input choice: ");
                int choice = keyboard.nextInt();
                switch (choice) {
                    case 1:
                        runInMemory();
                        finished = true;
                        break;
                    case 2:
                        runInFiles();
                        finished = true;
                        break;
                    case 3:
                        runWithXML();
                        finished=true;
                        break;
                    case 4:
                        runJDBC();
                        finished=true;
                        break;
                    default:
                        throw new ValidatorException("Please input a valid choice.");
                }
            } catch (ValidatorException | ParserConfigurationException | SAXException | IOException ve) {
                System.out.println(ve.getMessage());
            }catch(InputMismatchException e ){
                System.out.println("Please input a number.");
            }
        }
    }
}
