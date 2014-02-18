package org.n3r.elastic.bean;

import org.n3r.core.lang.RBaseBean;

public class ElasticBean extends RBaseBean {

    private String id;

    private String key;

    private String date;

    private String contentEng;

    private String contentChs;

    private String state;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContentEng() {
        return contentEng;
    }

    public void setContentEng(String contentEng) {
        this.contentEng = contentEng;
    }

    public String getContentChs() {
        return contentChs;
    }

    public void setContentChs(String contentChs) {
        this.contentChs = contentChs;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
