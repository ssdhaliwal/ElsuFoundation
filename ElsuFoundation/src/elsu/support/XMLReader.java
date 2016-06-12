package elsu.support;

import java.util.*;
import java.io.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * XMLReader is a DOM processor for XML files. The class was designed to allow
 * for simpler and quick access to the XML nodes, their attributes, and
 * children.
 *
 * 20090709 SSD initial version 20090827 SSD added getRootNode() method to
 * position the document to start of the processing node. It helps in scanning
 * through all children till it finds the first matching node. 20141128 SSD
 * updated the debug, removed it
 *
 * @author: seraj.dhaliwal
 * @email: seraj.dhaliwal@live.com
 *
 */
public class XMLReader {

    private String _xmlDocument = null;
    private DocumentBuilderFactory _documentBuilderFactory = null;
    private DocumentBuilder _documentBuilder = null;
    private Document _document = null;
    private XPath _xPath = null;

    /**
     * XMLReader constructor. Pass it a the document we will be working on and
     * all the class support methods provide access to the data.
     *
     * @param xmlDocument full path to the document
     * @exception XMLException
     */
    public XMLReader(String xmlDocument) throws XMLException {
        try {
            setXMLDocument(xmlDocument);
        } catch (Exception ex) {
            throw new XMLException(ex.getMessage());
        }
    }

    /**
     * Method used to perform initialization. It is indirectly called from
     * setXMLDocument. Users can call setXMLDocument to change the XML document
     * the class will be processing.
     *
     * @exception XMLException
     */
    private void initalizeXML() throws XMLException {
        /*
         * create the XML factory and set default values.
         */
        _documentBuilderFactory = DocumentBuilderFactory.newInstance();
        _documentBuilderFactory.setNamespaceAware(true);
        _documentBuilderFactory.setValidating(false);

        try {
            _documentBuilder = _documentBuilderFactory.newDocumentBuilder();
        } catch (Exception ex) {
            throw new XMLException(ex.getMessage());
        }

        try {
            if (getXMLDocument().startsWith("<?xml ")) {
                _document = _documentBuilder.parse(new InputSource(
                        new StringReader(getXMLDocument())));
            } else {
                _document = _documentBuilder.parse(getXMLDocument());
            }
        } catch (Exception ex) {
            throw new XMLException(ex.getMessage());
        }

        _xPath = XPathFactory.newInstance().newXPath();
        _document.getDocumentElement().normalize();
    }

    /**
     * Returns the node starting with the node name provided. Useful to
     * reposition the rootnode prior to processing the children.
     *
     * @param node
     * @param nodeName
     * @return
     */
    public Node getRootNode(Node node, String nodeName) {
        Node rootNode = null;

        // loop and get all the child nodes for this node
        if (node.hasChildNodes()) {
            NodeList nodes = node.getChildNodes();

            if (nodes != null) {
                for (int i = 0; i < nodes.getLength() && rootNode == null; i++) {
                    if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE
                            && nodes.item(i).getNodeName()
                            .equalsIgnoreCase(nodeName)) {
                        rootNode = nodes.item(i);
                    } else if (nodes.item(i).hasChildNodes()) {
                        rootNode = getRootNode(nodes.item(i), nodeName);
                    }
                }
            }
        }

