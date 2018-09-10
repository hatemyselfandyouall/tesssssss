package cn.ucmed.Util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class XmlLoader {
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger
            .getLogger(XmlLoader.class);

    public static void main(String[] args) {
        String s = "1";
        List<String> list = new ArrayList<>();
        list.add(s);
        list.add("2");
        list.add("3");
        list.add("4");
    }

    public static JSONObject xml2jsonObj(String xmls) {
        try {
            Element root = parseXmlString(xmls);
            return xml2j2(root);
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    public static JSONArray xml2jsonAry(String xmls) {
        try {
            Element root = parseXmlString(xmls);
            return xml2ary(root);
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    /**
     * 将XML添加到MAP中
     *
     * @param map
     * @param file
     */
    public static void addXml(Map<String, File> map, File file) {
        for (File f : file.listFiles()) {
            if (f.isDirectory()) {
                addXml(map, f);
            } else {
                if (f.getName().endsWith(".xml")) {
                    map.put(f.getName(), f);
                }
            }
        }
    }

    /**
     * 加载APIxml文件
     */
    public static void loadApiXml() {
        Map<String, File> map = new HashMap<>();
        String path = XmlLoader.class.getClassLoader().getResource("")
                .getPath()
                + "api-valid2/";
        File file = new File(path);
        if (!file.isDirectory()) {
            logger.debug(path);
        } else {
            addXml(map, file);
        }
        logger.debug(map.get("YH0010.xml").getAbsolutePath());
    }

    /**
     * fileTOstring
     *
     * @param file
     * @return
     */
    public static String readFile(File file) {
        StringBuilder sb = new StringBuilder();
        try (FileReader in = new FileReader(file)) {
            char[] bt = new char[1024];
            int temp;
            String str = null;
            while (-1 != (temp = in.read(bt))) {
                str = new String(bt, 0, temp);
                sb.append(str);
            }
        } catch (IOException e) {
            logger.debug(e);
        }
        return sb.toString();
    }

    public static void readXml(File file) {
        try {
            SAXReader saxReader = new SAXReader();
            Document doc = saxReader.read(file);
            Element root = doc.getRootElement();
            for (Iterator i = root.elementIterator(); i.hasNext(); ) {
                Element employee = (Element) i.next();
                for (Iterator j = employee.elementIterator(); j.hasNext(); ) {
                    Element node = (Element) j.next();
                    for (Iterator x = node.elementIterator(); x.hasNext(); ) {
                        Element xnode = (Element) x.next();
                        logger.debug(xnode.attributeValue("name"));
                    }
                    logger.debug(node.attributeValue("name"));
                    logger.debug(node.getName() + ":" + node.getText());
                }
            }
        } catch (DocumentException e) {
            logger.debug(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static void readXml2(File file) {
        try {
            SAXReader reader = new SAXReader();
            Document doc = reader.read(file);
            Element root = doc.getRootElement();
            List<Element> list = root.elements();
            for (Element child : list) {
                List<Element> els = child.elements();
                for (Element e : els) {
                    parseElement(e);
                }
            }
        } catch (DocumentException e) {
            logger.debug(e);
        }
    }

    public static void readXml3(File file) {
        try {
            SAXReader reader = new SAXReader();
            Document doc = reader.read(file);
            Element root = doc.getRootElement();
            parseElement(root);
        } catch (DocumentException e) {
            logger.debug(e);
        }
    }

    public static void parseElement(Element e) {
        logger.debug(e.attributeValue("name") + " : "
                + e.attributeCount() + " : " + e.getName());
        List<Element> list = e.elements();
        if (list.isEmpty()) {
            List<Attribute> asList = e.attributes();
            JSONObject obj = new JSONObject();
            for (Attribute a : asList) {
                obj.put(a.getName(), a.getValue());
            }
            logger.debug(obj.toString());
        } else {
            for (Element element : list) {
                parseElement(element);
            }
        }
    }

    public static void parseXmlRoot(Element e) {
        logger.debug(e.getName() + " : " + e.getText());
    }

    /**
     * 解析xml字符串
     */
    public static Element parseXmlString(String xmlString) {
        Document doc = null;
        Element root = null;
        try {
            doc = DocumentHelper.parseText(xmlString);
            root = doc.getRootElement();
        } catch (DocumentException e) {
            logger.debug(e);
        }
        return root;
    }

    /**
     * xml 转 json
     *
     * @param root
     * @return
     */
    public static JSONObject xml2JSON(Element root) {
        JSONObject obj = null;
        if (null != root) {
            obj = new JSONObject();
            logger.debug(root.isRootElement());
            List<Element> elist = root.elements();
            logger.debug(elist.size());
            if (!elist.isEmpty()) { // 判断子节点数
                for (Element e : elist) {
                    if (isAttr(e)) {
                        obj.put(e.getName(), e.getText());
                    } else {
                        addArray(obj, e);
                    }
                }
            }
        }
        return obj;
    }

    // 1  判断当前结点是否有子节点
    // 2 判断当前结点的属性 object array
    public static void xml2j(JSONObject obj, Element ele) {
        logger.debug(ele.isRootElement());
        List<Element> elist = ele.elements();
        logger.debug(elist.size());
        if (!elist.isEmpty()) { // 判断子节点数
            for (Element e : elist) {
                if (isAttr(e)) { // 是否为属性
                    obj.put(e.getName(), e.getText());
                } else if (isObj(e)) { // 是否为OBJ对象
                } else { // 是否为array
                    addArray(obj, e);
                }
            }
        }
    }

    public static void addArray(JSONObject obj, Element ele) {
        List<Element> elist = ele.elements();
        if (ele.getName().toLowerCase().contains("list")) {
            JSONArray ary = new JSONArray();
            for (Element e : elist) {
                ary.add(list2JSON(e.elements()));
            }
            obj.put(ele.getName(), ary);
        } else {
            obj.put(ele.getName(), list2JSON(elist));
        }
    }

    public static JSONObject list2JSON(List<Element> list) {
        JSONObject obj = new JSONObject();
        for (Element e : list) {
            obj.put(e.getName(), e.getText());
        }
        return obj;
    }

    public static JSONArray list2Ary(List<Element> list) {
        return new JSONArray();
    }

    /**
     * 是否为属性值
     * 判断是否为最后一个节点
     */
    public static boolean isAttr(Element e) {
        @SuppressWarnings("unchecked")
        List<Element> elist = e.elements();
        return elist.isEmpty();
    }

    /**
     * 判断是否为 对象
     *
     * @param e
     * @return
     */
    public static boolean isObj(Element e) {
        List<Element> elist = e.elements();
        if (elist.size() == 1 && isAttr(elist.get(0))) {
            return true;
        } else if (elist.size() > 1) {
            String name1 = elist.get(0).getName();
            String name2 = elist.get(1).getName();
            if (!name1.equals(name2)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAry(Element e) {
        List<Element> elist = e.elements();
        if (elist.size() > 1) {
            String name1 = elist.get(0).getName();
            String name2 = elist.get(1).getName();
            if (!name1.equals(name2)) {
                return true;
            }
        }
        return false;
    }

    /**
     * XML转换成josn
     *
     * @param ele
     * @return
     */
    public static JSONObject xml2j2(Element ele) {
        JSONObject obj = new JSONObject();
        List<Element> elist = ele.elements();
        if (!elist.isEmpty()) { // 判断子节点数
            for (Element e : elist) {
                if (isAttr(e)) { // 是否为属性
                    obj.put(e.getName(), e.getText());
                } else if (isObj(e)) { // 是否为OBJ对象
                    if (isObjAry(e, elist)) {
                        obj = putAry(obj, e);
                    } else {
                        obj.put(e.getName(), xml2j2(e));
                    }
                } else { // 是否为array
                    obj.put(e.getName(), xml2ary(e));
                }
            }
        }
        return obj;
    }

    /**
     * 转换成ary
     *
     * @param ele
     * @return
     */
    public static JSONArray xml2ary(Element ele) {
        JSONArray ary = new JSONArray();
        List<Element> elist = ele.elements();
        for (Element e : elist) {
            ary.add(xml2j2(e));
        }
        return ary;
    }

    public static JSONObject putAry(JSONObject obj, Element e) {
        JSONArray ary = null;
        if (null != obj.opt(e.getName())) {
            ary = obj.optJSONArray(e.getName());
            logger.debug(obj.opt(e.getName()));
        }
        logger.debug(obj.toString());
        if (null == ary) {
            ary = new JSONArray();
        }
        ary.add(xml2j2(e));
        obj.put(e.getName(), ary);
        return obj;
    }

    public static boolean isObjAry(Element e, List<Element> elist) {
        boolean falg = false;
        if (elist.size() > 1 && elist.size() > elist.indexOf(e)) {
            String name = e.getName();
            String name2 = "";
            if ((elist.size() - 1) == elist.indexOf(e)) {
                name2 = elist.get(elist.indexOf(e) - 1).getName();
            } else {
                name2 = elist.get(elist.indexOf(e) + 1).getName();
            }
            if (name.equals(name2)) {
                falg = true;
            }
        }
        return falg;
    }

    public static String getMsgByXML(String xml, String start, String end) {
        int st = xml.indexOf(start);
        int en = xml.indexOf(end);
        return xml.substring(st + start.length(), en);
    }

    public static String getMsgByXML2(String xml, String start, String end) {
        int st = xml.indexOf(start);
        int en = xml.indexOf(end);
        return xml.substring(st, en + end.length());
    }
}
