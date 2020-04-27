package domain;

import java.io.Serializable;
import java.util.Objects;

public class Purchase extends BaseEntity<Long> implements Serializable {

    private Long clientID;
    private Long bookID;
    private int nrBooks;

    public Purchase() {
    }

    public Purchase(Long clientID, Long bookID, int nrBooks) {
        this.clientID = clientID;
        this.bookID = bookID;
        this.nrBooks = nrBooks;
    }


    public Long getClientID() {
        return clientID;
    }

    public Long getBookID() {
        return bookID;
    }

    public int getNrBooks() {
        return nrBooks;
    }

    public void setClientID(Long clientID) {
        this.clientID = clientID;
    }

    public void setBookID(Long bookID) {
        this.bookID = bookID;
    }

    public void setNrBooks(int nrBooks) {
        this.nrBooks = nrBooks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Purchase purchase = (Purchase) o;
        return nrBooks == purchase.nrBooks &&
                clientID.equals(purchase.clientID) &&
                bookID.equals(purchase.bookID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientID, bookID, nrBooks);
    }

    @Override
    public String toString() {
        return "Purchase{" +
                "clientID=" + clientID +
                ", bookID=" + bookID +
                ", nrBooks=" + nrBooks +
                "} " + super.toString();
    }
}
