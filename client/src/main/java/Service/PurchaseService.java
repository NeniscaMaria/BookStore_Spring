package Service;

import TCP.TCPClient;
import domain.Message;
import domain.Purchase;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.CompletableFuture;

public class PurchaseService {
    private ExecutorService executorService;
    private TCPClient tcpClient;

    public PurchaseService(ExecutorService executorService, TCPClient tcpClient) {
        this.executorService = executorService;
        this.tcpClient = tcpClient;
    }

    public CompletableFuture<Message<Set<Purchase>>> getAllPurchases(){
        return CompletableFuture.supplyAsync(()->{
            //create request
            Message request  = new Message(PurchaseServiceInterface.GET_ALL_PURCHASES,"");
            //send request to server and receive answer
            return tcpClient.sendAndReceive(request);
        },executorService);
    }

    public CompletableFuture<Message> removePurchase(Long id){
        return CompletableFuture.supplyAsync(()->{
            String ID = String.valueOf(id);
            Message request = new Message(PurchaseServiceInterface.REMOVE_PURCHASE,ID);
            return tcpClient.sendAndReceive(request);
        },executorService);
    }

    public CompletableFuture<Message<Optional<Purchase>>> addPurchase(Purchase purchase){
        return CompletableFuture.supplyAsync(()-> {
            Message request = new Message(PurchaseServiceInterface.ADD_PURCHASE, purchase);
            return tcpClient.sendAndReceive(request);
        },executorService);
    }
    public CompletableFuture<Message<Optional<Purchase>>> updatePurchase(Purchase purchase) {
        return CompletableFuture.supplyAsync(()->{
            Message<Purchase> request = new Message<Purchase>(PurchaseServiceInterface.UPDATE_PURCHASE,purchase);
            return tcpClient.sendAndReceive(request);
        },executorService);
    }
    public CompletableFuture<Message<Set<Purchase>>> filterPurchasesByClientID(Long id) throws SQLException {
        return CompletableFuture.supplyAsync(()->{
            String ID = String.valueOf(id);
            Message request = new Message(PurchaseServiceInterface.FILTER,ID);
            return tcpClient.sendAndReceive(request);
        },executorService);
    }

    public CompletableFuture<Message<Optional<Purchase>>> findOnePurchase(Long id) throws SQLException {
        return CompletableFuture.supplyAsync(()->{
            String ID = String.valueOf(id);
            Message request = new Message(PurchaseServiceInterface.FIND_ONE,ID);
            return tcpClient.sendAndReceive(request);
        },executorService);
    }
}


