package ru.clevertec.service;

import lombok.extern.slf4j.Slf4j;
import ru.clevertec.annotation.JsonField;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class FromJsonService {

    public Object toObject(String json, Class<?> clazz) {
        Map<String, String> jsonMap = executeMapOfFieldsNameAndValue(json);
        Object object = getNewInstanceOfConstructor(clazz);

        return jsonMap.entrySet()
                .stream()
                .map(entry -> {
                    try {
                        Field declaredField = getFieldByJsonKey(clazz, entry.getKey());
                        Object parsedObject = getParsedObject(entry.getValue(), declaredField);

                        declaredField.setAccessible(true);
                        declaredField.set(object, parsedObject);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        log.error(e.getMessage());
                    }
                    return object;
                })
                .reduce((o, o2) -> o2)
                .orElseThrow(() -> new RuntimeException("Something went wrong!"));
    }

    private Field getFieldByJsonKey(Class<?> clazz, String jsonKey) throws NoSuchFieldException {
        for (Field field : clazz.getDeclaredFields()) {
            JsonField annotation = field.getAnnotation(JsonField.class);
            if (annotation != null && annotation.value().equals(jsonKey)) {
                return field;
            } else if (field.getName().equals(jsonKey)) {
                return field;
            }
        }
        throw new NoSuchFieldException("No field found for JSON key: " + jsonKey);
    }

    private Map<String, String> executeMapOfFieldsNameAndValue(String json) {
        return getParsedJsonByPattern(json).stream()
                .map(s -> s.endsWith(",") ? s.substring(0, s.length() - 1) : s)
                .map(s -> s.split(":", 2))
                .collect(Collectors.toMap(
                        strings -> strings[0].replaceAll("^\"|\"$", ""),
                        strings -> {
                            String value = strings[1];
                            if (value.startsWith("{") && value.endsWith("}")) {
                                return value;
                            } else {
                                return value.replaceAll("^\"|\"$", "");
                            }
                        },
                        (v1, v2) -> v2,
                        LinkedHashMap::new
                ));
    }

    private List<String> getParsedJsonByPattern(String json) {
        List<String> keyValueList = new ArrayList<>();
        Pattern pattern = Pattern
                .compile("((?=\\[)\\[[^]]*]|(?=\\{)\\{[^}]*}|\"[^\"]*\"|(?=\\d)\\d*.\\d*|(?=\\w)\\w*|(?=\\b[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}\\b))" +
                        ":+((?=\\[)\\[[^]]*]|(?=\\{)\\{[^}]*}|\"[^\"]*\"|(?=\\d)\\d*.\\d*|(?=\\w)\\w*|(?=\\b[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}\\b|(?=\\b[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}\\b)))");
        Matcher matcher = pattern.matcher(json);
        while (matcher.find()) {
            keyValueList.add(matcher.group());
        }
        return keyValueList;
    }

    private Object getNewInstanceOfConstructor(Class<?> clazz) {
        Object object = null;
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            object = constructor.newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            log.error(e.getMessage());
        }
        return object;
    }

    private Object getParsedObject(String value, Field declaredField) {
        Class<?> type = declaredField.getType();
        if (type.getSimpleName().startsWith("boolean") || type.getName().startsWith("java.lang.Boolean")) {
            return Boolean.valueOf(value);
        } else if (type.getName().equals("java.util.UUID")) {
            return UUID.fromString(value);
        } else if (type.isPrimitive() || (type.getSuperclass() != null
                && type.getSuperclass().getName().endsWith("java.lang.Number"))) {
            return getParsedNumber(value, type);
        } else if (type.getName().startsWith("java.lang.String")) {
            return value;
        } else if (Collection.class.isAssignableFrom(type)) {
            return getParsedCollection(value, declaredField);
        } else if (type.getName().endsWith("java.util.Map")) {
            return getParsedMap(value, declaredField);
        } else if (type.isEnum()) {
            return getParsedEnum(value, declaredField);
        } else if (type.isArray()) {
            Object[] objects = value.lines()
                    .map(s -> s.replace("[", ""))
                    .map(s -> s.replace("]", ""))
                    .map(s -> s.split(","))
                    .flatMap(Arrays::stream)
                    .toArray();
            if (type.getSimpleName().startsWith("double")) {
                return Arrays.stream(objects)
                        .mapToDouble(s -> Double.parseDouble((String) s))
                        .toArray();
            } else if (type.getSimpleName().startsWith("long")) {
                return Arrays.stream(objects)
                        .mapToLong(s -> Long.parseLong((String) s))
                        .toArray();
            }
            return objects;
        } else if (type.getName().equals("java.time.LocalDate")) {
            return LocalDate.parse(value);
        } else {
            return toObject(value, type);
        }
    }

    private Object getParsedNumber(String value, Class<?> type) {
        return switch (type.getSimpleName()) {
            case "Integer", "int" -> Integer.parseInt(value);
            case "Long", "long" -> Long.parseLong(value);
            case "BigDecimal" -> new BigDecimal(value);
            case "BigInteger" -> new BigInteger(value);
            case "Byte", "byte" -> Byte.parseByte(value);
            case "Short", "short" -> Short.parseShort(value);
            case "Float", "float" -> Float.parseFloat(value);
            default -> Double.parseDouble(value);
        };
    }

    private LinkedHashMap<Object, Integer> getParsedMap(String value, Field declaredField) {
        return value.lines()
                .map(s -> s.replace("{", ""))
                .map(s -> s.replace("}", ""))
                .map(s -> s.split(","))
                .flatMap(Arrays::stream)
                .map(s -> s.split(":"))
                .collect(Collectors.toMap(
                        strings -> getParsedGeneric(declaredField, strings[0].replaceAll("^\"|\"$", "")),
                        strings -> Integer.parseInt(strings[1].replaceAll("^\"|\"$", "")),
                        (v1, v2) -> v2,
                        LinkedHashMap::new
                ));
    }

    private Collection<Object> getParsedCollection(String value, Field declaredField) {
        Class<?> type = declaredField.getType();
        if (type.getName().startsWith("java.util.List")) {
            return getParsedCollectionStream(value, declaredField)
                    .toList();
        } else if (type.getName().startsWith("java.util.Set")) {
            return getParsedCollectionStream(value, declaredField)
                    .collect(Collectors.toSet());
        }
        return Collections.emptyList();
    }

    private Stream<Object> getParsedCollectionStream(String value, Field declaredField) {
        return Arrays.stream(value.split("(?<=}),\\s*(?=\\{)"))
                .map(s -> s.replace("[", "").replace("]", ""))
                .map(s -> getParsedGeneric(declaredField, s));
    }

    private Object getParsedGeneric(Field declaredField, String s) {
        if (declaredField.getGenericType() instanceof ParameterizedType type) {
            Class<?> generic = (Class<?>) type.getActualTypeArguments()[0];
            if (Number.class.isAssignableFrom(generic)) {
                return getParsedNumber(s, generic);
            } else if (!generic.getName().startsWith("java")) {
                return toObject(s, generic);
            }
        }
        return s;
    }

    private Enum<?> getParsedEnum(String value, Field declaredField) {
        Class<?> enumClass = declaredField.getType();
        if (enumClass.isEnum()) {
            Object[] enumValues = enumClass.getEnumConstants();
            for (Object enumValue : enumValues) {
                if (((Enum<?>) enumValue).name().equals(value)) {
                    return (Enum<?>) enumValue;
                }
            }
        }
        return null;
    }
}