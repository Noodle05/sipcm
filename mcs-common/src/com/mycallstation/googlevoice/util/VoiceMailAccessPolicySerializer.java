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
import com.mycallstation.googlevoice.setting.VoiceMailAccessPolicy;

/**
 * @author wgao
 * 
 */
@Component("voiceMailAccessPolicySerializer")
public class VoiceMailAccessPolicySerializer implements
		JsonDeserializer<VoiceMailAccessPolicy>,
		JsonSerializer<VoiceMailAccessPolicy> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement,
	 * java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
	 */
	@Override
	public VoiceMailAccessPolicy deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		int value = json.getAsInt();
		return VoiceMailAccessPolicy.valueOf(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object,
	 * java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(VoiceMailAccessPolicy src, Type typeOfSrc,
			JsonSerializationContext context) {
		return new JsonPrimitive(src.getValue());
	}
}
