package repository;

import domain.Client;
import domain.validators.ClientValidator;
import domain.validators.Validator;
import domain.validators.ValidatorException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.Assert.assertEquals;


public class ClientXMLRepositoryTest {
    private static final Long ID1 = 1L;
    private static final Long ID2 = 2L;
    private static final Long ID3 = 3L;
    private static final String SERIAL_NUMBER1 = "sn01";
    private static final String SERIAL_NUMBER2 = "sn02";
    private static final String SERIAL_NUMBER3 = "sn03";
    private static final String SERIAL_NUMBER4 = "sn04";
    private static final String WRONG_SERIAL_NUMBER = "s n";
    private static final String NAME1 = "Client Name";
    private static final String NAME2 = "NewClient Name";
    private static final String NAME3 = "NewClient NewName";
    private static final String WRONG_NAME = "Name";

    private ClientXMLRepository repository;
    private Validator<Client> validator;
    private Client client1;
    private Client client2;
    private Client client3;
    private Client wrongSerial;
    private Client wrongName;
    private Client updateClient;

    private HashSet allClients;

    private String filename;
    @Before
    public void setUp() throws Exception {
        filename="testClient.xml";
        validator = new ClientValidator();
        repository = new ClientXMLRepository(validator,filename);
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

        updateClient =new Client(SERIAL_NUMBER1,NAME3);
        updateClient.setId(ID1);

        allClients = new HashSet<Client>();
        allClients.add(client1);
        allClients.add(client2);


        repository.save(client1);
        repository.save(client2);
    }

    @After
    public void tearDown() throws Exception {
        // delete the new entries from the file,
        // otherwise it won't work the next time
        // you run the tests
        repository.delete(ID2);
        repository.delete(ID3);

        filename=null;
        validator = null;
        repository = null;
        client1 = null;
        client2 = null;
        client3 = null;
        allClients = null;
        wrongName = null;
        wrongSerial = null;
        updateClient = null;
    }

    @Test
    public void testSave1() {
        assertEquals("It should save the client", Optional.empty(),repository.save(client3));
        assertEquals("It should not save the client",client3,repository.save(client3).get());
    }

    @Test
    public void testUpdate1() {
        assertEquals("Should update it",updateClient,repository.update(updateClient).get());
    }

    @Test
    public void testDelete1() {
        assertEquals("Should delete it",ID1,repository.delete(ID1).get().getId());
        assertEquals("Should not find it",Optional.empty(),repository.delete(ID1));
    }
}