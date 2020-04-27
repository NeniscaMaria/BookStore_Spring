package SDI.server.validators;

import domain.Book;
import domain.ValidatorException;

import java.util.Calendar;
import java.util.Optional;

public class BookValidator implements Validator<Book>  {
    boolean checkNull(String stringToBeChecked){
        return stringToBeChecked.equals("") || stringToBeChecked.equals(" ");
    }
    private Optional<Boolean> validName(String name){
        // Name can't contain numbers, special characters
        // If multiple authors, they are separate by ';'
        return Optional.of(name.contains("1234567890=+_[]{}()~`!@#$%^&*<>,/?"));
    }
    private boolean isSerialNumberValid(String number){//no spaces in the serial number and no special characters
        String[] arrOfStr = number.split(" ", 3);
        return arrOfStr.length==1 && !number.contains("!,.:;?/(){}[]@#$%^&*_+=-");
    }

    @Override
    public void validate(Book entity) throws ValidatorException {
        // Checks if the Book is null
        if(entity == null)
            throw new ValidatorException("Book cannot be null");

        if(entity.getId() < 0)
            throw new ValidatorException("Please enter a non-negative ID.");

        if(entity.getSerialNumber().equals("0") || !isSerialNumberValid(entity.getSerialNumber()))
            throw new ValidatorException("Please enter a valid serial number.");

        if(checkNull(entity.getTitle()))
            throw new ValidatorException("Please enter a valid name.");

        if(checkNull(entity.getAuthor()))
            throw new ValidatorException("Please enter a valid author name.");
        //Optional<Boolean> e = validName(entity.getAuthor());
        //this cannot work because in optional we will always have a value : true/false.
        //e.ifPresent(c->{throw new ValidatorException("Please enter a valid author name.");});

        if(entity.getYear() < 0 || entity.getYear() > Calendar.getInstance().get(Calendar.YEAR))
            throw new ValidatorException("Please enter a valid year");

        if(entity.getPrice() <= 0)
            throw new ValidatorException("Please enter a valid price");

        



    }
}
