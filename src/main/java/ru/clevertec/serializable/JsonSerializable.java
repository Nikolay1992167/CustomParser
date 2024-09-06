package ru.clevertec.serializable;

public interface JsonSerializable {

    Object fromJson(String json, Class<?> clazz);
    String toJson(Object object);
}