/**
 * 
 */
package com.mycallstation.googlevoice.util;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mycallstation.googlevoice.setting.PhoneType;

/**
 * @author Wei Gao
 * 
 */
public class PhoneTypeSerializer implements JsonSerializer<PhoneType>,
		JsonDeserializer<PhoneType> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement,
	 * java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
	 */
	@Override
	public PhoneType deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		int value = json.getAsInt();
		return PhoneType.valueOf(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object,
	 * java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(PhoneType src, Type typeOfSrc,
			JsonSerializationContext context) {
		return new JsonPrimitive(src.getValue());
	}
}
