package cn.edu.sustech.cs209.chatting.common;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Request implements Serializable {

  @Serial
  private static final long serialVersionUID = 3347467515355760884L;

  private ResponseType type;

  private String action;

  private Map<String, Object> attributesMap;

  public Request() {
    this.attributesMap = new HashMap<String, Object>();
  }

  public ResponseType getType() {
    return type;
  }

  public void setType(ResponseType type) {
    this.type = type;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public Map<String, Object> getAttributesMap() {
    return attributesMap;
  }

  public Object getAttribute(String name) {
    return this.attributesMap.get(name);
  }

  public void setAttribute(String name, Object value) {
    this.attributesMap.put(name, value);
  }

  public void removeAttribute(String name) {
    this.attributesMap.remove(name);
  }

  public void clearAttribute() {
    this.attributesMap.clear();
  }
}