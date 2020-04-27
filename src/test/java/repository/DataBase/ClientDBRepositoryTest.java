package repository.DataBase;

import domain.Client;
import domain.validators.ClientValidator;
import domain.validators.Validator;
import domain.validators.ValidatorException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.Assert.*;

public class ClientDBRepositoryTest {
    private static final Long ID1 = 19L;
    private static final Long ID2 = 29L;
    private static final Long ID3 = 39L;
    private static final String SERIAL_NUMBER1 = "sn01";
    private static final String SERIAL_NUMBER2 = "sn02";
    private static final String SERIAL_NUMBER3 = "sn03";
    private static final String SERIAL_NUMBER4 = "sn04";
    private static final String WRONG_SERIAL_NUMBER = "s n";
    private static final String NAME1 = "Client Name";
    private static final String NAME2 = "NewClient Name";
    private static final String NAME3 = "NewClient NewName";
    private static final String WRONG_NAME = "Name";

    private ClientDBRepository repository;
    private Validator<Client> validator;
    private Client client1;
    private Client client2;
    private Client client3;
    private Client wrongSerial;
    private Client wrongName;
    private Client updateClient;
    private HashSet allClients;

    private String url = "jdbc:postgresql://localhost:5432/bookstoretest?currentSchema=bookstore&user=postgres&password=password";


    @Before
    public void setUp() throws Exception {
        validator = new ClientValidator();
        repository = new ClientDBRepository(validator, url);
        client1 = new Client(SERIAL_NUMBER1,NAME1);
        client1.setId(ID1);
        client2 = new Client(SERIAL_NUMBER2,NAME2);
        client3 = new Client(SERIAL_NUMBER3,NAME3);
        client2.setId(ID2);
        client3.setId(ID3);

        wrongSerial = new Client(WRONG_SERIAL_NUMBER,NAME1);
        wrongSerial.setId(ID2);
        wrongName = new Client(SERIAL_NUMBER4,WRONG_NAME);
        wrongName.setId(ID1);

        updateClient = new Client(SERIAL_NUMBER1,NAME3);
        updateClient.setId(ID1);

        allClients = new HashSet<Client>();
        allClients.add(client1);
        allClients.add(client2);


        repository.save(client1);
        repository.save(client2);
    }

    @After
    public void tearDown() throws Exception {
        repository.delete(ID1);
        repository.delete(ID2);
        repository.delete(ID3);
<<<<<<< HEAD
=======
        validator = null;
        repository = null;
        client1 = null;
        client2 = null;
        client3 = null;
        allClients = null;
        wrongName = null;
        wrongSerial = null;
        updateClient = null;
>>>>>>> fe1b9fa06c4bf4d7862824c5efda32695f2cb8c7
    }

    @Test
    public void findAll() throws SQLException {
<<<<<<< HEAD
        assertEquals("There should be 5 clients", 5,((Collection<?>) repository.findAll()).size());
=======
        assertEquals("There should be 2 clients", allClients.size(),repository.findAll().spliterator().getExactSizeIfKnown());
>>>>>>> fe1b9fa06c4bf4d7862824c5efda32695f2cb8c7
    }

    @Test
    public void findOne() throws SQLException {
        assertEquals("It should find one client",client1,repository.findOne(ID1).get());
        assertEquals("It should find none", Optional.empty(),repository.findOne(5L));
    }

    @Test
    public void testFindAll() {//with sort
    }

    @Test
    public void save() throws SQLException, ParserConfigurationException, TransformerException, SAXException, IOException {
        assertEquals("It should save the client", Optional.empty(),repository.save(client3));
        assertEquals("It should not save the client",Optional.of(new Client()),repository.save(client3));
    }

    @Test
    public void delete() throws SQLException {
        assertEquals("Should delete it",Optional.of(new Client()),repository.delete(ID1));
        assertEquals("Should not find it",Optional.empty(),repository.delete(ID1));

    }

    @Test
    public void update() throws SQLException {
        assertEquals("Should update it",Optional.of(new Client()),repository.update(updateClient));
        assertEquals("Should not find it",Optional.empty(),repository.update(client3));
    }

//    @Test(expected = ValidatorException.class)
//    public void testUpdateException() throws Exception {
//        repository.update(wrongSerial);
//        repository.update(wrongName);
//    }
//
//    @Test(expected = ValidatorException.class)
//    public void testSaveException() throws Exception {
//        repository.save(wrongName);
//        repository.save(wrongSerial);
//    }
}