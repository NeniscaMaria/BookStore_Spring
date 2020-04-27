package SDI.server.repository.DataBase;

import domain.Sort;
import SDI.server.repository.SortingRepository;
import SDI.server.validators.Validator;
import domain.ValidatorException;
import domain.Book;
import org.postgresql.util.PSQLException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class BookDataBaseRepository implements SortingRepository<Long, Book> {

//    private static final String URL = "jdbc:postgresql://localhost:5432/bookstore";
    private String URL = "jdbc:postgresql://localhost:5432/bookstore?currentSchema=bookstore&user=postgres&password=password";
    private String USER = System.getProperty("postgres");
//    private static final String URL = "jdbc:postgresql://localhost:5432/bookstore";

    private String PASSWORD = System.getProperty("password");
    private Validator<Book> validator;

    public BookDataBaseRepository(Validator<Book> validator) {
        this.validator=validator;
    }

    public BookDataBaseRepository(Validator<Book> validator, String URL) {
        this.validator=validator;
        this.URL = URL;
    }

    @Override
    public Iterable<Book> findAll(Sort sort) throws SQLException {

        return sort.sortBook();

    }

    @Override
    public Optional<Book> findOne(Long id) throws SQLException {
        String cmd = "select * from Books where id = ?";
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

        PreparedStatement preparedStatement = connection.prepareStatement(cmd);
        preparedStatement.setLong(1, id);

        ResultSet resultSet = preparedStatement.executeQuery();
        if(resultSet.next()) {
            String serialNumber = resultSet.getString("serialNumber");
            String title = resultSet.getString("title");
            String author = resultSet.getString("author");
            int year = resultSet.getInt("year");
            double price = resultSet.getDouble("price");
            int stock = resultSet.getInt("inStock");

            Book b = new Book(serialNumber, title, author, year, price, stock);
            b.setId(id);
            connection.close();
            return Optional.of(b);
        }
        connection.close();
        return Optional.empty();
    }

    @Override
    public Iterable<Book> findAll() throws SQLException {
        List<Book> result = new ArrayList<>();
        String cmd = "select * from Books";

        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        PreparedStatement preparedStatement = connection.prepareStatement(cmd);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Long id = resultSet.getLong("id");
            String serialNumber = resultSet.getString("serialNumber");
            String title = resultSet.getString("title");
            String author = resultSet.getString("author");
            int year = resultSet.getInt("year");
            double price = resultSet.getDouble("price");
            int stock = resultSet.getInt("inStock");

            Book b = new Book(serialNumber, title, author, year, price, stock);
            b.setId(id);
            result.add(b);

            connection.close();
        }



        return result;

    }

    @Override
    public Optional<Book> save(Book entity) throws ValidatorException, ParserConfigurationException, IOException, SAXException, TransformerException, SQLException {
        String cmd = "insert into Books(id, serialNumber, title, author, year, price, inStock)" +
                "values(?, ?, ?, ?, ?, ?, ?)";

        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        PreparedStatement preparedStatement = connection.prepareStatement(cmd);
        preparedStatement.setLong(1, entity.getId());
        preparedStatement.setString(2, entity.getSerialNumber());
        preparedStatement.setString(3, entity.getTitle());
        preparedStatement.setString(4, entity.getAuthor());
        preparedStatement.setInt(5, entity.getYear());
        preparedStatement.setDouble(6, entity.getPrice());
        preparedStatement.setInt(7, entity.getInStock());
        try{
            preparedStatement.executeUpdate();
        }
        catch (PSQLException e){
            connection.close();
            return Optional.of(entity);
        }

        connection.close();


    return Optional.empty();
    }


    @Override
    public Optional<Book> delete(Long id) throws SQLException {
        String sql = "delete from Books where id=?";


        Optional<Book> b = findOne(id);
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setLong(1, id);
        preparedStatement.executeUpdate();

        connection.close();
        return b;
    }

    @Override
    public Optional<Book> update(Book entity) throws ValidatorException, SQLException {

        String cmd = "update Books " +
                "set serialNumber = ?, " +
                "title = ?, " +
                "author = ?, " +
                "year = ?, " +
                "price = ?, " +
                "inStock = ? " +
                "where id = ?;";


        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        PreparedStatement preparedStatement = connection.prepareStatement(cmd);
        preparedStatement.setString(1, entity.getSerialNumber());
        preparedStatement.setString(2, entity.getTitle());
        preparedStatement.setString(3, entity.getAuthor());
        preparedStatement.setInt(4, entity.getYear());
        preparedStatement.setDouble(5, entity.getPrice());
        preparedStatement.setInt(6, entity.getInStock());
        preparedStatement.setLong(7, entity.getId());
        int rows = preparedStatement.executeUpdate();
        if (rows > 0){
            return Optional.of(entity);
        }
        connection.close();

        return Optional.empty();
    }



    @Override
    public void removeEntitiesWithClientID(Long id) throws ParserConfigurationException, IOException, SAXException {

    }


}
