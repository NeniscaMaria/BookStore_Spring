package Service;

import domain.Client;
import domain.ValidatorException;

import javax.xml.parsers.ParserConfigurationException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface ClientServiceInterface {
    public String GET_ALL_CLIENTS = "getAllClients";
    public String GET_ALL_SORT = "getAllSort";
    public String REMOVE_CLIENT = "removeClient";
    public String ADD_CLIENT = "addClient";
    public String UPDATE_CLIENT = "updateClient";
    public String FIND_ONE = "findOneClient";
    public String FILTER_NAME = "filterClientsByName";

    CompletableFuture<Set<Client>> getAllClients() throws SQLException;
    CompletableFuture<Optional<Client>> removeClient(Long id) throws SQLException;
    CompletableFuture<Optional<Client>> addClient(Client entity) throws SQLException, ValidatorException, ParserConfigurationException;

    CompletableFuture<Optional<Client>> updateClient(Client entity) throws SQLException, ValidatorException;
    CompletableFuture<Optional<Client>> findOneClient(Long clientID) throws SQLException;
    CompletableFuture<Set<Client>> filterClientsByName(String s) throws SQLException;
}
