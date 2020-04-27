package SDI.server.repository;

import SDI.server.validators.Validator;
import domain.ValidatorException;
import domain.Purchase;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PurchaseFileRepository extends InMemoryRepository<Long, Purchase> {
    private String fileName;

    public PurchaseFileRepository(Validator<Purchase> validator, String fileName) {
        super(validator);
        this.fileName = fileName;
        loadData();
    }

    @Override
    public void removeEntitiesWithClientID(Long ID){
        if (ID == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        List<Purchase> valuesToRemove = entities.values().stream().filter(p-> p.getClientID().equals(ID)) //we remained only with the purchaseID s of the client with ID
                .collect(Collectors.toList());
        entities.values().removeAll(valuesToRemove);
        this.writeAllToFile();
    }

    private void loadData() {
        Path path = Paths.get(fileName);
        try {//Files.lines(path) return a stream that contains the lines in the file
            Files.lines(path).forEach(line -> {
                List<String> items = Arrays.asList(line.split(","));
                if(items.size()==4) {
                    Long id = Long.valueOf(items.get(0));
                    Long clientID = Long.valueOf(items.get(1));
                    Long bookID = Long.valueOf(items.get((2)));
                    int nrBooks = Integer.parseInt(items.get(3));
                    domain.Purchase purchase = new Purchase(clientID, bookID,nrBooks);
                    purchase.setId(id);
                    try {
                        super.save(purchase);
                    } catch (ValidatorException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Optional<Purchase> save(domain.Purchase entity) throws ValidatorException{
        Optional<Purchase> optional = null;
        optional = super.save(entity);
        if (optional.isPresent()) {
            return optional;
        }
        saveToFile(entity);
        return Optional.empty();
    }

    private void saveToFile(domain.Purchase entity) {
        Path path = Paths.get(fileName);
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {

            bufferedWriter.write(
                    entity.getId() + "," + entity.getClientID() + "," + entity.getBookID()+","+entity.getNrBooks());
            bufferedWriter.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeAllToFile(){
        Path path = Paths.get(fileName);
        Iterable<Purchase> clients = super.findAll();
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING)) {
            StreamSupport.stream(clients.spliterator(), false)
                    .forEach(entity->{
                        try {
                            bufferedWriter.write(
                                    entity.getId() + "," + entity.getClientID() + "," + entity.getBookID()+","+entity.getNrBooks());
                            bufferedWriter.newLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Optional<Purchase> update(Purchase purchase){
        Optional<Purchase> res = super.update(purchase);
        res.ifPresent(r->{this.writeAllToFile();});
        return res;
    }

    public Optional<Purchase> delete(Long ID){
        Optional<Purchase> res = super.delete(ID);
        res.ifPresent(r->{this.writeAllToFile();});
        return res;
    }
}
