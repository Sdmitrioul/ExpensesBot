package com.dskroba.principals;

import com.dskroba.storage.ValueConverter;
import com.dskroba.type.Currency;
import com.google.gson.*;

import java.lang.reflect.Type;

public class PrincipalConverter implements ValueConverter<Principal> {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Currency.class, new CurrencyAdapter())
            .create();

    @Override
    public Principal parse(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return GSON.fromJson(value, Principal.class);
    }

    @Override
    public String convertToString(Principal value) {
        if (value == null) {
            return "";
        }
        return GSON.toJson(value);
    }

    private static class CurrencyAdapter implements JsonSerializer<Currency>, JsonDeserializer<Currency> {

        @Override
        public JsonElement serialize(Currency src, Type typeOfSrc, JsonSerializationContext context) {
            return src != null ? new JsonPrimitive(src.name()) : JsonNull.INSTANCE;
        }

        @Override
        public Currency deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            if (json == null || json.isJsonNull()) {
                return null;
            }
            return Currency.parseValue(json.getAsString());
        }
    }
}