        return rootNode;
    }

    /**
     * Returns an ArrayList of the node attributes (strings) - not their values.
     * This is useful when the user wants to get a list of all attributes and
     * then retrieve value for the one they are processing.
     *
     * @param node XML document node
     * @return ArrayList or null
     */
    public ArrayList<Node> getNodeAttributes(Node node) {
        ArrayList<Node> al = null;

        // loop and get all the child nodes for this node
        if (node.hasAttributes()) {
            al = new ArrayList<>();
            NamedNodeMap nodes = node.getAttributes();

            if (nodes != null) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    al.add((Node) nodes.item(i));
                }
            }
        }

        return al;
    }

    /**
     * Returns an ArrayList of the node children (objects) - not their values.
     * This is useful when the user wants to get a list of all children and then
     * retrieve value for the one they are processing.
     *
     * @param node XML document node
     * @return ArrayList or null
     */
    public ArrayList<Node> getNodeChildren(Node node) {
        ArrayList<Node> al = null;

        // loop and get all the child nodes for this node
        if (node.hasChildNodes()) {
            al = new ArrayList<>();
            NodeList nodes = node.getChildNodes();

            if (nodes != null) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        al.add((Node) nodes.item(i));
                    }
                }
            }
        }

        return al;
    }

    /**
     * Returns an ArrayList of the child nodes. This is useful when the user all
     * node objects so they can process them by iterating one-by-one.
     *
     * @param element name of the child element
     * @return NodeList or null
     */
    public NodeList getNodesByElement(String element) {
        NodeList nl;

        if (element != null) {
            nl = getDocument().getElementsByTagName(element);
        } else {
            nl = getDocument().getElementsByTagName("*");
        }

        return nl;
    }

    /*
     * Returns node from document using XPath query
     */
    public NodeList getNodeListByXPath(String expression) {
        try {
            return (NodeList) _xPath.compile(expression).evaluate(getDocument(),
                    XPathConstants.NODESET);
        } catch (Exception exi) {
            return null;
        }
    }

    public Node getNodeByXPath(String expression) {
        try {
            return (Node) _xPath.compile(expression).evaluate(getDocument(),
                    XPathConstants.NODE);
        } catch (Exception exi) {
            return null;
        }
    }

    public String getNodeValueByXPath(String expression) {
        try {
            return _xPath.compile(expression).evaluate(getDocument());
        } catch (Exception exi) {
            return null;
        }
    }

    public NodeList getNodeAttributesByXPath(String expression) {
        try {
            return (NodeList) _xPath.compile(expression).evaluate(getDocument(),
                    XPathConstants.NODESET);
        } catch (Exception exi) {
            return null;
        }
    }

    /**
     * Returns the value of the attribute specified by Node.
     *
     * @param node XML document node
     * @param attribute name of the attribute
     * @return value of the attributes or null
     */
    public String getNodeAttributeValue(Node node, String attribute) {
        ArrayList<Node> al;
        Enumeration emr;
        Node n;

        // get attributes
        al = getNodeAttributes(node);
        if (al.size() > 0) {
            emr = Collections.enumeration(al);

            while (emr.hasMoreElements()) {
                n = (Node) emr.nextElement();

                if (n.getNodeName().equalsIgnoreCase(attribute)) {
                    return (n.getNodeValue());
                }
            }
        }

        return null;
    }

    /**
     * Returns the text for the node. Text is stored between the start and end
     * tags of a XML element not as part of the tag.
     *
     * @param node XML document node
     * @return text (string) or null
     */
    public String getNodeText(Node node) {
        if (node.getChildNodes() == null) {
            return null;
        }
        if (node.getChildNodes().getLength() == 1) {
            if (node.getFirstChild().getNodeType() == Node.TEXT_NODE) {
                // java 1.5 return(node.getTextContent());
                return (node.getFirstChild().getNodeValue());
            }
        }

        return null;
    }

    /**
     * (accessor) Sets and returns the document this class is going to process.
     * When new document is specified, then XML DOM engine is reinitialized.
     *
     * @param xmlDocument full path of the XML document
     * @return xmlDocument name of the stored document
     * @exception XMLException
     */
    private String setXMLDocument(String xmlDocument) throws XMLException {
        _xmlDocument = xmlDocument;

        try {
            initalizeXML();
        } catch (Exception ex) {
            throw new XMLException(ex.getMessage());
        }

        return getXMLDocument();
    }

    /**
     * (accessor) Returns the document this class is going to process.
     *
     * @return xmlDocument name of the stored document
     */
    public String getXMLDocument() {
        return _xmlDocument;
    }

    /**
     * (accessor) Returns the _document object which was created to process the
     * XML document.
     *
     * @return _document XML DOM document object
     */
    public Document getDocument() {
        return _document;
    }
}
