/**
 * 
 */
package com.mycallstation.googlevoice.util;

import java.lang.reflect.Type;

import org.springframework.stereotype.Component;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mycallstation.googlevoice.setting.ScheduleSet;

/**
 * @author Wei Gao
 * 
 */
@Component("booleanSerializer")
public class ScheduleSetSerializer implements JsonDeserializer<ScheduleSet>,
		JsonSerializer<ScheduleSet> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement,
	 * java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
	 */
	@Override
	public ScheduleSet deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		String value = json.getAsString();
		return ScheduleSet.byValue(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object,
	 * java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(ScheduleSet src, Type typeOfSrc,
			JsonSerializationContext context) {
		return new JsonPrimitive(src.getValue());
	}
}
