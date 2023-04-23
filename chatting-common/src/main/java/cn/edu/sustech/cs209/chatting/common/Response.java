package cn.edu.sustech.cs209.chatting.common;

import java.io.OutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Response implements Serializable {

    @Serial
    private static final long serialVersionUID = 8552903078317069001L;

    private ResponseStatus status;

    private ResponseType type;

    private Map<String, Object> dataMap;


    private OutputStream outputStream;

    public Response() {
        this.status = ResponseStatus.OK;
        this.dataMap = new HashMap<String, Object>();
    }


    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public ResponseType getType() {
        return type;
    }

    public void setType(ResponseType type) {
        this.type = type;
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void setData(String name, Object value) {
        this.dataMap.put(name, value);
    }

    public Object getData(String name) {
        return this.dataMap.get(name);
    }

    public void removeData(String name) {
        this.dataMap.remove(name);
    }

    public void clearData() {
        this.dataMap.clear();
    }
}