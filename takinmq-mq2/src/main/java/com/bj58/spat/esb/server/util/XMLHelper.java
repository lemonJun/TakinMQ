/*
 * Copyright 2010 58.com, Inc.
 * 
 * SPAT team blog: http://blog.58.com/spat/
 * website: http://www.58.com
 */
package com.bj58.spat.esb.server.util;

import java.io.File;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;

/**
 * XMLHelper
 *
 * @author Service Platform Architecture Team (spat@58.com)
 */
public class XMLHelper {

    public static Element GetXmlDoc(String filePath) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        }
        Document doc = null;
        try {
            File f = new File(filePath);
            if (db != null) {
                doc = db.parse(f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (doc != null) {
            return (Element) doc.getDocumentElement();
        }
        return null;
    }

    public static Node selectSingleNode(String express, Object source) {
        Node result = null;
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        try {
            result = (Node) xpath.evaluate(express, source, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static NodeList selectNodes(String express, Object source) {
        NodeList result = null;
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        try {
            result = (NodeList) xpath.evaluate(express, source, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return result;
    }
}
