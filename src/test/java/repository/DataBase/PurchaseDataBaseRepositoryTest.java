package repository.DataBase;

import domain.Book;
import domain.Client;
import domain.Purchase;
import domain.validators.BookValidator;
import domain.validators.ClientValidator;
import domain.validators.PurchaseValidator;
import domain.validators.Validator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import service.BookService;
import service.ClientService;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;

public class PurchaseDataBaseRepositoryTest {

    private static final Long ID1 = 199L;
    private static final Long ID2 = 299L;
    private static final Long ID3 = 399L;
    private static final Long ID5 = 599L;

    private static final int NRBOOKS1 = 188;
    private static final int NRBOOKS2 = 210;
    private static final int NRBOOKS3 = 450;
    private static final int WRONG_NRBOOKS = 2000;

    private ClientDBRepository repoClient;
    private Validator<Client> validClient;
    private ClientService clientService;

    private BookDataBaseRepository repoBook;
    private Validator<Book> validBook;
    private BookService bookService;

    private PurchaseDataBaseRepository repoPurchase;
    private Validator<Purchase> validPurchase;

    private Book book1;
    private Book book2;
    private Client client1;
    private Client client2;
    private Purchase purchase1;
    private Purchase purchase2;
    private Purchase purchase3;
    private Purchase purchase_books;

    private HashSet purchases;

    private String url = "jdbc:postgresql://localhost:5432/bookstoretest?currentSchema=bookstore&user=postgres&password=password";



    @Before
    public void setUp() throws Exception {

        validClient = new ClientValidator();
        validBook = new BookValidator();

        purchases = new HashSet();
        validClient = new ClientValidator();
        validBook = new BookValidator();

        repoClient = new ClientDBRepository(validClient, url);
        repoBook = new BookDataBaseRepository(validBook, url);

        clientService = new ClientService(repoClient);
        bookService = new BookService(repoBook);


        client1 = new Client("123", "a b");
        client1.setId(ID1);

        client2 = new Client("143", "a b");
        client2.setId(ID2);

        clientService.addClient(client1);
        clientService.addClient(client2);

        book1 = new Book("123", "a", "a", 1888, 34.5, 100);
        book1.setId(ID1);
        book1.setInStock(NRBOOKS2);

        book2 = new Book("193", "a", "a", 1888, 34.5, 100);
        book2.setId(ID2);
        book2.setInStock(NRBOOKS3);

        bookService.addBook(book1);
        bookService.addBook(book2);

        validPurchase = new PurchaseValidator(clientService, bookService);
        repoPurchase = new PurchaseDataBaseRepository(validPurchase, repoBook, url);

        purchase1 = new Purchase(ID1, ID1, NRBOOKS2);
        purchase1.setId(ID1);

        purchase2 = new Purchase(ID1, ID2, NRBOOKS2);
        purchase2.setId(ID2);
        purchase3 = new Purchase(ID2, ID2, NRBOOKS1);
        purchase3.setId(ID3);
        purchase_books = new Purchase(ID2, ID1, WRONG_NRBOOKS);
        purchase_books.setId(ID5);


        repoPurchase.save(purchase1);
        repoPurchase.save(purchase2);


        purchases.add(purchase1);
        purchases.add(purchase2);
    }

    @After
    public void tearDown() throws Exception {
        // delete the new entries from the file,
        // otherwise it won't work the next time
        // you run the tests
        repoPurchase.delete(ID1);
        repoPurchase.delete(ID3);
        repoPurchase.delete(ID2);
        repoPurchase.delete(ID5);

        clientService.removeClient(ID1);
        clientService.removeClient(ID2);

        bookService.deleteBook(ID1);
        bookService.deleteBook(ID2);
    }

    @Test
    public void testFindAll() {
        assertEquals("There should be two books", purchases.size(), repoPurchase.findAll().spliterator().getExactSizeIfKnown());
    }




    @Test
    public void save() throws ParserConfigurationException, TransformerException, SAXException, IOException {
        assertEquals("Purchase should be saved", Optional.empty(), repoPurchase.save(purchase3));
        assertEquals("Purchase should not be saved", purchase3, repoPurchase.save(purchase3).get());

    }

    @Test
    public void delete() {
        assertEquals("Should delete purchase", purchase2, repoPurchase.delete(ID2).get());
        assertEquals("Should not find purchase", Optional.empty(), repoPurchase.delete(ID2));

    }

    @Test
    public void update() {
        assertEquals("Should update purchase", purchase1, repoPurchase.update(purchase1).get());
        assertEquals("Should not find purchase", Optional.empty(), repoPurchase.update(purchase3));

    }


}