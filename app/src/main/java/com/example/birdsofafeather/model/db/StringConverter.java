package com.example.birdsofafeather.model.db;

import androidx.room.TypeConverter;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class StringConverter implements Serializable {

    @TypeConverter
    public String fromStringValuesList(List<String> stringValues) {
        if (stringValues == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {
        }.getType();
        String json = gson.toJson(stringValues, type);
        return json;
    }

    @TypeConverter
    public List<String> toStringValuesList(String stringValuesString) {
        if (stringValuesString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {
        }.getType();
        List<String> productCategoriesList = gson.fromJson(stringValuesString, type);
        return productCategoriesList;
    }

}
