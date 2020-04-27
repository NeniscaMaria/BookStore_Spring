import Service.BookService;
import Service.ClientService;
import Service.PurchaseService;
import TCP.TCPClient;
import domain.Message;
import ui.Console;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        TCPClient tcpClient= new TCPClient();
        ClientService clientService = new ClientService(executorService,tcpClient);
        PurchaseService purchaseService = new PurchaseService(executorService,tcpClient);
        BookService bookService = new BookService(executorService, tcpClient);
        Console console = new Console(clientService, bookService, purchaseService);
        console.runConsole();

        executorService.shutdown();

    }
}
