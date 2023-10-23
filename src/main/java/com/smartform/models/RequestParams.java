package com.smartform.models;

import java.util.List;

import io.quarkus.panache.common.Page;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import lombok.Data;

@Data
public class RequestParams {

    @QueryParam("page")
    @DefaultValue("0")
    @Positive
    public int index;

    @QueryParam("size")
    @DefaultValue("50")
    @Positive
    public int size;

    @QueryParam("offset")
    @DefaultValue("00")
    @Positive
    public int offset;

    @QueryParam("fields")
    public List<String> fields;
    
    @QueryParam("order")
    public String order;
    
    @QueryParam("filter")
    public String filter;
    
    public Page toPage() {
        return Page.of(index, size);
    }

    @Override
    public String toString() {
        return "PageRequest{" +
                "page=" + index +
                ", size=" + size +
                '}';
    }
}