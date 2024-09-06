package ru.clevertec.service;

import lombok.extern.slf4j.Slf4j;
import ru.clevertec.annotation.JsonField;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class ToJsonService {

    public String toJson(Object object) {
        Field[] declaredFields = object.getClass().getDeclaredFields();
        Map<String, Object> fieldMap = executeMapOfFieldsNameAndValue(object, declaredFields);

        String json = fieldMap.entrySet()
                .stream()
                .map(entry -> {
                    StringBuilder jsonBuilder = new StringBuilder();

                    if (entry.getValue() instanceof Number
                            || entry.getValue() instanceof Boolean
                            || "null".equals(entry.getValue())) {
                        return jsonBuilder.append("\"%s\":%s,".formatted(entry.getKey(), entry.getValue()));
                    } else if (entry.getValue() instanceof Collection<?> collection) {
                        return jsonBuilder.append("\"%s\":[%s],".formatted(entry.getKey(), collectionToJson(collection)));
                    } else if (entry.getValue() instanceof Map<?, ?> map) {
                        return jsonBuilder.append("\"%s\":{%s},".formatted(entry.getKey(), mapToJson(map)));
                    } else if (entry.getValue().getClass().isArray()) {
                        return jsonBuilder.append("\"%s\":[%s],".formatted(entry.getKey(),
                                arrayToJson(entry.getValue())));
                    } else if (!entry.getValue().getClass().getTypeName().startsWith("java")
                            && !(entry.getValue() instanceof Enum)) {
                        return jsonBuilder.append("\"%s\":%s,".formatted(entry.getKey(), toJson(entry.getValue())));
                    }
                    return jsonBuilder.append("\"%s\":\"%s\"," .formatted(entry.getKey(), entry.getValue()));
                })
                .collect(Collectors.joining("", "{", "}"));
        String finalJson = removeLastComma(json);
        log.info("toJson:\n{}", finalJson);
        return finalJson;
    }

    private Map<String, Object> executeMapOfFieldsNameAndValue(Object object, Field[] declaredFields) {
        return Arrays.stream(declaredFields)
                .collect(Collectors.toMap(
                        field -> {
                            JsonField annotation = field.getAnnotation(JsonField.class);
                            return annotation != null ? annotation.value() : field.getName();
                        },
                        field -> {
                            Object value = new Object();
                            try {
                                field.setAccessible(true);
                                value = field.get(object);
                            } catch (IllegalAccessException e) {
                                log.error(e.getMessage());
                            }
                            return Objects.requireNonNullElse(value, "null");
                        },
                        (v1, v2) -> v2,
                        LinkedHashMap::new
                ));
    }

    private String collectionToJson(Collection<?> objects) {
        return objects.stream()
                .map(o -> {
                    if (isObjectInstanceOfStringOrNumberOrBooleanOrEnum(o)) {
                        return isObjectInstanceOfStringOrEnum(o) ? "\"%s\"".formatted(o) : String.valueOf(o);
                    } else if (o instanceof Collection<?> collection) {
                        return "[%s]".formatted(collectionToJson(collection));
                    } else if (o.getClass().isArray()) {
                        return "[%s]".formatted(arrayToJson(o));
                    } else if (o instanceof Map<?, ?> map) {
                        return "{%s}".formatted(mapToJson(map));
                    }
                    return toJson(o);
                })
                .collect(Collectors.joining(","));
    }

    private String mapToJson(Map<?, ?> objectMap) {
        return objectMap.entrySet()
                .stream()
                .map(e -> {
                    if (isObjectInstanceOfStringOrNumberOrBooleanOrEnum(e.getValue())) {
                        return isObjectInstanceOfStringOrEnum(e.getValue())
                                ? "\"%s\":\"%s\"".formatted(e.getKey(), e.getValue())
                                : "\"%s\":%s".formatted(e.getKey(), e.getValue());
                    } else if (e.getValue() instanceof Collection<?> collection) {
                        return "\"%s\":[%s]".formatted(e.getKey(), collectionToJson(collection));
                    } else if (e.getValue() instanceof Map<?, ?> map) {
                        return "\"%s\":{%s}".formatted(e.getKey(), mapToJson(map));
                    } else if (e.getValue().getClass().isArray()) {
                        return "\"%s\":[%s]".formatted(e.getKey(), arrayToJson(e.getValue()));
                    }
                    return "\"%s\":%s".formatted(e.getKey(), toJson(e.getValue()));
                })
                .collect(Collectors.joining(","));
    }

    private String arrayToJson(Object value) {
        int length = Array.getLength(value);
        Object[] objects = new Object[length];
        for (int i = 0; i < length; i++) {
            Object o = Array.get(value, i);
            objects[i] = o;
        }
        return Arrays.stream(objects)
                .map(o -> {
                    if (isObjectInstanceOfStringOrNumberOrBooleanOrEnum(o)) {
                        return isObjectInstanceOfStringOrEnum(o) ? "\"%s\"".formatted(o) : String.valueOf(o);
                    } else if (o.getClass().isArray()) {
                        return "[%s]".formatted(arrayToJson(o));
                    }
                    return toJson(o);
                })
                .collect(Collectors.joining(","));
    }

    private boolean isObjectInstanceOfStringOrNumberOrBooleanOrEnum(Object o) {
        return o instanceof String || o instanceof Number || o instanceof Boolean || o instanceof Enum;
    }

    private boolean isObjectInstanceOfStringOrEnum(Object o) {
        return o instanceof String || o instanceof Enum;
    }

    private String removeLastComma(String json) {
        return new StringBuilder(json).replace(json.length() - 2, json.length() - 1, "").toString();
    }
}