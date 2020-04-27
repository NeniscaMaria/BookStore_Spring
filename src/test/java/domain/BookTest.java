package domain;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;


public class BookTest {
    private Book book;

    private static final Long ID = 1L;
    private static final Long NEW_ID = 2L;
    private static final String SERIAL_NUMBER = "serial1";
    private static final String NEW_SERIAL_NUMBER = "serial2";
    private static final String NAME = "book1";
    private static final String NEW_NAME = "new_book";
    private static final String AUTHOR = "author1";
    private static final String NEW_AUTHOR = "new_author";
    private static final double PRICE = 20.5;
    private static final double NEW_PRICE = 30.2;
    private static final int YEAR = 2013;
    private static final int NEW_YEAR = 2018;
    private static final int STOCK = 20;
    private static final int NEW_STOCK = 18;

    @Before
    public void setUp() throws Exception {
        book = new Book(SERIAL_NUMBER, NAME, AUTHOR, YEAR, PRICE, STOCK);
        book.setId(ID);
    }

    @After
    public void tearDown() throws Exception {
        book = null;
    }

    @Test
    public void testGetSerialNumber() throws Exception {
        assertEquals("Serial numbers should be equal", SERIAL_NUMBER, book.getSerialNumber());
    }

    @Test
    public void testSetSerialNumber() throws Exception {
        book.setSerialNumber(NEW_SERIAL_NUMBER);
        assertEquals("Serial numbers should be equal", NEW_SERIAL_NUMBER, book.getSerialNumber());
    }

    @Test
    public void testGetId() throws Exception {
        assertEquals("Ids should be equal", ID, book.getId());
    }

    @Test
    public void testSetId() throws Exception {
        book.setId(NEW_ID);
        assertEquals("Ids should be equal", NEW_ID, book.getId());
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals("Names should be be the same", NAME, book.getTitle());
    }

    @Test
    public void testSetName() throws Exception {
        book.setTitle(NEW_NAME);
        assertEquals("Names should be the same", NEW_NAME, book.getTitle());
    }

    @Test
    public void testGetAuthor() throws Exception {
        assertEquals("Authors should be the same", AUTHOR, book.getAuthor());
    }

    @Test
    public void testSetAuthor() throws Exception {
        book.setAuthor(NEW_AUTHOR);
        assertEquals("Authors should be the same", NEW_AUTHOR, book.getAuthor());
    }

    @Test
    public void testGetYear() throws Exception {
        assertEquals("Years should be equal", YEAR, book.getYear());
    }

    @Test
    public void testSetYear() throws Exception {
        book.setYear(NEW_YEAR);
        assertEquals("Years should be equal", NEW_YEAR, book.getYear());
    }

    @Test
    public void testGetPrice() throws Exception {
        assertEquals("Prices should be equal", String.valueOf(PRICE), String.valueOf(book.getPrice()));
    }

    @Test
    public void testSetPrice() throws Exception {
        book.setPrice(NEW_PRICE);
        assertEquals("Prices should be equal", String.valueOf(NEW_PRICE), String.valueOf(book.getPrice()));
    }

    @Test
    public void testSetStock() throws Exception{
        book.setInStock(NEW_STOCK);
        assertEquals("Stocks should be equal", NEW_STOCK, book.getInStock());
    }

    @Test
    public void testGetStock() throws Exception{
        assertEquals("Stocks should be equal", String.valueOf(STOCK), String.valueOf(book.getInStock()));
    }
}
