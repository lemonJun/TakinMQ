package com.bj58.spat.esb.server.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.jboss.netty.channel.Channel;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.bj58.spat.esb.server.util.XMLHelper;

public class SubjectConfig {

    /**主题*/
    private int subjectID;

    /**主题对应ClientID*/
    private List<Client> clientID;

    /**
     * 获取主题-ClientID 配置文件
     * @param path
     * @return
     * @throws Exception
     */
    //	public static ConcurrentHashMap<Integer, List<Client>> getAllSubject(String path) throws Exception {
    //		File f = new File(path);
    //		if (!f.exists()) {
    //			throw new Exception("esb.subject.config not fond:" + path);
    //		}
    //
    //		ConcurrentHashMap<Integer, List<Client>> map = new ConcurrentHashMap<Integer, List<Client>>();
    //
    //		Element xmlDoc = XMLHelper.GetXmlDoc(path);
    //		XPathFactory xpathFactory = XPathFactory.newInstance();
    //		XPath xpath = xpathFactory.newXPath();
    //
    //		NodeList xnServers = (NodeList) xpath.evaluate("//Subject/subject", xmlDoc, XPathConstants.NODESET);
    //
    //		for (int i = 0; i < xnServers.getLength(); i++) {
    //			Node node = (Node) xnServers.item(i);
    //			if (node == null) {
    //				continue;
    //			}
    //			NamedNodeMap nnm = node.getAttributes();
    //			if (nnm != null) {
    //				String name = nnm.getNamedItem("name").getNodeValue();
    //				String clientID = nnm.getNamedItem("clientID").getNodeValue();
    //					
    //				if (name != null && !"".equals(name)) {
    //					List<Client> cIDList = new ArrayList<Client>();
    //					if (clientID != null) {
    //						String cID[] = clientID.split(",");
    //						for (int j = 0; j < cID.length; j++) {
    //							List<ESBChannel> list = SubjectFactory.getSubjectFactory().getClientChannelList(name+"_"+cID[j]);
    //							if(list != null && list.size() > 0){
    //								cIDList.add(new Client(Integer.valueOf(cID[j]),Integer.valueOf(name), list));
    //							}else{
    //								cIDList.add(new Client(Integer.valueOf(cID[j]),Integer.valueOf(name)));
    //							}
    //						}
    //						map.put(Integer.valueOf(name), cIDList);
    //					}
    //				}
    //			}
    //		}
    //
    //		return map;
    //	}

    public static ConcurrentHashMap<Integer, Subject> getAllSubjectMap(String path) throws Exception {
        File f = new File(path);
        if (!f.exists()) {
            throw new Exception("esb.subject.config not fond:" + path);
        }

        ConcurrentHashMap<Integer, Subject> map = new ConcurrentHashMap<Integer, Subject>();

        Element xmlDoc = XMLHelper.GetXmlDoc(path);
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();

        NodeList xnServers = (NodeList) xpath.evaluate("//Subject/subject", xmlDoc, XPathConstants.NODESET);

        for (int i = 0; i < xnServers.getLength(); i++) {
            Node node = (Node) xnServers.item(i);
            if (node == null) {
                continue;
            }
            NamedNodeMap nnm = node.getAttributes();
            if (nnm != null) {
                String name = nnm.getNamedItem("name").getNodeValue();
                String clientID = nnm.getNamedItem("clientID").getNodeValue();
                String isQueue = null;
                boolean bool = false;
                String abandonable = null;
                boolean bool2 = false;

                if (nnm.getNamedItem("isQueue") != null) {
                    isQueue = nnm.getNamedItem("isQueue").getNodeValue();
                }

                if (nnm.getNamedItem("abandonable") != null) {
                    abandonable = nnm.getNamedItem("abandonable").getNodeValue();
                }

                if (name != null && !"".equals(name)) {
                    List<Client> cIDList = new ArrayList<Client>();
                    if (clientID != null) {
                        String cID[] = clientID.split(",");
                        for (int j = 0; j < cID.length; j++) {
                            List<Channel> list = SubjectFactory.getSubjectFactory().getClientChannelList(name, Integer.parseInt(cID[j]));
                            if (list != null && list.size() > 0) {
                                cIDList.add(new Client(Integer.valueOf(cID[j]), Integer.valueOf(name), list));
                            } else {
                                cIDList.add(new Client(Integer.valueOf(cID[j]), Integer.valueOf(name)));
                            }
                        }

                        if (isQueue != null && "true".equals(isQueue.trim())) {
                            bool = true;
                        }

                        if (abandonable != null && "true".equals(abandonable.trim())) {
                            bool2 = true;
                        }

                        map.put(Integer.valueOf(name), new Subject(Integer.valueOf(name), cIDList, bool, bool2));
                    }
                }
            }
        }

        return map;
    }

    public int getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(int subjectID) {
        this.subjectID = subjectID;
    }

    public List<Client> getClientID() {
        return clientID;
    }

    public void setClientID(List<Client> clientID) {
        this.clientID = clientID;
    }

}