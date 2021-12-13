package com.teamwill.leaseforge.qa.slim.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EditableBody {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final ObjectNode body;

    @SneakyThrows
    public EditableBody(String body) {
        this.body = (ObjectNode) mapper.readTree(body);
    }

    public void put(String att, String value) {
        body.put(att, value);
    }

    public void remove(String att) {
        body.remove(att);
    }

    public String asString() {
        return body.toPrettyString();
    }
}
