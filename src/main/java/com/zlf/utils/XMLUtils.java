package com.zlf.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.alibaba.druid.util.StringUtils;

public class XMLUtils {
	public static final Logger logger=LoggerFactory.getLogger(CommonResponse.class);
		
    private DocumentBuilder builder;  
    
    private XPath xpath;  
      
    private XMLUtils () {  
        init();  
    }  
      
    private static class SingletonHolder {    
        private static final XMLUtils INSTANCE = new XMLUtils();    
    }    
  
    public static final XMLUtils getInstance() {    
        return SingletonHolder.INSTANCE;   
    }    
    /**
     * 创建Document对象，和测试xml结果的xmlpath初始化
     */
    private void init() {  
        if(builder == null) {  
            DocumentBuilderFactory domfactory = DocumentBuilderFactory  
                    .newInstance();  
            domfactory.setValidating(false);
            try {
				domfactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			} catch (ParserConfigurationException e1) {
				throw new RuntimeException(e1);
			}
            domfactory.setIgnoringComments(true);  
            try {  
                builder = domfactory.newDocumentBuilder();  
            } catch (ParserConfigurationException e) {  
                throw new RuntimeException(  
                        "Create DocumentBuilder instance occur one exception.");  
            }  
        }  
          
        if(xpath == null) {  
            XPathFactory xpfactory = XPathFactory.newInstance();  
            xpath = xpfactory.newXPath();  
        }  
    }  
  
    /**
     * Document转String.
     * @param doc
     * @return  String 类型的XML
     */
    public String document2String(Document doc) {  
        DOMSource domSource = new DOMSource(doc);  
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(bos);  
        TransformerFactory tf = TransformerFactory.newInstance();  
        Transformer transformer;  
        try {  
            transformer = tf.newTransformer();  
            transformer.transform(domSource, result);  
        } catch (TransformerException e) {  
            throw new RuntimeException(  
                "Transformer org.w3c.dom.document object occur one exception.");  
        }  
        return bos.toString();  
    }  
  
    /** 
     * @Description: 根据XML路径解析XML文档 
     * @param path 
     * @return Document 
     * @throws 
     */  
    public Document parseDocument(String path) {  
        try {  
            return builder.parse(path);  
        } catch (SAXException e) {  
            throw new RuntimeException(  
                    "The xml path is invalid or parsing xml occur exception.");  
        } catch (IOException e) {  
            throw new RuntimeException(  
                    "The xml path is invalid or parsing xml occur exception.");  
        }  
    }  
  
    /** 
     * @Description: 根据文件解析XML文档 
     * @param file 
     * @return Document 
     * @throws 
     */  
    public Document parseDocument(File file) {  
        try {  
            return builder.parse(file);  
        } catch (SAXException e) {  
            throw new RuntimeException(  
                    "The input xml file is null or parsing xml occur exception.");  
        } catch (IOException e) {  
            throw new RuntimeException(  
                    "The input xml file is null or parsing xml occur exception.");  
        }  
  
    }  
  
    /** 
     * @Description: 根据输入流解析XML文档 
     * @param is 
     * @throws IOException 
     * @throws SAXException 
     * @return Document 
     * @throws 
     */  
    public Document parseDocumentFromInputStream(InputStream is) {  
        try {  
            return builder.parse(is);  
        } catch (SAXException e) {  
            throw new RuntimeException(  
                    "The input xml fileInputStream is null or parsing xml occur exception.");  
        } catch (IOException e) {  
            throw new RuntimeException(  
                    "The input xml fileInputStream is null or parsing xml occur exception.");  
        }  
    }  
  
    
    
    
    /** 
     * @Description: 将html代码片段转换成document对象 
     * @param @param fragment 
     * @return Document 
     * @throws 
     */  
    public Document fragment2Document(String fragment) {  
        try {  
            return builder.parse(new InputSource(new StringReader(fragment)));  
        } catch (SAXException e) {  
            throw new RuntimeException(  
                    "parse fragment to document occur SAXException,please check your fragment.");  
        } catch (IOException e) {  
            throw new RuntimeException(  
                    "parse fragment to document occur one IOException.");  
        }  
    }  
  
