package com.twister.cineworld.model.json.data;

import java.lang.reflect.Type;

import com.google.gson.*;

public class CineworldDateTypeConverter implements JsonSerializer<CineworldDate>, JsonDeserializer<CineworldDate> {
	public JsonElement serialize(final CineworldDate src, final Type srcType, final JsonSerializationContext context) {
		return new JsonPrimitive(src.getDate());
	}

	public CineworldDate deserialize(final JsonElement json, final Type typeOfT,
	        final JsonDeserializationContext context)
	        throws JsonParseException {
		CineworldDate date = new CineworldDate();
		date.setDate(json.getAsJsonPrimitive().getAsString());
		return date;
	}
}
