package com.kvlt.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ResultModel
 *
 * @author KVLT
 * @date 2019-04-13.
 */
public class ResultModel {

    private int id;
    private String content;

    public ResultModel() {

    }

    /**
     * read property from json string
     * @param id id
     * @param content data
     */
    public ResultModel(@JsonProperty("id") int id,
                       @JsonProperty("context") String content) {
        this.id = id;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
