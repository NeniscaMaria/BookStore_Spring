package SDI.server.validators;

import domain.ValidatorException;

public interface Validator<T> {
    void validate(T entity) throws ValidatorException;
}
