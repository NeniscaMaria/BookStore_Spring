package SDI.server.repository;

import domain.ValidatorException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public interface Repository<ID, T extends domain.BaseEntity<ID>> {
    /*
        DESCR:Find the entity with the given {@code id}.
        PRE:param id must be not null.
        POST: return an {@code Optional} encapsulating the entity with the given id.
        THROWS: IllegalArgumentException if the given id is null.
     */
    Optional<T> findOne(ID id) throws SQLException;

    Iterable<T> findAll() throws SQLException;//return all entities

    /*
     DESCR: Saves the given entity.
     PRE: param entity must not be null.
     POST: return an {@code Optional} - null if the entity was saved otherwise (e.g. id already exists) returns the entity.
     THROWS: IllegalArgumentException if the given entity is null.
            ValidatorException if the entity is not valid.
     */
    Optional<T> save(T entity) throws ValidatorException, ParserConfigurationException, IOException, SAXException, TransformerException, SQLException;

    /*DESCR:Removes the entity with the given id.
     PRE: param id must not be null.
     POST: return an {@code Optional} - null if there is no entity with the given id, otherwise the removed entity.
     THROWS: IllegalArgumentException if the given id is null.
     */
    Optional<T> delete(ID id) throws SQLException;

    /*DESCR:Updates the given entity.
     PRE: param entity must not be null.
     POST: @return an {@code Optional} - null if the entity was updated otherwise (e.g. id does not exist) returns the
              entity.
     THROWS: IllegalArgumentException if the given entity is null.
             ValidatorException if the entity is not valid.
     */
    Optional<T> update(T entity) throws ValidatorException, SQLException;

    void removeEntitiesWithClientID(ID id) throws ParserConfigurationException, IOException, SAXException;

}
