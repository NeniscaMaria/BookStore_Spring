package SDI.server.repository;

import SDI.server.validators.Validator;
import domain.Purchase;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PurchaseXMLRepository extends InMemoryRepository<Long, Purchase> {
    private String filename;

    public PurchaseXMLRepository(Validator<Purchase> validator, String fileName) throws IOException, SAXException, ParserConfigurationException {
        super(validator);
        this.filename = fileName;
        loadData();
    }

    @Override
    public void removeEntitiesWithClientID(Long ID) throws ParserConfigurationException, IOException, SAXException {
        if (ID == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(filename);
        Element root = document.getDocumentElement();
        NodeList children = root.getChildNodes();

        List<Purchase> valuesToRemove = entities.values().stream().filter(p-> p.getClientID().equals(ID)) //we remained only with the purchaseID s of the client with ID
                .collect(Collectors.toList());
        entities.values().removeAll(valuesToRemove);

        IntStream.range(0, children.getLength())
                .mapToObj(children::item)
                .filter(node -> node instanceof Element)
                .filter(node-> createPurchaseFromElement((Element)node).getClientID()==ID)
                .forEach(node->{
                    Node parent = node.getParentNode();
                    parent.removeChild(node);
                    saveAllToFile(document);});
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
    private Purchase createPurchaseFromElement(Element purchaseElement){
        Node IDNode = purchaseElement.getElementsByTagName("ID").item(0);
        Long ID = Long.parseLong(IDNode.getTextContent());

        Node clientIDNode = purchaseElement.getElementsByTagName("clientID").item(0);
        Long clientID = Long.parseLong(clientIDNode.getTextContent());

        Node bookIDNode = purchaseElement.getElementsByTagName("bookID").item(0);
        Long bookID = Long.parseLong(bookIDNode.getTextContent());

        Node nrBooksNode = purchaseElement.getElementsByTagName("nrBooks").item(0);
        int nrBooks = Integer.parseInt(nrBooksNode.getTextContent());

        Purchase purchase = new Purchase(clientID,bookID,nrBooks);
        purchase.setId(ID);
        return purchase;
    }
    private void loadData() throws ParserConfigurationException, IOException, SAXException {
        File file = new File(filename);
        Document document = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(file);

        document.getDocumentElement().normalize();
        Element root = document.getDocumentElement();

        NodeList children = document.getElementsByTagName("purchase");
        IntStream.range(0, children.getLength())
                .mapToObj(children::item)
                .filter(node -> node.getNodeType()==Node.ELEMENT_NODE)
                .forEach(node ->{ super.save(createPurchaseFromElement((Element) node));});
    }

    private Node purchaseToNode(Purchase purchase, Document document){
        Element purchaseElement = document.createElement("purchase");
        Element IDElement = document.createElement("ID");
        IDElement.setTextContent(purchase.getId().toString());
        purchaseElement.appendChild(IDElement);

        Element clientIDelement = document.createElement("clientID");
        clientIDelement.setTextContent(purchase.getClientID().toString());
        purchaseElement.appendChild(clientIDelement);

        Element bookIDElement = document.createElement("bookID");
        bookIDElement.setTextContent(purchase.getBookID().toString());
        purchaseElement.appendChild(bookIDElement);

        Element nrBooksElement= document.createElement("nrBooks");
        nrBooksElement.setTextContent(Integer.toString(purchase.getNrBooks()));
        purchaseElement.appendChild(nrBooksElement);

        return purchaseElement;
    }

    @Override
    public Optional<Purchase> save(domain.Purchase entity){

        Optional<Purchase> optional;
        try {
            optional = super.save(entity);
            if (optional.isPresent()) {
                return optional;
            }
            saveToFile(entity);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private void saveToFile(domain.Purchase entity) throws ParserConfigurationException, IOException, SAXException {
        Document document = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(filename);

        Element root = document.getDocumentElement();
        Node clientNode = purchaseToNode(entity, document);
        root.appendChild(clientNode);
        saveAllToFile(document);
    }

    public Optional<Purchase> update(Purchase purchase){
        Optional<Purchase> res = super.update(purchase);
        res.ifPresent(r-> {
            try {
                Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(filename);
                Element root = document.getDocumentElement();
                NodeList children = root.getChildNodes();

                IntStream.range(0, children.getLength())
                        .mapToObj(children::item)
                        .filter(node -> node instanceof Element)
                        .filter(node-> createPurchaseFromElement((Element) node).getId().equals(purchase.getId()))
                        .forEach(node->{
                            Node parent = node.getParentNode();
                            Node newNode = purchaseToNode(purchase,document);
                            parent.replaceChild(newNode,node);
                            Transformer transformer= null;
                            saveAllToFile(document);
                        });
            } catch (SAXException | IOException | ParserConfigurationException e) {
                e.printStackTrace();
            }});
        return res;
    }


    public Optional<Purchase> delete(Long ID){
        Optional<Purchase> res = super.delete(ID);
        res.ifPresent(r->{
            try {
                Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(filename);
                Element root = document.getDocumentElement();
                NodeList children = root.getChildNodes();

                IntStream.range(0, children.getLength())
                        .mapToObj(children::item)
                        .filter(node -> node instanceof Element)
                        .filter(node-> createPurchaseFromElement((Element)node).getId()==ID)
                        .forEach(node->{
                            Node parent = node.getParentNode();
                            parent.removeChild(node);
                            saveAllToFile(document);});
            } catch (SAXException | IOException | ParserConfigurationException e) {
                e.printStackTrace();
            }});
        return res;
    }
}
