package SDI.server.repository;

import SDI.server.validators.Validator;
import domain.Client;
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

public class ClientXMLRepository extends InMemoryRepository<Long, Client> {
    private String fileName;

    public ClientXMLRepository(Validator<Client> validator, String fileName) {
        super(validator);
        this.fileName = fileName;
        try {
            loadData();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    private void saveAllToFile(Document document){
        try {
            Transformer transformer = TransformerFactory
                    .newInstance()
                    .newTransformer();
            transformer.transform(new DOMSource(document),
                    new StreamResult(new File(fileName)));
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
    private Client createClientFromElement(Element clientElement){
        Node IDNode = clientElement.getElementsByTagName("ID").item(0);
        Long ID = Long.parseLong(IDNode.getTextContent());

        Node serialNoNode = clientElement.getElementsByTagName("SerialNo").item(0);
        String serialNo = serialNoNode.getTextContent();

        Node nameNode = clientElement.getElementsByTagName("Name").item(0);
        String name = nameNode.getTextContent();

        Client client = new Client(serialNo,name);
        client.setId(ID);
        return client;
    }
    private void loadData() throws ParserConfigurationException, IOException, SAXException {
        File file = new File(fileName);
        Document document = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(file);

        document.getDocumentElement().normalize();
        Element root = document.getDocumentElement();

        NodeList children = document.getElementsByTagName("client");
        IntStream.range(0, children.getLength())
                .mapToObj(children::item)
                .filter(node -> node.getNodeType()==Node.ELEMENT_NODE)
                .forEach(node ->{ super.save(createClientFromElement((Element) node));});
    }

    private Node clientToNode(Client client, Document document){
        Element clientElement = document.createElement("client");
        Element IDElement = document.createElement("ID");
        IDElement.setTextContent(client.getId().toString());
        clientElement.appendChild(IDElement);

        Element serialNoElement = document.createElement("SerialNo");
        serialNoElement.setTextContent(client.getSerialNumber());
        clientElement.appendChild(serialNoElement);

        Element nameElement = document.createElement("Name");
        nameElement.setTextContent(client.getName());
        clientElement.appendChild(nameElement);

        return clientElement;
    }

    @Override
    public Optional<Client> save(domain.Client entity){

        Optional<Client> optional;
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

    private void saveToFile(domain.Client entity) throws ParserConfigurationException, TransformerException, IOException, SAXException {
        Document document = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(fileName);

        Element root = document.getDocumentElement();
        Node clientNode = clientToNode(entity, document);
        root.appendChild(clientNode);
        saveAllToFile(document);
    }

    public Optional<Client> update(Client client){
        Optional<Client> res = super.update(client);
        res.ifPresent(r-> {
            try {
                Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fileName);
                Element root = document.getDocumentElement();
                NodeList children = root.getChildNodes();

                IntStream.range(0, children.getLength())
                        .mapToObj(children::item)
                        .filter(node -> node instanceof Element)
                        .filter(node-> createClientFromElement((Element) node).getId().equals(client.getId()))
                        .forEach(node->{
                            Node parent = node.getParentNode();
                            Node newNode = clientToNode(client,document);
                            parent.replaceChild(newNode,node);
                            Transformer transformer= null;
                            saveAllToFile(document);
                        });
            } catch (SAXException | IOException | ParserConfigurationException e) {
                e.printStackTrace();
            }});
        return res;
    }


    public Optional<Client> delete(Long ID){
        Optional<Client> res = super.delete(ID);
        res.ifPresent(r->{
            try {
                Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fileName);
                Element root = document.getDocumentElement();
                NodeList children = root.getChildNodes();

                IntStream.range(0, children.getLength())
                        .mapToObj(children::item)
                        .filter(node -> node instanceof Element)
                        .filter(node-> createClientFromElement((Element)node).getId()==ID)
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
