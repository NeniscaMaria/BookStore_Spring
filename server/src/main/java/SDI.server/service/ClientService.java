package SDI.server.service;

import SDI.server.repository.DataBase.ClientDBRepository;
import domain.Message;
import domain.Sort;
import SDI.server.repository.Repository;
import domain.ValidatorException;
import Service.ClientServiceInterface;
import domain.Client;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ClientService implements ClientServiceInterface {
    private Repository<Long, Client> repository;
    private ExecutorService executorService;

    public ClientService(Repository<Long, domain.Client> repository, ExecutorService executorService) {
        this.repository = repository;
        this.executorService = executorService;
    }

    @Override
    public synchronized CompletableFuture<Optional<Client>> addClient(Client client) throws ValidatorException {
        return CompletableFuture.supplyAsync(()-> {
            try {
                return repository.save(client);
            } catch (ParserConfigurationException | IOException | SAXException | TransformerException | SQLException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        },executorService).handle((res,ex)->{return Optional.empty();});
    }

    public synchronized CompletableFuture<Iterable<Client>> getAllClients(String ...a) throws SQLException {
        return  CompletableFuture.supplyAsync(()-> {
            Iterable<Client> clients = null;
            if (repository instanceof ClientDBRepository) {
                try {
                    clients = ((ClientDBRepository) repository).findAll(new Sort(a).and(new Sort(a)));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            };
            return StreamSupport.stream(clients.spliterator(), false).collect(Collectors.toList());
        });
    }

    @Override
    public synchronized CompletableFuture<Optional<Client>> removeClient(Long ID) throws SQLException {
        return CompletableFuture.supplyAsync(()-> {
            try {
                return repository.delete(ID);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        },executorService);
    }

    @Override
    public synchronized CompletableFuture<Optional<Client>> updateClient(domain.Client client) throws ValidatorException, SQLException {
        return CompletableFuture.supplyAsync(()-> {
            try {
                return repository.update(client);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        },executorService);
    }

    @Override
    public synchronized CompletableFuture<Set<Client>> getAllClients() throws SQLException {
        return CompletableFuture.supplyAsync(()->{
            Iterable<Client> clients = null;
            try {
                clients = repository.findAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return StreamSupport.stream(clients.spliterator(), false).collect(Collectors.toSet());
        },executorService);
    }

    /*POST:Returns all students whose name contain the given string.
     PRE: @param s
     */
    @Override
    public synchronized CompletableFuture<Set<Client>> filterClientsByName(String s) throws SQLException {
        return CompletableFuture.supplyAsync(()->{
            Iterable<Client> clients = null;
            try {
                clients = repository.findAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            HashSet<domain.Client> filteredClients= new HashSet<>();
            clients.forEach(filteredClients::add);
            filteredClients.removeIf(student -> !student.getName().contains(s));
            return filteredClients;
        },executorService);
    }
    @Override
    public synchronized CompletableFuture<Optional<Client>> findOneClient(Long clientID) throws SQLException {
        return CompletableFuture.supplyAsync(()-> {
            try {
                return repository.findOne(clientID);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        },executorService);
    }

}
