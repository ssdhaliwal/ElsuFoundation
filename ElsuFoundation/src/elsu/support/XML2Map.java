/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elsu.support;

import java.util.*;
import static java.util.UUID.*;

/**
 *
 * @author ssdhaliwal
 */
public class XML2Map {

    // variable to store the xml document object from XMLReader class
    protected XMLReader _xmlr = null;
    private int _dependencyIndex = 0;
    private String _dupkeySuffix = randomUUID().toString();

    // store all properties from app.config
    private Map<String, Object> _properties = new HashMap<>();

    // array of path strings which need to be filtered from hashmap
    private String[] _filterPath = new String[]{};
    private String[] _dependencyPath = new String[]{};

    public XML2Map(String document) throws Exception {
        _xmlr = new XMLReader(document);
        loadConfig(_xmlr.getDocument(), "", 1);
    }

    public XML2Map(String document, String[] filterPath) throws Exception {
        // update filter path before processing
        if (filterPath != null) {
            this._filterPath = filterPath;
        }

        _xmlr = new XMLReader(document);
        loadConfig(_xmlr.getDocument(), "", 1);
    }

    public XML2Map(String document, String[] filterPath, String[] dependencyPath) throws Exception {
        // update filter path before processing
        if (filterPath != null) {
            this._filterPath = filterPath;
        }

        // update dependency path before processing
        if (dependencyPath != null) {
            this._dependencyPath = dependencyPath;
        }

        _xmlr = new XMLReader(document);
        loadConfig(_xmlr.getDocument(), "", 1);
    }

    public XML2Map(org.w3c.dom.Node document) {
        loadConfig(document, "", 1);
    }

    public XML2Map(org.w3c.dom.Node document, String[] filterPath) {
        // update filter path before processing
        if (filterPath != null) {
            this._filterPath = filterPath;
        }

        loadConfig(document, "", 1);
    }

    public XML2Map(org.w3c.dom.Node document, String[] filterPath, String[] dependencyPath) {
        // update filter path before processing
        if (filterPath != null) {
            this._filterPath = filterPath;
        }

        // update dependency path before processing
        if (dependencyPath != null) {
            this._dependencyPath = dependencyPath;
        }

        loadConfig(document, "", 1);
    }

    public XML2Map(org.w3c.dom.Node document, int level) {
        loadConfig(document, "", level);
    }

    public XML2Map(org.w3c.dom.Node document, int level, String[] filterPath) {
        // update filter path before processing
        if (filterPath != null) {
            this._filterPath = filterPath;
        }

        loadConfig(document, "", level);
    }

    public XML2Map(org.w3c.dom.Node document, int level, String[] filterPath, String[] dependencyPath) {
        // update filter path before processing
        if (filterPath != null) {
            this._filterPath = filterPath;
        }

        // update dependency path before processing
        if (dependencyPath != null) {
            this._dependencyPath = dependencyPath;
        }

        loadConfig(document, "", level);
    }

    public Map<String, Object> getProperties() {
        return this._properties;
    }

    public Object getProperty(String key) {
        return getProperties().get(key);
    }

    protected void loadConfig(org.w3c.dom.Node parent, String nodePath, int level) {
        // retrieve the child nodes for processing
        ArrayList<org.w3c.dom.Node> nodes = _xmlr.getNodeChildren(parent);

        // parse the list of child nodes for the node being processed
        ArrayList<org.w3c.dom.Node> nAttributes = null;
        String nodeAttrKey = "";
        String nodePathHold = nodePath;

        if (nodes != null) {
            for (org.w3c.dom.Node node : nodes) {
                // adjust for special match on nodePath
                //if (this._childPath.length > 1) {
                //    if (nodePath.equals(this._childPath[0])) {
                //        System.out.println("2>" + _xmlr.getNodeValueByXPath(this._childPath[1], node));
                //        System.out.println("2>" + _xmlr.getNodeValueByXPath(this._childPath[2], node));
                //        System.out.println("2>" + _xmlr.getNodeValueByXPath(this._childPath[3], node));
                //    }
                //}

                nodePath += (nodePath.isEmpty() ? node.getNodeName() : "." + node.getNodeName());
                nAttributes = _xmlr.getNodeAttributes(node);
                //System.out.println(nodePath);

                // loop through the attributes array and append them to the
                // string builder object
                nodeAttrKey = "";
                if (nAttributes != null) {
                    // get id, name, class attribute if any
                    nodeAttrKey = getAttributeKey(nAttributes);

                    // get the key value for path
                    for (org.w3c.dom.Node na : nAttributes) {
                        if (na.getNodeName().equals(nodeAttrKey)) {
                            nodePath += "." + na.getNodeValue();
                            break;
                        }
                    }

                    // first get the key or if none; set it to first value
                    for (org.w3c.dom.Node na : nAttributes) {
                        // append the attribute details (key/text) to the string
                        // builder object
                        if (!na.getNodeName().equals(nodeAttrKey)) {
                            addProperty(nodePath + "." + na.getNodeName(), na.getNodeValue());
                            //System.out.println(nodePath + "." + na.getNodeName()
                            //        + "=" + na.getNodeValue());
                        }

                        // yield processing to other threads
                        Thread.yield();
                    }
                }

                // if node has a text value, display the text
                if (_xmlr.getNodeText(node) != null) {
                    addProperty(nodePath, _xmlr.getNodeText(node));
                    //System.out.println(nodePath
                    //        + "=" + _xmlr.getNodeText(node));
                }

                // recall the function (recursion) to see if the node has child 
                // nodes and preocess them in hierarchial level
                loadConfig(node, nodePath, (level + 1));
                nodePath = nodePathHold;

                // yield processing to other threads
                Thread.yield();
            }
        }
    }

    private String getAttributeKey(ArrayList<org.w3c.dom.Node> nodes) {
        String result = "";

        // attributes are in name order, so for class we keep looping
        for (org.w3c.dom.Node na : nodes) {
            if (na.getNodeName().equals("id")) {
                result = "id";
                break;
            } else if (na.getNodeName().equals("name")) {
                result = "name";
                break;
            } else if (na.getNodeName().equals("class")) {
                result = "class";
            }
        }

        return result;
    }

    public void addProperty(String key, Object value) {
        boolean dependency = false;
        
        // if value is null, exit
        if (value == null) {
            return;
        }

        // check the filterPath to ensure the key does not start with the filter
        if (this._filterPath.length > 0) {
            Boolean match = false;
            for (String filter : this._filterPath) {
                if (key.startsWith(filter)) {
                    match = true;
                    break;
                }
            }

            if (!match) {
                return;
            }
        }

        // check the filterPath to ensure the key does not start with the filter
        if (this._dependencyPath.length > 0) {
            for (String filter : this._dependencyPath) {
                if (key.startsWith(filter)) {
                    dependency = true;
                    break;
                }
            }
        }

        // add the key to the keymap for utilization
        if (getProperties().containsKey(key)) {
            if (dependency) {
                this._dependencyIndex++;
                getProperties().put(key + "_" + this._dupkeySuffix + "_" + this._dependencyIndex, value);
            } else {
                if (getProperties().containsKey(key + "_" + this._dupkeySuffix)) {
                    this._dupkeySuffix = randomUUID().toString();
                }

                this._dependencyIndex = 0;

                getProperties().put(key + "_" + this._dupkeySuffix, value);
            }
        } else {
            getProperties().put(key, value);
            this._dependencyIndex = 0;
        }
    }
}
