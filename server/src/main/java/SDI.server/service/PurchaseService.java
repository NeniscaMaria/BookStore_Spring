package SDI.server.service;

import SDI.server.repository.DataBase.PurchaseDataBaseRepository;
import domain.Sort;
import SDI.server.repository.Repository;
import Service.PurchaseServiceInterface;
import domain.ValidatorException;
import domain.Purchase;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PurchaseService  implements PurchaseServiceInterface {

    private Repository<Long, Purchase> repository;
    private ExecutorService executorService;

    public PurchaseService(Repository<Long, domain.Purchase> repository, ExecutorService executorService) {
        this.repository = repository;
        this.executorService = executorService;
    }

    public synchronized CompletableFuture<Optional<Purchase>> addPurchase(domain.Purchase purchase) throws ValidatorException{
        return CompletableFuture.supplyAsync(()-> {
            try {
                return repository.save(purchase);
            } catch (ParserConfigurationException | IOException | SAXException | TransformerException | SQLException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        },executorService);
    }

    public synchronized void removeClients(Long ID) throws IOException, SAXException, ParserConfigurationException {
        repository.removeEntitiesWithClientID(ID);
    }

    public synchronized CompletableFuture<Optional<Purchase>> removePurchase(Long ID) throws SQLException {
        return CompletableFuture.supplyAsync(()-> {
            try {
                return repository.delete(ID);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        },executorService);
    }

    public synchronized CompletableFuture<Optional<Purchase>> updatePurchase(domain.Purchase purchase) throws ValidatorException, SQLException {
        return CompletableFuture.supplyAsync(()-> {
            try {
                return repository.update(purchase);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        },executorService);
    }

    public synchronized CompletableFuture<Set<Purchase>> getAllPurchases() throws SQLException {

        return CompletableFuture.supplyAsync(()->{
            Iterable<Purchase> purchases= null;
            try {
                purchases = repository.findAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return StreamSupport.stream(purchases.spliterator(), false).collect(Collectors.toSet());
        },executorService);
    }

    public synchronized Iterable<Purchase> getAllPurchases(String ...a) throws SQLException {

        Iterable<domain.Purchase> pur;
        if (repository instanceof PurchaseDataBaseRepository){
            pur = ((PurchaseDataBaseRepository)repository).findAll(new Sort(a).and(new Sort(a)));
            return StreamSupport.stream(pur.spliterator(), false).collect(Collectors.toList());
        }
        else throw new ValidatorException("Too many parameters");

    }

    public synchronized CompletableFuture<Set<Purchase>> filterPurchasesByClientID(Long clientID) throws SQLException {
        return CompletableFuture.supplyAsync(()->{
            Iterable<Purchase> purchases = null;
            try {
                purchases = repository.findAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Set<domain.Purchase> filteredPurchases= new HashSet<>();
            purchases.forEach(filteredPurchases::add);
            filteredPurchases.removeIf(purchase -> !(purchase.getClientID()==clientID));
            return filteredPurchases;
        },executorService);
    }

    public synchronized CompletableFuture<Optional<Purchase>> findOnePurchase(Long purchaseID) throws SQLException {
        return CompletableFuture.supplyAsync(()-> {
            try {
                return repository.findOne(purchaseID);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        },executorService);
    }
}
