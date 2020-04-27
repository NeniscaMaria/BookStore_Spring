package SDI.server.repository.DataBase;

import domain.Sort;
import SDI.server.repository.SortingRepository;
import SDI.server.validators.Validator;
import domain.ValidatorException;
import domain.Client;
import org.postgresql.util.PSQLException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class ClientDBRepository implements SortingRepository<Long, Client> {


    private static String URL = "jdbc:postgresql://localhost:5432/bookstore?currentSchema=bookstore&user=postgres&password=password";
    private static final String USER = System.getProperty("postgres");
    private static final String PASSWORD = System.getProperty("password");
    private Validator<Client> validator;

    public ClientDBRepository(Validator<Client> validator) {
        this.validator=validator;
    }
    public ClientDBRepository(Validator<Client> validator, String URL) {
        this.validator=validator;
        this.URL = URL;
    }

    @Override
    public Iterable<Client> findAll(Sort sort) throws SQLException {
        return sort.sortClient();
    }

    @Override
    public Optional<Client> findOne(Long id) throws SQLException {
        String cmd = "select * from Clients where id = ?";
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        PreparedStatement preparedStatement = connection.prepareStatement(cmd);
        preparedStatement.setLong(1,id);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            String name = resultSet.getString("name");
            String serialNumber = resultSet.getString("serialNumber");
            Client client = new Client(serialNumber, name);
            client.setId(id);
            connection.close();
            return Optional.of(client);
        }
        connection.close();
        return Optional.empty();
    }

    @Override
    public Iterable<Client> findAll() throws SQLException {
        List<Client> result = new ArrayList<>();
        String cmd = "select * from Clients";

        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        PreparedStatement preparedStatement = connection.prepareStatement(cmd);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Long id = resultSet.getLong("id");
            String serialNumber = resultSet.getString("serialNumber");
            String name = resultSet.getString("name");

            Client client = new Client(serialNumber, name);
            client.setId(id);
            result.add(client);
        }
        connection.close();
        return result;
    }

    @Override
    public Optional<Client> save(Client entity) throws ValidatorException, SQLException {
        String cmd = "insert into Clients(id,serialNumber,name) values(?, ?, ?)";
        validator.validate(entity);
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        PreparedStatement preparedStatement = connection.prepareStatement(cmd);
        preparedStatement.setLong(1, entity.getId());
        preparedStatement.setString(2, entity.getSerialNumber());
        preparedStatement.setString(3, entity.getName());
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
    public Optional<Client> delete(Long id) throws SQLException {
        String sql = "delete from Clients where id=?";

        Optional<Client> c = findOne(id);
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setLong(1, id);
        if (preparedStatement.executeUpdate() == 0) {
            connection.close();

            return Optional.empty();
        } else {
            connection.close();
            return c;
        }
    }

    @Override
    public Optional<Client> update(Client entity) throws ValidatorException, SQLException {
        validator.validate(entity);
        String sql = "update Clients set name=?, serialnumber=? where id=?";

        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, entity.getName());
        preparedStatement.setString(2, entity.getSerialNumber());
        preparedStatement.setLong(3, entity.getId());
        if(preparedStatement.executeUpdate()==0) {
            connection.close();

            return Optional.empty();
        }
        else {
            connection.close();

            return Optional.of(entity);
        }


    }

    @Override
    public void removeEntitiesWithClientID(Long id) throws ParserConfigurationException, IOException, SAXException {

    }
}
