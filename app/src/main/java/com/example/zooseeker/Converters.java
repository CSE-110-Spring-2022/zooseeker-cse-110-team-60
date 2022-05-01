package com.example.zooseeker;

import androidx.room.TypeConverter;

public class Converters {
    @TypeConverter
    public static String fromArray(String[] list) {
        return String.join(",", list);
    }

    @TypeConverter
    public static String[] fromString(String string) {
        return string.split(", ");
    }
}
