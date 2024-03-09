package me.majhrs16.cht.util;

import com.google.gson.*;

public class JsonFormatter {
	public static String format(String json) {
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonElement je = new JsonParser().parse(json);
			return gson.toJson(je);

		} catch (NoClassDefFoundError e) {
			return e.toString();
		}
	}
}