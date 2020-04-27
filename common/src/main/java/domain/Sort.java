package domain;

import java.sql.*;
import java.util.*;

public class Sort {

    public enum Direction{
        ASC, DESC
    }

    private String URL = "jdbc:postgresql://localhost:5432/bookstore?currentSchema=bookstore&user=postgres&password=password";
    private String USER = System.getProperty("postgres");
    private String PASSWORD = System.getProperty("password");

    public static Direction dir = Direction.ASC;
    private List<String> info = new ArrayList<>();

    public Sort() {
    }

    public Sort(String ...a) {
        info = Arrays.asList(a.clone());
    }

    public Sort(Direction dir, String ...a) {
        Sort.dir = dir;
        info = Arrays.asList(a.clone());

    }

    public Iterable<Book> sortBook() throws SQLException {
        String s = "select * from Books order by ";
        Optional<String> ee = info.stream().reduce((s1, s2) -> s1 + " " + Sort.dir + ", " + s2 + " " + Sort.dir);
        s = s + ee.get() + ";";

        List<Book> result = new ArrayList<>();
        System.out.println(s);
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        PreparedStatement preparedStatement = connection.prepareStatement(s);
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
     public Iterable<Client> sortClient() throws SQLException {
         String cmd = "select * from Clients order by ";
         Optional<String> ee = info.stream().reduce((s1, s2) -> s1 + ", " + s2 + " " + dir);
         cmd = cmd + ee.get() + ";";
         List<Client> result = new ArrayList<>();

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

    public Iterable<Purchase> sortPurchase() throws SQLException {
        String cmd = "select * from Purchases order by ";
        Optional<String> ee = info.stream().reduce((s1, s2) -> s1 + ", " + s2 + " " + dir);
        cmd = cmd + ee.get() + ";";
        List<Purchase> result = new ArrayList<>();

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

    public Sort and(Sort sort){
        sort.info.stream().reduce((s1, s2) -> s1 + ", " + s2 + " " + dir);
        return sort;
    }

}