    /** 
     * @Description: 通过xpath获取节点列表 
     * @param node 
     * @param expression 
     * @throws XPathExpressionException 
     * @return NodeList 
     * @throws 
     */  
    public NodeList selectNodes(Node node, String expression) {  
        XPathExpression xpexpreesion = null;  
        try {  
            xpexpreesion = this.xpath.compile(expression);  
            return (NodeList) xpexpreesion.evaluate(node,  
                    XPathConstants.NODESET);  
        } catch (XPathExpressionException e) {  
            throw new RuntimeException(  
                    "Compile xpath expression occur excetion,please check out your xpath expression.");  
        }  
    }  

    /**
     * 将老节点的所有子节点作为doc下的一个新节点的子节点
     * @param doc  
     * @param newNode 新的父节点
     * @param oldNode 老的父节点
     * @return
     */
     public  Document addOldNodesChildNodeListToNewNode(Document doc,Node newNode,Node oldNode){
    	NodeList childNodes = oldNode.getChildNodes();
    	if(childNodes.getLength()>0){
    		for (int i = 0; i <childNodes.getLength(); i++) {
    			Node node = childNodes.item(i);
//    			这个老的父节点的子节点不能还是父节点了，这些子节点是最小一维子节点。
    			if(node.getNodeName().equals("#text")||StringUtils.isEmpty(node.getTextContent())||node.getChildNodes().getLength()>1){
    				continue;
    			}else{
    				Element element =doc.createElement(node.getNodeName());
    				element.setTextContent(node.getTextContent());
    				newNode.appendChild(element);
    			}
    			
			}
    	}
    	return doc;
    }
   /**
    * 查询出对应名字的子节点
    * @param parentNode
    * @param childName
    * @return
    */
     public Node findChild(Node parentNode,String childName){
    	 NodeList childNodes = parentNode.getChildNodes();
    	 for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if(node.getNodeName().equals(childName)){
				return node;
			}
		}
    	 return null;
     }
     
     /**
      * 获取节点下的指定节点名的节点值
      * @param parentNode 指定节点
      * @param nodeName   节点名
      * @return
      */
     public String getPointNodeNamesTextContentValue(Node parentNode,String nodeName){
    	 if(parentNode ==null){
    		 return "";
    	 }
    	 NodeList childNodes = parentNode.getChildNodes();
    	 for (int i = 0; i < childNodes.getLength(); i++) {
    		 Node item = childNodes.item(i);
    		 if(item.getNodeName().equals(nodeName)){
    			 String textContent = item.getTextContent();
    			 if(StringUtils.isEmpty(textContent)){
    				 return "";
    			 }else{
    				 return item.getTextContent().trim();
    			 }
    		 }
    	 }
    	 return "";
     }
     
     /**
      * 获取document下第一个指定节点元素的值
      * @param document
      * @param nodeName
      * @return
      */
     public String getNodeValue(Document document,String nodeName){
    	 Node node = document.getElementsByTagName(nodeName).item(0);
    	 if(node ==null){
    		 return "";
    	 }
    	 String value = node.getTextContent();
    	 if(StringUtils.isEmpty(value)){
    		 value=value.trim();
    		 return value;
    	 }else{
    		 return "";
    	 }
     }
     
     
     
     /**
      * 向目标节点/元素添加新的元素并赋值
      * @param document
      * @param fatherNode 指定节点
      * @param nodeName	  新添加的节点名
      * @param nodeValue 新添加的节点值
      */
     public void addElementToPointNode(Document document,Node fatherNode,String nodeName,String nodeValue){
    	Element element=document.createElement(nodeName);
    	if(nodeValue !=null){
    		element.setTextContent(nodeValue);
    	}
    	 fatherNode.appendChild(element);
     }
   
    /** 
     * @Description: 通过xpath获取单个节点 
     * @param node 
     * @param expression 
     * @return Node 
     * @throws 
     */  
    public Node selectSingleNode(Node node, String expression) {  
        XPathExpression xpexpreesion = null;  
        try {  
            xpexpreesion = this.xpath.compile(expression);  
            return (Node) xpexpreesion.evaluate(node, XPathConstants.NODE);  
        } catch (XPathExpressionException e) {  
            throw new RuntimeException(  
                    "Compile xpath expression occur excetion,please check out your xpath expression.");  
        }  
    }  
  
    /** 
     * @Description: 根据xpath获取节点的文本值(只返回匹配的第一个节点的文本值) 
     * @param node 
     * @param expression 
     * @return String 
     * @throws 
     */  
    public String getNodeText(Node node, String expression) {  
        XPathExpression xpexpreesion = null;  
        try {  
            xpexpreesion = this.xpath.compile(expression);  
            return (String) xpexpreesion.evaluate(node, XPathConstants.STRING);  
        } catch (XPathExpressionException e) {  
            throw new RuntimeException(  
                    "Compile xpath expression occur excetion,please check out your xpath expression.");  
        }  
    }  
      
    /** 
     * @Description: 根据xpath获取节点的文本值(若xpath表达式匹配到多个节点,则会提取所有匹配到节点的文本值) 
     * @param @param node 
     * @param @param expression 
     * @return List<String> 
     * @throws 
     */  
    public List<String> getMultiNodeText(Node node, String expression) {  
        NodeList nodeList = selectNodes(node, expression);  
        if(null == nodeList || nodeList.getLength() == 0) {  
            return null;  
        }  
        List<String> list = new ArrayList<String>();  
        for(int i=0; i < nodeList.getLength(); i++) {  
            Node n = nodeList.item(i);  
            String text = n.getTextContent();  
            list.add(text);  
        }  
        return list;  
    }  
      
       /** 
     * @Description: 根据xpath获取节点的属性值(若xpath表达式匹配到多个节点,则只会提取匹配到的第一个节点的属性值) 
     * @param @param node 
     * @param @param expression 
     * @param @param atrributeName 
     * @return String 
     * @throws 
     */  
    public String getNodeAttributeValue(Node node,  
            String expression, String atrributeName) {  
        Node matchNode = selectSingleNode(node, expression);  
        if (null == matchNode) {  
            return null;  
        }  
        Node attNode = matchNode.getAttributes().getNamedItem(  
                atrributeName);  
        if (null == attNode) {  
            return null;  
        }  
        return attNode.getNodeValue();  
    }  
    /** 
     * @Description: 根据xpath获取节点的属性值(若xpath表达式匹配到多个节点,则会提取所有匹配到节点的属性值) 
     * @param  expression      Xpath表达式,如div\span[@class] 
     * @param  atrributeName   属性名称 
     * @return List<String> 
     * @throws 
     */  
    public List<String> getMultiNodeAttributeValue(Node node, String expression,String atrributeName) {  
        NodeList nodeList = selectNodes(node, expression);  
        if(null == nodeList || nodeList.getLength() == 0) {  
            return null;  
        }  
        List<String> list = new ArrayList<String>();  
        for(int i=0; i < nodeList.getLength(); i++) {  
            Node currentItem = nodeList.item(i);  
            Node attNode = currentItem.getAttributes().getNamedItem(atrributeName);  
            if(null == attNode) {  
                continue;  
            }  
            String val = currentItem.getAttributes().getNamedItem(atrributeName).getNodeValue();  
            list.add(val);  
        }  
        return list;  
    }  
  
    
    
    public static void main(String[] args) throws ParserConfigurationException,  
            SAXException, IOException {  
  
        /*String fragment = "<data><employee><name>益达</name>" 
                + "<title>Manager</title></employee></data>"; 
 
        XMLUtils util = new XMLUtils(); 
        Document doc = util.fragment2Document(fragment); 
        NodeList nodes = doc.getElementsByTagName("employee"); 
 
        for (int i = 0; i < nodes.getLength(); i++) { 
            Element element = (Element) nodes.item(i); 
 
            NodeList name = element.getElementsByTagName("name"); 
            Element line = (Element) name.item(0); 
            System.out.println("Name: " + line.getNodeName() + ":" 
                    + line.getTextContent()); 
 
            NodeList title = element.getElementsByTagName("title"); 
            line = (Element) title.item(0); 
            System.out.println("Name: " + line.getNodeName() + ":" 
                    + line.getTextContent()); 
        }*/  
          
//        String fragment = "<data><employee><name id=\"1\">益达</name><name id=\"2\">yida</name>"  
//                + "<title>Manager</title></employee></data>";  
//  
//        XMLUtils util = new XMLUtils();  
//        Document doc = util.fragment2Document(fragment);  
//          
//          
//        List<String> strList = util.getMultiNodeText(doc, "//employee/name[@id]");  
//        String s = GerneralUtils.joinCollection(strList);  
//        System.out.println(s);  
//          
//        strList = util.getMultiNodeAttributeValue(doc, "//employee/name[@id]", "id");  
//        s = GerneralUtils.joinCollection(strList);  
//        System.out.println(s);  
    } 
	
	
	
}
