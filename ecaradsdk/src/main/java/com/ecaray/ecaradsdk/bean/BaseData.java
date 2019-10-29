package com.ecaray.ecaradsdk.bean;

import java.io.Serializable;
import java.util.List;

public class BaseData implements Serializable {
    private  String  message;
    private  String  runtime;
    private  String   state;
    private  String   ts;

    private   ResponseData data;

    private List<ResponseData> datas;

    public List<ResponseData> getDatas() {
        return datas;
    }

    public void setDatas(List<ResponseData> datas) {
        this.datas = datas;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public ResponseData getData() {
        return data;
    }

    public void setData(ResponseData data) {
        this.data = data;
    }
}
