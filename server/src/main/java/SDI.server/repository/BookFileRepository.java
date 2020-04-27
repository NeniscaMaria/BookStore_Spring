package SDI.server.repository;

import SDI.server.validators.Validator;
import domain.ValidatorException;
import domain.Book;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BookFileRepository extends InMemoryRepository<Long, Book> {
    private String fileName;

    public BookFileRepository(Validator<Book> validator, String fileName) {
        super(validator);
        this.fileName = fileName;
        loadData();
    }

    // Load data from file
    private void loadData() {
        Path path = Paths.get(fileName);

        try {
            Files.lines(path).forEach(line -> {
                List<String> items = Arrays.asList(line.split(","));

                Long id = Long.valueOf(items.get(0));
                String serialNumber = items.get(1);
                String name = items.get((2));
                String author = items.get((3));
                int year = Integer.parseInt(items.get((4)));
                double price = Double.parseDouble(items.get((5)));
                int stock = Integer.parseInt(items.get((6)));


                Book book = new Book(serialNumber, name, author, year, price, stock);
                book.setId(id);

                try {
                    super.save(book);
                } catch (ValidatorException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Optional<Book> save(Book entity) throws ValidatorException {
        Optional<Book> optional = null;
        optional = super.save(entity);
        if (optional.isPresent()) {
            return optional;
        }
        saveToFile(entity);

        return Optional.empty();
    }

    public Optional<Book> update(Book book){
        Optional<Book> b = super.update(book);
        b.ifPresent(bb -> {saveAllToFile();});
        return b;
    }

    public Optional<Book> delete(Long bookID){
        Optional<Book> b = super.delete(bookID);
        b.ifPresent(
                bb -> {saveAllToFile();}
        );
        return b;
    }

    // Save data to file
    // in: entity (Book)
    private void saveToFile(domain.Book entity) {
        Path path = Paths.get(fileName);

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {

            bufferedWriter.write(
                    entity.getId() + "," + entity.getSerialNumber() + "," + entity.getTitle() + "," + entity.getAuthor() + "," + entity.getYear()+","+entity.getPrice()+","+entity.getInStock());
            bufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Re-writes the document
    private void saveAllToFile(){
        Path path = Paths.get(fileName);
        Iterable<Book> books = super.findAll();
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName))) {
            books.forEach(entity -> {
                try {
                    bufferedWriter.write(
                            entity.getId() + "," + entity.getSerialNumber() + "," + entity.getTitle() + "," + entity.getAuthor() + "," + entity.getYear()+","+entity.getPrice()+","+entity.getInStock());
                    bufferedWriter.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
