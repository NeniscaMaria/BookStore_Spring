package domain;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PurchaseTest {
    private Book book;
    private Client client;
    private Purchase purchase;

    private static final Long ID = 1L;
    private static final Long NEW_ID = 2L;
    private static final Long ID_CLIENT = 1L;
    private static final Long ID_BOOK1 = 2L;
    private static final int NR_BOOKS = 20;
    private static final int NEW_NR_BOOKS = 18;

    @Before
    public void setUp() throws Exception {
        client = new Client();
        client.setId(ID_CLIENT);
        book = new Book();
        book.setId(ID_BOOK1);
        purchase = new Purchase(ID_CLIENT, ID_BOOK1, NR_BOOKS);
        purchase.setId(ID);
    }

    @After
    public void tearDown() throws Exception {
        book = null;
        client = null;
        purchase = null;
    }

    @Test
    public void getClientID() {
        assertEquals("Ids should be equal", ID_CLIENT, purchase.getClientID());

    }

    @Test
    public void getBookID() {
        assertEquals("Ids should be equal", ID_BOOK1, purchase.getBookID());

    }

    @Test
    public void getNrBooks() {
        assertEquals("Ids should be equal", NR_BOOKS, purchase.getNrBooks());
    }

    @Test
    public void setClientID() {
        purchase.setClientID(NEW_ID);
        assertEquals("Ids should be equal for client", NEW_ID, purchase.getClientID());
    }

    @Test
    public void setBookID() {
        purchase.setBookID(NEW_ID);
        assertEquals("Ids should be equal for book", NEW_ID, purchase.getBookID());

    }

    @Test
    public void setNrBooks() {
        purchase.setNrBooks(NEW_NR_BOOKS);
        assertEquals("Number of books should be equal", NEW_NR_BOOKS, purchase.getNrBooks());

    }
}