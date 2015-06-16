package com.gojira.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.lang.reflect.Type;

/**
 * Implementation of a date {@link JsonSerializer} and {@link JsonDeserializer}
 * for GSON. It parses ISO 8601 String dates into {@link DateTime} objects.
 *
 * @author Stratos Theodorou
 * @version 1.0
 * @since 14/06/2015
 */
public class DateTimeConverter implements JsonSerializer<DateTime>, JsonDeserializer<DateTime> {

    @Override
    public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(ISODateTimeFormat.dateTime().withOffsetParsed().print(src));
    }

    @Override
    public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String isoDateTime = json.getAsString();
        return ISODateTimeFormat.dateTime().withOffsetParsed().parseDateTime(isoDateTime);
    }

}
