/**
 * 
 */
package com.mycallstation.googlevoice.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mycallstation.googlevoice.setting.PhoneType;
import com.mycallstation.googlevoice.setting.ScheduleSet;
import com.mycallstation.googlevoice.setting.VoiceMailAccessPolicy;

/**
 * @author wgao
 * 
 */
public abstract class Utility {
	private static final Gson gson = new GsonBuilder()
			.registerTypeAdapter(VoiceMailAccessPolicy.class,
					new VoiceMailAccessPolicySerializer())
			.registerTypeAdapter(ScheduleSet.class, new ScheduleSetSerializer())
			.registerTypeAdapter(PhoneType.class, new PhoneTypeSerializer())
			.create();

	public static Gson getGson() {
		return gson;
	}
}
