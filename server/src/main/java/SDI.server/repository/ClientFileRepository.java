package SDI.server.repository;

import SDI.server.validators.Validator;
import domain.ValidatorException;
import domain.Client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;


public class ClientFileRepository extends InMemoryRepository<Long, domain.Client> {
    private String fileName;

    public ClientFileRepository(Validator<Client> validator, String fileName) {
        super(validator);
        this.fileName = fileName;
        loadData();
    }

    private void loadData() { //loads data from file to memory
        Path path = Paths.get(fileName);
        try {//Files.lines(path) return a stream that contains the lines in the file
            Files.lines(path).forEach(line -> {
                List<String> items = Arrays.asList(line.split(","));
                if(items.size()==3) {
                    Long id = Long.valueOf(items.get(0));
                    String serialNumber = items.get(1);
                    String name = items.get((2));
                    domain.Client student = new domain.Client(serialNumber, name);
                    student.setId(id);
                    try {
                        super.save(student);
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
    public Optional<domain.Client> save(domain.Client entity) throws ValidatorException{
        Optional<Client> optional = null;
        optional = super.save(entity);
        if (optional.isPresent()) {
            return optional;
        }
        saveToFile(entity);
        return Optional.empty();
    }

    private void saveToFile(domain.Client entity) {
        //writes changes to file
        Path path = Paths.get(fileName);
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {

            bufferedWriter.write(
                    entity.getId() + "," + entity.getSerialNumber() + "," + entity.getName());
            bufferedWriter.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeAllToFile(){
        //rewrites the whole file
        Path path = Paths.get(fileName);
        Iterable<Client> clients = super.findAll();
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING)) {
            StreamSupport.stream(clients.spliterator(), false)
                    .forEach(entity->{
                        try {
                            bufferedWriter.write(
                                    entity.getId() + "," + entity.getSerialNumber() + "," + entity.getName());
                            bufferedWriter.newLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Optional<Client> update(Client client){
        //updates a client
        Optional<Client> res = super.update(client);
        res.ifPresent(r->{this.writeAllToFile();});
        return res;
    }

    public Optional<Client> delete(Long ID){//delete a client
        Optional<Client> res = super.delete(ID);
        res.ifPresent(r->{this.writeAllToFile();});
        return res;
    }
}
