package com.teamwill.leaseforge.qa.slim;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import lombok.SneakyThrows;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class AbstractScript {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");
    private Configuration ftlConfig;
    private RandomDataProvider randomDataProvider;
    private ContextHolderWrapper contextHolderWrapper;

    public void setAs(String varName, String value) {
        ContextHolder.INSTANCE.put(varName, value);
    }

    public void ifValueOfIsThenSetAs(String varToTest, String valueToTest, String varToSet, String valueToSet) {
        String value = ContextHolder.INSTANCE.get(varToTest);
        if (value == null) {
            return;
        }
        if (value.equals(valueToTest)) {
            ContextHolder.INSTANCE.put(varToSet, valueToSet);
        }
    }

    public String contextValueFor(String key) {
        return ContextHolder.INSTANCE.get(key);
    }

    public void storeIn(String value, String key) {
        ContextHolder.INSTANCE.put(key, value);
    }

    @SneakyThrows
    public String pageContentRelativeTo(String page, String relativeTo) {
        relativeTo = relativeTo.replaceAll("\\<.*?\\>", "");
        relativeTo = relativeTo.replace("[?]", "");
        relativeTo = relativeTo.replace(".", "/");
        File parentDir = new File(relativeTo);

        page = page.replaceAll("\\<.*?\\>", "");
        if (page.startsWith("&gt;")) {
            page = page.substring("&gt;".length());
        }
        String filePath = parentDir.getPath() + "/" + page + ".wiki";
        File file = findFile(filePath);
        if (file == null) {
            return filePath + " not found";
        }

        String pageContent = Files.readString(file.toPath());
        return pageContent.substring(pageContent.indexOf("==JSON==") + "==JSON==".length());
    }

    @SneakyThrows
    File findFile(String filePath) {
        File root = new File(".");
        try (Stream<Path> walk = Files.walk(root.toPath())) {
            Optional<Path> fileName = walk
                    .filter(path -> path.endsWith(filePath))
                    .findFirst();
            if (fileName.isPresent()) {
                return fileName.get()
                               .toFile();
            }
        }
        return null;
    }

    @SneakyThrows
    public String merge(String templateBody) {
        Map<String, Object> vars = ContextHolder.INSTANCE.allContextData();
        Template template = new Template("name", new StringReader(templateBody),
                getFtlConfiguration());
        vars.put("random", randomDataProvider);
        vars.put("context", contextHolderWrapper);
        vars.put("today", formatter.format(LocalDate.now()));
        StringWriter stringWriter = new StringWriter();
        template.process(vars, stringWriter);
        return stringWriter.toString()
                           .trim();
    }

    private Configuration getFtlConfiguration() {
        if (ftlConfig == null) {
            ftlConfig = new Configuration(Configuration.VERSION_2_3_31);
            ftlConfig.setDefaultEncoding("UTF-8");
            ftlConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

            randomDataProvider = new RandomDataProvider();
            contextHolderWrapper = new ContextHolderWrapper();
        }
        return ftlConfig;
    }

    public String evaluate(JsonNode node, String key) {
        JsonNode child = find(node, key);
        return child == null ? "" : child.asText();
    }

    public JsonNode find(JsonNode node, String key) {
        int pos = key.indexOf(".");
        if (pos < 1) {
            return getAttribute(node, key);
        }
        String localKey = key.substring(0, pos);
        JsonNode child = getAttribute(node, localKey);
        return child == null ? null : find(child, key.substring(pos + 1));
    }

    private JsonNode getAttribute(JsonNode node, String key) {
        if (key.contains("[")) {
            int deb = key.indexOf("[");
            int end = key.indexOf("]");
            String indexAsTxt = key.substring(deb + 1, end);
            String reducedKey = key.substring(0, deb);
            JsonNode child = node.get(reducedKey);
            if (child instanceof ArrayNode) {
                int index = Integer.parseInt(indexAsTxt);
                return child.get(index);
            } else {
                return null;
            }
        } else {
            return node.get(key);
        }
    }

    public static class ContextHolderWrapper {
        public String get(String key) {
            return ContextHolder.INSTANCE.get(key);
        }
    }
}
