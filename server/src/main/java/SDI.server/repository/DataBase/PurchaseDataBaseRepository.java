package SDI.server.repository.DataBase;

import domain.Sort;
import SDI.server.repository.Repository;
import SDI.server.repository.SortingRepository;
import SDI.server.validators.Validator;
import domain.ValidatorException;
import domain.Book;
import domain.Purchase;
import org.postgresql.util.PSQLException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PurchaseDataBaseRepository implements SortingRepository<Long, Purchase> {

    private String URL = "jdbc:postgresql://localhost:5432/bookstore?currentSchema=bookstore&user=postgres&password=password";
    private String USER = System.getProperty("postgres");
    private String PASSWORD = System.getProperty("password");
    private Validator<Purchase> validator;
    private Repository<Long, Book> books;

    public PurchaseDataBaseRepository(Validator<Purchase> validator, Repository<Long, Book> books) {
        this.validator = validator;
        this.books = books;
    }

    public PurchaseDataBaseRepository(Validator<Purchase> validator, Repository<Long, Book> books, String URL) {
        this.validator = validator;
        this.books = books;
        this.URL = URL;
    }


    @Override
    public Iterable<Purchase> findAll(Sort sort) throws SQLException {
        return sort.sortPurchase();
    }

    @Override
    public Optional<Purchase> findOne(Long id) throws SQLException{
        String cmd = "select * from Purchases where id = ?";

        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        PreparedStatement preparedStatement = connection.prepareStatement(cmd);
        preparedStatement.setLong(1, id);

        ResultSet resultSet = preparedStatement.executeQuery();
        if(resultSet.next()) {
            Long clientID = resultSet.getLong("clientID");
            Long bookID = resultSet.getLong("bookID");
            int nrBooks = resultSet.getInt("nrBooks");

            Purchase p = new Purchase(clientID, bookID, nrBooks);
            p.setId(id);
            connection.close();
            return Optional.of(p);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<Purchase> findAll() throws SQLException {
        List<Purchase> result = new ArrayList<>();
        String cmd = "select * from Purchases";

        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        PreparedStatement preparedStatement = connection.prepareStatement(cmd);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Long id = resultSet.getLong("id");
            Long clientID = resultSet.getLong("clientID");
            Long bookID = resultSet.getLong("bookID");
            int nrBooks = resultSet.getInt("nrBooks");

            Purchase p = new Purchase(clientID, bookID, nrBooks);
            p.setId(id);
            result.add(p);

        }
        connection.close();

        return result;

    }

    @Override
    public Optional<Purchase> save(Purchase entity) throws ValidatorException, ParserConfigurationException, IOException, SAXException, TransformerException, SQLException {
        String cmd = "insert into Purchases(id, clientID, bookID, nrBooks)" +
                "values(?, ?, ?, ?)";

        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        PreparedStatement preparedStatement = connection.prepareStatement(cmd);
        preparedStatement.setLong(1, entity.getId());
        preparedStatement.setLong(2, entity.getClientID());
        preparedStatement.setLong(3, entity.getBookID());
        preparedStatement.setInt(4, entity.getNrBooks());

        try{
            preparedStatement.executeUpdate();
            Optional<Book> book = books.findOne(entity.getBookID());
            book.ifPresent(b->{
                b.setInStock(b.getInStock() - entity.getNrBooks());
                try {
                    books.update(b);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
        catch (PSQLException e){
            connection.close();
            return Optional.of(entity);
        }

        return Optional.empty();
    }


    @Override
    public Optional<Purchase> delete(Long id) throws SQLException {
        String sql = "delete from Purchases where id=?";


        Optional<Purchase> p = findOne(id);
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setLong(1, id);
        int rows = preparedStatement.executeUpdate();
        if (rows > 0){

            Optional<Book> book = books.findOne(p.get().getBookID());
            book.ifPresent(b->{
                b.setInStock(b.getInStock() + p.get().getNrBooks());

                try {
                    books.update(b);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            });
            connection.close();
            return p;
        }

        return Optional.empty();
    }

    @Override
    public Optional<Purchase> update(Purchase entity) throws ValidatorException, SQLException {

        String cmd = "update Purchases " +
                "set clientID = ?, " +
                "bookID = ?, " +
                "nrBooks = ? " +
                "where id = ?;";

        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

        Optional<Purchase> pur = findOne(entity.getId());

        pur.ifPresent(p->{
            int oldBooks = p.getNrBooks();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(cmd);
                preparedStatement.setLong(1, entity.getClientID());
                preparedStatement.setLong(2, entity.getBookID());
                preparedStatement.setInt(3, entity.getNrBooks());
                preparedStatement.setLong(4, entity.getId());
                preparedStatement.executeUpdate();

                Book book = books.findOne(entity.getBookID()).get();
                book.setInStock(book.getInStock() - (entity.getNrBooks() - oldBooks));

                books.update(book);

            } catch (SQLException e) {
                e.printStackTrace();
            }

        });
        connection.close();
        return pur;

    }

    @Override
    public void removeEntitiesWithClientID(Long clientID) throws ParserConfigurationException, IOException, SAXException {

        try {
            findAll().forEach(p-> {
                if (p.getClientID().equals(clientID)) {
                    try {
                        delete(p.getId());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
