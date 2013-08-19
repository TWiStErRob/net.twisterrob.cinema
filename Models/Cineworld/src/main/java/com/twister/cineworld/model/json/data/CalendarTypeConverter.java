package com.twister.cineworld.model.json.data;

import java.lang.reflect.Type;
import java.text.*;
import java.util.*;

import com.google.gson.*;

public class CalendarTypeConverter implements JsonSerializer<Calendar>, JsonDeserializer<Calendar> {
	private static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";

	public JsonElement serialize(Calendar src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(new SimpleDateFormat(PATTERN).format(src.getTime()));
	}
	public Calendar deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		Calendar cal = Calendar.getInstance();
		String value = json.getAsJsonPrimitive().getAsString();
		try {
			Date d = new SimpleDateFormat(PATTERN).parse(value);
			cal.setTimeInMillis(d.getTime());
			return cal;
		} catch (ParseException ex) {
			throw new JsonParseException("Cannot deserialize Calendar value: " + value + " with pattern " + PATTERN, ex);
		}
	}
}
