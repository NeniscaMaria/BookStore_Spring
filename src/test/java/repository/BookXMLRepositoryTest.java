package repository;

import domain.Book;
import domain.validators.BookValidator;
import domain.validators.Validator;
import domain.validators.ValidatorException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Optional;

public class BookXMLRepositoryTest {
    private static final Long ID1 = 1L;
    private static final Long ID2 = 2L;
    private static final Long ID3 = 3L;
    private static final Long ID4 = 4L;
    private static final Long ID5 = 5L;
    private static final Long ID6 = 6L;
    private static final Long ID7 = 7L;
    private static final Long ID8 = 8L;
    private static final Long ID9 = 9L;
    private static final Long ID10 = 10L;
    private static final String SN1 = "123";
    private static final String SN2 = "213";
    private static final String SN3 = "231";
    private static final String WRONG_SN = "0";
    private static final String NAME1 = "Book1";
    private static final String NAME2 = "Book2";
    private static final String NAME3 = "Book3";
    private static final String WRONG_NAME = " ";
    private static final String AUTHOR1 = "author a";
    private static final String AUTHOR2 = "author b";
    private static final String AUTHOR3 = "author c";
    private static final String WRONG_AUTHOR = "author2";
    private static final int YEAR1 = 1788;
    private static final int YEAR2 = 2010;
    private static final int YEAR3 = 450;
    private static final int WRONG_YEAR = 2025;
    private static final int STOCK1 = 1788;
    private static final int STOCK2 = 2010;
    private static final int STOCK3 = 450;
    private static final int WRONG_STOCK = 2025;
    private static final double PRICE1 = 10.2;
    private static final double PRICE2 = 30.2;
    private static final double PRICE3 = 40.2;
    private static final double WRONG_PRICE = -40.2;
    private String filepath = "testBooks.xml";

    private BookXMLRepository repo;
    private Validator<Book> valid;
    private HashSet books;
    private Book book1;
    private Book book2;
    private Book book3;
    private Book book_serial;
    private Book book_name;
    private Book book_author;
    private Book book_year;
    private Book book_price;
    private Book book_update;

    @Before
    public void setUp() throws Exception{
        valid = new BookValidator();
        repo = new BookXMLRepository(valid, filepath);
        books = new HashSet();

        book1 = new Book(SN1, NAME1, AUTHOR1, YEAR1, PRICE1, STOCK1);
        book1.setId(ID1);
        book2 = new Book(SN2, NAME2, AUTHOR2, YEAR2, PRICE2, STOCK2);
        book2.setId(ID2);
        book3 = new Book(SN3, NAME3, AUTHOR3, YEAR3, PRICE3, STOCK3);
        book3.setId(ID3);

        book_serial = new Book(WRONG_SN, NAME1, AUTHOR1, YEAR1, PRICE1, STOCK1);
        book_serial.setId(ID4);
        book_name = new Book(SN2, WRONG_NAME, AUTHOR2, YEAR2, PRICE2, STOCK2);
        book_name.setId(ID5);
        book_author = new Book(SN3, NAME3, WRONG_AUTHOR, YEAR3, PRICE3, STOCK3);
        book_author.setId(ID6);
        book_year = new Book(SN3, NAME3, AUTHOR2, WRONG_YEAR, PRICE1, STOCK2);
        book_year.setId(ID7);
        book_year = new Book(SN3, NAME3, AUTHOR2, WRONG_YEAR,PRICE1, STOCK3);
        book_year.setId(ID7);
        book_price = new Book(SN1, NAME1, AUTHOR2, YEAR3, WRONG_PRICE, STOCK1);
        book_price.setId(ID8);
        book_price = new Book(SN1, NAME1, AUTHOR2, YEAR3, WRONG_PRICE, WRONG_STOCK);
        book_price.setId(ID9);


        book_update = new Book(SN3, NAME3, AUTHOR1, YEAR2, PRICE1, WRONG_STOCK);
        book_update.setId(ID10);

        repo.save(book1);
        repo.save(book2);

        books.add(book1);
        books.add(book2);
    }


    @After
    public void tearDown() throws Exception{
        repo.delete(ID1);
        repo.delete(ID2);
        repo.delete(ID3);

        repo = null;
        valid = null;
        book1 = null;
        book2 = null;
        book3 = null;
        book_serial = null;
        book_name = null;
        book_author = null;
        book_year = null;
        book_update = null;
        book_price = null;

    }

    @Test
    public void testFindOne() throws Exception {
        assertEquals("It should find one book", book1, repo.findOne(ID1).get());
        assertEquals("It should find no book", Optional.empty(), repo.findOne(15L));
    }

    @Test
    public void testSave() {
        assertEquals("Book should be saved", Optional.empty(), repo.save(book3));
        assertEquals("Book should not be saved", book3, repo.save(book3).get());
    }

    @Test
    public void testFindAll() {
        assertEquals("There should be two books", books, repo.findAll());
    }

    @Test
    public void testDelete() throws Exception {
        assertEquals("Should delete book", book2, repo.delete(ID2).get());
        assertEquals("Should not find book", Optional.empty(), repo.delete(ID2));
    }


    @Test
    public void testUpdate() throws Exception {
        assertEquals("Should update book", book1, repo.update(book1).get());
        assertEquals("Should not find book", Optional.empty(), repo.update(book_update));
    }

    @Test(expected = ValidatorException.class)
    public void testSaveException() throws Exception {
        repo.save(book_serial);
        repo.save(book_name);
        repo.save(book_author);
        repo.save(book_year);
    }

    @Test(expected = ValidatorException.class)
    public void testUpdateException() throws Exception {
        repo.update(book_serial);
        repo.update(book_name);
        repo.update(book_author);
        repo.update(book_year);
    }
}