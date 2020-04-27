package Service;

import TCP.TCPClient;
import domain.Client;
import domain.Message;
import domain.ValidatorException;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.CompletableFuture;

public class ClientService{
    private ExecutorService executorService;
    private TCPClient tcpClient;

    public ClientService(ExecutorService executorService, TCPClient tcpClient) {
        this.executorService = executorService;
        this.tcpClient = tcpClient;
    }

    public CompletableFuture<Message<Set<Client>>> getAllClients(){
        return CompletableFuture.supplyAsync(()->{
            //create request
            Message request  = new Message(ClientServiceInterface.GET_ALL_CLIENTS,"");
            //send request to server and receive answer
            return tcpClient.sendAndReceive(request);
        },executorService);
    }

    public CompletableFuture<Message<Optional<Client>>> removeClient(Long id){
        return CompletableFuture.supplyAsync(()->{
            String ID = String.valueOf(id);
            Message request = new Message(ClientServiceInterface.REMOVE_CLIENT,ID);
            return tcpClient.sendAndReceive(request);
        },executorService);
    }

    public CompletableFuture<Message<Optional<Client>>> addClient(Client client){
        return CompletableFuture.supplyAsync(()-> {
            Message request = new Message(ClientServiceInterface.ADD_CLIENT, client);
            return tcpClient.sendAndReceive(request);
        },executorService);
    }

    public CompletableFuture<Message<Optional<Client>>> updateClient(Client entity) throws SQLException, ValidatorException{
        return CompletableFuture.supplyAsync(()->{
            Message request = new Message(ClientServiceInterface.UPDATE_CLIENT,entity);
            return tcpClient.sendAndReceive(request);
        },executorService);
    }
    public CompletableFuture<Message<Optional<Client>>> findOneClient(Long id) throws SQLException{
        return CompletableFuture.supplyAsync(()->{
            String ID = String.valueOf(id);
            Message request = new Message(ClientServiceInterface.FIND_ONE,ID);
            return tcpClient.sendAndReceive(request);
        },executorService);
    }
    public CompletableFuture<Message<Set<Client>>> filterClientsByName(String s) throws SQLException{
        return CompletableFuture.supplyAsync(()->{
            Message request = new Message(ClientServiceInterface.FILTER_NAME,s);
            return tcpClient.sendAndReceive(request);
        },executorService);
    }
    public CompletableFuture<Message<Iterable<Client>>> getAllClients(String ...a) throws SQLException{
        return CompletableFuture.supplyAsync(()->{
            Message request = new Message(ClientServiceInterface.GET_ALL_SORT,a);
            return tcpClient.sendAndReceive(request);
        },executorService);
    }
}
