package SDI.server.repository;

import SDI.server.validators.Validator;
import domain.Book;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.IntStream;
//jdbc:postgresql://localhost:5432/BookStore
public class BookXMLRepository extends InMemoryRepository<Long, Book> {

    private String filename;

    public BookXMLRepository(Validator<Book> validator, String filename) {
        super(validator);
        this.filename = filename;
        loadData();
    }

    private void saveAllToFile(Document document){
        try {
            Transformer transformer = TransformerFactory
                    .newInstance()
                    .newTransformer();
            transformer.transform(new DOMSource(document),
                    new StreamResult(new File(filename)));
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
    private Book createBookFromElement(Element book){

        Node IDNode = book.getElementsByTagName("id").item(0);
        Long ID = Long.parseLong(IDNode.getTextContent());

        Node serialNoNode = book.getElementsByTagName("serialNo").item(0);
        String serialNo = serialNoNode.getTextContent();

        Node titleNode = book.getElementsByTagName("title").item(0);
        String title = titleNode.getTextContent();

        Node authorNode = book.getElementsByTagName("author").item(0);
        String author = authorNode.getTextContent();

        Node yearNode = book.getElementsByTagName("year").item(0);
        int year = Integer.parseInt(yearNode.getTextContent());

        Node priceNode = book.getElementsByTagName("price").item(0);
        double price = Double.parseDouble(priceNode.getTextContent());

        Node stockNode = book.getElementsByTagName("stock").item(0);
        int stock = Integer.parseInt(stockNode.getTextContent());

        Book newBook = new Book(serialNo, title, author, year, price, stock);
        newBook.setId(ID);
        return newBook;
    }

    private void loadData() {
        File file = new File(filename);

        try {
            Document document = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .parse(file);

            document.getDocumentElement().normalize();

            NodeList children = document.getElementsByTagName("book");
            IntStream.range(0, children.getLength())
                    .mapToObj(children::item)
                    .filter(node -> node.getNodeType()==Node.ELEMENT_NODE)
                    .forEach(node ->{ super.save(createBookFromElement((Element) node));});
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }


    }

    private Node bookToNode(Book book, Document document){
        Element bookElement = document.createElement("book");
        Element IDElement = document.createElement("id");
        IDElement.setTextContent(book.getId().toString());
        bookElement.appendChild(IDElement);

        Element serialNoElement = document.createElement("serialNo");
        serialNoElement.setTextContent(book.getSerialNumber());
        bookElement.appendChild(serialNoElement);

        Element titleElement = document.createElement("title");
        titleElement.setTextContent(book.getTitle());
        bookElement.appendChild(titleElement);

        Element authorElement = document.createElement("author");
        authorElement.setTextContent(book.getAuthor());
        bookElement.appendChild(authorElement);

        Element yearElement = document.createElement("year");
        yearElement.setTextContent(String.valueOf(book.getYear()));
        bookElement.appendChild(yearElement);

        Element priceElement = document.createElement("price");
        priceElement.setTextContent(String.valueOf(book.getPrice()));
        bookElement.appendChild(priceElement);

        Element stockElement = document.createElement("stock");
        stockElement.setTextContent(String.valueOf(book.getInStock()));
        bookElement.appendChild(stockElement);

        return bookElement;
    }

    @Override
    public Optional<Book> save(Book entity){

        Optional<Book> optional;
        try {
            optional = super.save(entity);
            if (optional.isPresent()) {
                return optional;
            }
            saveToFile(entity);
        } catch (ParserConfigurationException | IOException | SAXException | TransformerException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private void saveToFile(Book entity) throws ParserConfigurationException, TransformerException, IOException, SAXException {
        Document document = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(filename);

        Element root = document.getDocumentElement();
        Node bookNode = bookToNode(entity, document);
        root.appendChild(bookNode);
        saveAllToFile(document);
    }

    public Optional<Book> update(Book book){
        Optional<Book> res = super.update(book);
        res.ifPresent(r-> {
            try {
                Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(filename);
                Element root = document.getDocumentElement();
                NodeList children = root.getChildNodes();

                IntStream.range(0, children.getLength())
                        .mapToObj(children::item)
                        .filter(node -> node instanceof Element)
                        .filter(node-> createBookFromElement((Element) node).getId().equals(book.getId()))
                        .forEach(node->{
                            Node parent = node.getParentNode();
                            Node newNode = bookToNode(book, document);
                            parent.replaceChild(newNode,node);
                            Transformer transformer= null;
                            saveAllToFile(document);
                        });
            } catch (SAXException | IOException | ParserConfigurationException e) {
                e.printStackTrace();
            }});
        return res;
    }


    public Optional<Book> delete(Long ID){
        Optional<Book> res = super.delete(ID);
        res.ifPresent(r->{
            try {
                Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(filename);
                Element root = document.getDocumentElement();
                NodeList children = root.getChildNodes();

                IntStream.range(0, children.getLength())
                        .mapToObj(children::item)
                        .filter(node -> node instanceof Element)
                        .filter(node-> createBookFromElement((Element) node).getId().equals(ID))
                        .forEach(node->{
                            Node parent = node.getParentNode();
                            parent.removeChild(node);
                            Transformer transformer= null;
                            saveAllToFile(document);});
            } catch (SAXException | IOException | ParserConfigurationException e) {
                e.printStackTrace();
            }});
        return res;
    }

}
