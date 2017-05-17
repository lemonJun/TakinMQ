package com.bj58.spat.esb.server.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ServiceConfig {
	
	private static Map<String, String> property = null;
	private static ServiceConfig instrance = new ServiceConfig();

	public static ServiceConfig getInstrance() {
		return instrance;
	}
	
	private ServiceConfig() {
		property = new HashMap<String, String>();
	}
	
	public void set(String key, String value) {
		property.put(key, value);
	}
	
	public String getString(String name) {
		return property.get(name);
	}
	
	public int getInt(String name) throws Exception {
		String value = property.get(name);
		if(value == null || value.equalsIgnoreCase("")) {
			throw new Exception("the property (" + name + ") is null");
		}
		return Integer.parseInt(value);
	}
	
	public boolean getBoolean(String name){
		boolean bool = false;
		String value = property.get(name);
		if(value == null || value.equalsIgnoreCase("")) {
			return false;
		}
		try{
			bool = Boolean.parseBoolean(value);
		}catch(Exception ex){
			bool =false;
		}
		
		return bool;
	}
	
	/**
	 * Load esb_config.xml
	 * @param paths
	 * @return
	 * @throws Exception
	 */
	public static ServiceConfig getServiceConfig(String path) throws Exception {
		ServiceConfig instance = ServiceConfig.getInstrance();
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression exprProperty = xpath.compile("//configuration/property");
		XPathExpression exprName = xpath.compile("name");
		XPathExpression exprValue = xpath.compile("value");
		
		File fServiceConfig = new File(path);
		if(!fServiceConfig.exists()) {
			throw new Exception("not find server config");
		}
		
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse(path);
		
		NodeList propertyNodes = (NodeList) exprProperty.evaluate(doc, XPathConstants.NODESET);
		for(int i=0; i<propertyNodes.getLength(); i++) {
			Node node = propertyNodes.item(i);
			Node nameNode = (Node) exprName.evaluate(node, XPathConstants.NODE);
			Node valueNode = (Node) exprValue.evaluate(node, XPathConstants.NODE);
			property.put(nameNode.getTextContent(), valueNode.getTextContent());
		}
		
		return instance;
	}

}
