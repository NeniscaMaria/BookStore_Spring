package domain;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class ClientTest {
    private static final Long ID = 1L;
    private static final Long NEW_ID = 2L;
    private static final String SERIAL_NUMBER = "sn01";
    private static final String NEW_SERIAL_NUMBER = "sn02";
    private static final String NAME = "Client Name";
    private static final String NEW_NAME = "New Name";

    private domain.Client  client;

    @Before
    public void setUp() throws Exception {
        client = new domain.Client(SERIAL_NUMBER, NAME);
        client.setId(ID);
    }

    @After
    public void tearDown() throws Exception {
        client=null;
    }

    @Test
    public void testGetSerialNumber() throws Exception {
        assertEquals("Serial numbers should be equal", SERIAL_NUMBER, client.getSerialNumber());
    }

    @Test
    public void testSetSerialNumber() throws Exception {
        client.setSerialNumber(NEW_SERIAL_NUMBER);
        assertEquals("Serial numbers should be equal", NEW_SERIAL_NUMBER, client.getSerialNumber());
    }

    @Test
    public void testGetId() throws Exception {
        assertEquals("Ids should be equal", ID, client.getId());
    }

    @Test
    public void testSetId() throws Exception {
        client.setId(NEW_ID);
        assertEquals("Ids should be equal", NEW_ID, client.getId());
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals("Names should be equal", NAME, client.getName());
    }

    @Test
    public void testSetName() throws Exception {
        client.setName(NEW_NAME);
        assertEquals("Names should be equal", NEW_NAME, client.getName());
    }

}