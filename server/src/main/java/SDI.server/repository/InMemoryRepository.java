package SDI.server.repository;

import SDI.server.validators.Validator;
import domain.ValidatorException;
import domain.BaseEntity;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryRepository<ID, T extends BaseEntity<ID>> implements SDI.server.repository.Repository<ID, T> {

    protected Map<ID, T> entities;
    private Validator<T> validator;

    public InMemoryRepository(Validator<T> validator) {
        this.validator=validator;
        entities = new HashMap<>();
    }

    @Override
    public Optional<T> findOne(ID id) {
        if(id==null)
            throw new IllegalArgumentException("ID must not be null");
        return Optional.ofNullable(entities.get(id)); // returns null if no mapping of the key found
        //.orElseThrow(()->new IllegalArgumentException("ID must not be null"));
    }

    @Override
    public Iterable<T> findAll() {
        return entities.entrySet().stream().map(entry -> entry.getValue()).collect(Collectors.toSet());
    }

    @Override
    public Optional<T> save(T entity) throws ValidatorException {//the validator check if the entity is null
        validator.validate(entity);
        return Optional.ofNullable(entities.putIfAbsent(entity.getId(), entity)); //returns current value if entity present
    }

    @Override
    public Optional<T> delete(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        return Optional.ofNullable(entities.remove(id));//null if there was no mapping for key
    }

    @Override
    public Optional<T> update(T entity) throws ValidatorException {//the validator checks if the entity is null
        validator.validate(entity);
        return Optional.ofNullable(entities.computeIfPresent(entity.getId(), (k, v) -> entity));//if successful returns entity it should return null
        //returns null if no such entity found in map
    }

    @Override
    public void removeEntitiesWithClientID(ID id) throws ParserConfigurationException, IOException, SAXException {
        return;
    }
}
