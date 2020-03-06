package org.r.base.common.utils;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zj on 2019/3/27.
 */
public class XMLUtils {

    /**
     * 源字符串
     */
    private String content;

    /**
     * 把源字符串解析出来的对象
     */
    private Document doc;

    /**
     * 编码
     */
    private String charSet = "UTF-8";

    public XMLUtils(String content) {
        this.content = content;
        SAXBuilder sb = new SAXBuilder();
        try {
            this.doc = sb.build(new ByteArrayInputStream(content.getBytes(charSet)));
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
        }
    }

    public Element getElement(Element parent, String name) {
        List elements = this.getElements(parent, name);
        if (elements.size() != 1) return null;
        return (Element) elements.get(0);
    }

    public List getElements(Element parent, String name) {

        return parent.getChildren(name);
    }

    public Element getRootElement() {
        if (this.doc == null) {
            return null;
        }
        return this.doc.getRootElement();
    }

    public String getText(Element startEle, String eleName) {
        List<String> texts = getTexts(startEle, eleName);
        if (texts == null || texts.size() != 1) return "";
        return texts.get(0);
    }


    private List<String> getTexts(Element startEle, String eleName) {
        List children = startEle.getChildren();
        List<String> resList = new ArrayList<>();
        for (Object child : children) {
            Element ele = (Element) child;
            if (eleName.equalsIgnoreCase(ele.getName())) {
                resList.add(ele.getText());
            }
            List subChildren = ele.getChildren();
            if (subChildren.size() == 0) {
                continue;
            }
            resList.addAll(this.getTexts(ele, eleName));
        }
        return resList;
    }

    /**
     * 以xml节点名称作为key转化成map
     *
     * @return map
     */
    public Map<String, Object> toMap() {
        return toMap(getRootElement());
    }

    /**
     * 获取指定节点名的下的所有参数的map
     *
     * @param nodeName
     * @return
     */
    public Map<String, Object> toMap(String nodeName) {
        Element element = getElement(getRootElement(), nodeName);
        return toMap(element);
    }

    /**
     * 获取指定节点的下的所有参数的map
     *
     * @param root
     * @return
     */
    private Map<String, Object> toMap(Element root) {
        if (root == null) {
            return new HashMap<>(1);
        }
        List children = root.getChildren();
        return toMap(children);
    }


    /**
     * 递归构造map
     *
     * @param list 数据源
     * @return
     */
    private Map<String, Object> toMap(List list) {
        if (CollectionUtils.isEmpty(list)) {
            return new HashMap<>(1);
        }
        Map<String, Object> result = new HashMap<>(10);
        for (Object child : list) {
            Element ele = (Element) child;
            List children = ele.getChildren();
            if (CollectionUtils.isEmpty(children)) {
                result.put(ele.getName(), ele.getText());
            } else {
                result.put(ele.getName(), toMap(children));
            }
        }
        return result;
    }
}
