/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elsu.support;

import java.util.*;

/**
 *
 * @author ssdhaliwal
 */
public class XMLViewer {

    // variable to store the xml document object from XMLReader class
    protected XMLReader _xmlr = null;

    public XMLViewer(String document) throws Exception {
        _xmlr = new XMLReader(document);
        showConfig(_xmlr.getDocument(), 1);
    }

    public XMLViewer(org.w3c.dom.Node document) {
        showConfig(document, 1);
    }

    public XMLViewer(String document, int level) throws Exception {
        _xmlr = new XMLReader(document);
        showConfig(_xmlr.getDocument(), level);
    }

    public XMLViewer(org.w3c.dom.Node document, int level) {
        showConfig(document, level);
    }

    /**
     * showConfigNodes(...) method is used to recursively scan the XML config
     * file and display the nodes in tree format.
     *
     * @param parent
     * @param level level of the node, increased for each child-node to allow
     * tabbed tree display output
     *
     */
    protected void showConfig(org.w3c.dom.Node parent, int level) {
        // create a local class to display node value/text and associated
        // node attributes
        class SubShowNode {

            // loop through the node attributes for the node passed
            String displayNodeAttributes(org.w3c.dom.Node node) {
                // if node is null, return empty string
                if (node == null) {
                    return "";
                }

                // create string build object to support string concatanation
                StringBuilder sb = new StringBuilder();

                // retrieve node attributes (if any)
                ArrayList nAttributes = _xmlr.getNodeAttributes(node);

                // loop through the attributes array and append them to the
                // string builder object
                if (nAttributes != null) {
                    for (Object na : nAttributes) {
                        // append the attribute details (key/text) to the string
                        // builder object
                        if (na != null) {
                            sb.append(" [ATTR=").append(((org.w3c.dom.Node) na).getNodeName())
                                    .append("//")
                                    .append(((org.w3c.dom.Node) na).getNodeValue())
                                    .append("]");
                        }

                        // yield processing to other threads
                        Thread.yield();
                    }
                }

                // return the string builder representation as a string
                return sb.toString();
            }
        }

        // declare the showNode class to allow methods to reference the display
        // method to prevent duplicaion in code
        SubShowNode showNode = new SubShowNode();

        // retrieve the child nodes for processing
        ArrayList nodes = _xmlr.getNodeChildren(parent);

        // if node level is 1, then this is root node, display it with no
        // indentation
        if (level == 1) {
            // display the parent node name
            String data = org.apache.commons.lang3.StringUtils.repeat('~', level) + parent.getNodeName();

            // use the sub function to extract node attributes
            data += showNode.displayNodeAttributes(parent);

            // display all collected data to the user output
            System.out.println(data);
        }

        // parse the list of child nodes for the node being processed
        if (nodes != null) {
            for (Object node : nodes) {
                // display the parent node name
                String data = org.apache.commons.lang3.StringUtils.repeat('\t', level)
                        + ((org.w3c.dom.Node) node).getNodeName();

                // use the sub function to extract node attributes
                data += showNode.displayNodeAttributes((org.w3c.dom.Node) node);

                // if node has a text value, display the text
                if (_xmlr.getNodeText((org.w3c.dom.Node) node) != null) {
                    data += " (TEXT=" + _xmlr.getNodeText((org.w3c.dom.Node) node)
                            + ")";
                }

                // display all collected data to the user output
                System.out.println(data);

                // recall the function (recursion) to see if the node has child 
                // nodes and preocess them in hierarchial level
                showConfig((org.w3c.dom.Node) node, (level + 1));

                // yield processing to other threads
                Thread.yield();
            }
        }
    }
}
