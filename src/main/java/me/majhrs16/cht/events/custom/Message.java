package me.majhrs16.cht.events.custom;

import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import org.jetbrains.annotations.NotNull;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.Bukkit;

import java.util.function.UnaryOperator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import java.lang.reflect.Method;

import me.majhrs16.cht.ChatTranslator;
import me.majhrs16.cht.util.util;

import me.majhrs16.lib.logger.Logger;

public class Message extends Event implements Cancellable {
	private interface Converter {
		Object convert(String arg) throws Exception;
	}

	private final ChatTranslator plugin = ChatTranslator.getInstance();
	private final Logger logger         = plugin.logger;
	private final UUID uuid             = UUID.randomUUID();

	private static final HandlerList HANDLERS = new HandlerList();
	private static final List<Converter> converters = Arrays.asList(
			(arg) -> {
				arg = arg.toLowerCase();
				if (arg.equals("true") || arg.equals("false"))
					return Boolean.parseBoolean(arg);

				else
					throw new IllegalArgumentException();
			},
			(arg) -> {
				arg = arg.toLowerCase();
				if (arg.equals("null"))
					return null;

				else
					throw new IllegalArgumentException();
			},
			Integer::parseInt,
			Float::parseFloat,
			Double::parseDouble
			// Agrega más convertidores según sea necesario para otros tipos de datos
	);

	private Message to;
	private CommandSender sender;
	private String name;
	private String sender_type;
	private Formats messages       = new Formats();
	private Formats tool_tips      = new Formats();
	private String[] sounds        = new String[0];
	private boolean is_cancelled   = false;
	private String lang_source     = util.getNativeLang();
	private String lang_target     = ChatTranslator.getInstance().storage.getDefaultLang();
	private Boolean is_force_color = true;
	private Boolean is_format_papi = true;
	private String last_format     = "UNKNOWN";

	public Message() {
		setSender(Bukkit.getConsoleSender());
		getMessages().setFormats("%ct_messages%");
	}

	@NotNull public static HandlerList getHandlerList() { return HANDLERS; }
	@NotNull public HandlerList getHandlers()           { return HANDLERS; }

	public boolean isCancelled() {
		return is_cancelled;
	}

	public Message setCancelledThis(boolean isCancelled) {
		this.is_cancelled  = isCancelled;
		return this;
	}

	public void setCancelled(boolean isCancelled) {
		try {
			getTo().setCancelledThis(isCancelled); // Soporte con CE.

		} catch (NullPointerException e) {
			logger.error(e.toString());
		}

		setCancelledThis(isCancelled);
	}

	public Message setTo(Message to) {
		this.to = to == null ? new Message() : to;
		return this;
	}

	public Message setSender(CommandSender sender) {
		this.sender = sender;
		setSenderName(sender == null ? "CONSOLE" : sender.getName());
		setSenderType(sender instanceof Player ? "PLAYER" : "CONSOLE");
		return this;
	}

	private Message setSenderType(String type) {
		this.sender_type = type;
		return this;
	}

	public Message setSenderName(String name) {
		this.name = name;
		return this;
	}

	private Message setMessages(Formats formats) {
		this.messages = formats;
		return this;
	}
	private Message setToolTips(Formats formats) {
		this.tool_tips = formats;
		return this;
	}

	public Message setSound(int index, String sounds) {
		this.sounds[index] = sounds;
		return this;
	}

	public Message setSounds(String... sounds) {
		this.sounds = sounds;
		return this;
	}

	public Message setLangSource(String lang) {
		this.lang_source = lang;
		return this;
	}

	public Message setLangTarget(String lang) {
		this.lang_target = lang;
		return this;
	}

	public Message setFormatPAPI(Boolean formatPAPI) {
		this.is_format_papi = formatPAPI;
		return this;
	}

	public Message setForceColor(Boolean color) {
		this.is_force_color = color;
		return this;
	}

	public Message format(String path, UnaryOperator<String> preFormats, UnaryOperator<String> preTexts) {
		util.applyMessagesFormat(this, path, (formats, texts) -> {
			if (preFormats != null) formats.replaceAll(preFormats);
			if (preTexts != null) texts.replaceAll(preTexts);
		});

		util.applyToolTipsFormat(this, path, (formats, texts) -> {
			if (preFormats != null) formats.replaceAll(preFormats);
			if (preTexts != null) texts.replaceAll(preTexts);
		});

		util.applySoundsFormat(this, path);

		last_format = path;

		return this;
	}

	public Message format(String path, UnaryOperator<String> preFormats) {
		return format(path, preFormats, null);
	}

	public Message format(String path) {
		return format(path, null, null);
	}


	public Message getTo()		      { return to; }

	public CommandSender getSender()  { return sender; }
	public String getSenderType()	  { return sender_type; }
	public String getSenderName()	  { return name; }

	public String getSound(int index) { return sounds[index]; }

	public Formats getMessages()      { return messages; }
	public Formats getToolTips()      { return tool_tips; }
	public String[] getSounds()       { return sounds; }

	public String getLangSource()     { return lang_source; }
	public String getLangTarget()     { return lang_target; }

	public Boolean getFormatPAPI()    { return is_format_papi; }
	public Boolean isForceColor()     { return is_force_color; }
	public String getLastFormatPath() { return last_format; }

	public Message clone() {
		Message from = new Message();
			Message to = new Message(); // BUGAZO!! Hay que clonarlo manualmente o sino no copia completamente. O_o??
				if (getTo() != null) {
					to.setSender(getTo().getSender());
					to.setSenderName(getTo().getSenderName());
					to.setMessages(getTo().getMessages());
					to.setToolTips(getTo().getToolTips());
					to.setSounds(getTo().getSounds());
					to.setCancelledThis(getTo().isCancelled());
					to.setLangSource(getTo().getLangSource());
					to.setLangTarget(getTo().getLangTarget());
					to.setForceColor(getTo().isForceColor());
					to.setFormatPAPI(getTo().getFormatPAPI());
					to.last_format = getTo().last_format;
				}
			from.setTo(to);

			from.setSender(getSender());
			from.setSenderName(getSenderName());
			from.setMessages(getMessages());
			from.setToolTips(getToolTips());
			from.setSounds(getSounds());
			from.setCancelledThis(isCancelled());
			from.setLangSource(getLangSource());
			from.setLangTarget(getLangTarget());
			from.setForceColor(isForceColor());
			from.setFormatPAPI(getFormatPAPI());
			from.last_format = last_format;

		return from;
	}

	@SuppressWarnings("unchecked")
	public String toJson() {
		JSONObject messages, tool_tips;

		JSONObject from = new JSONObject();
			from.put("senderName", getSenderName());

			messages = new JSONObject();
				messages.put("formats", Arrays.asList(getMessages().getFormats()));
				messages.put("texts", Arrays.asList(getMessages().getTexts()));
			from.put("messages", messages);

			tool_tips = new JSONObject();
				tool_tips.put("formats", Arrays.asList(getToolTips().getFormats()));
				tool_tips.put("texts", Arrays.asList(getToolTips().getTexts()));
			from.put("toolTips", tool_tips);

			from.put("sounds", Arrays.asList(getSounds()));
			from.put("isCancelled", isCancelled());
			from.put("langSource", getLangSource());
			from.put("langTarget", getLangTarget());
			from.put("isColor", isForceColor());
			from.put("isPAPI", getFormatPAPI());

		JSONObject to = new JSONObject();
			if (getTo() != null) {
				to.put("senderName", getTo().getSenderName());

				messages = new JSONObject();
					messages.put("formats", Arrays.asList(getTo().getMessages().getFormats()));
					messages.put("texts", Arrays.asList(getTo().getMessages().getTexts()));
				from.put("messages", messages);

				tool_tips = new JSONObject();
					tool_tips.put("formats", Arrays.asList(getTo().getToolTips().getFormats()));
					tool_tips.put("texts", Arrays.asList(getTo().getToolTips().getTexts()));
				from.put("toolTips", tool_tips);

				to.put("sounds", Arrays.asList(getTo().getSounds()));
				to.put("isCancelled", getTo().isCancelled());
				to.put("langSource", getTo().getLangSource());
				to.put("langTarget", getTo().getLangTarget());
				to.put("isColor", getTo().isForceColor());
				to.put("isPAPI", getTo().getFormatPAPI());
			}

		JSONObject json = new JSONObject();
			json.put("from", from);
			json.put("to", to);
		return json.toString();
	}

	public static Message fromJson(String jsonString) {
		JSONObject messages, tool_tips;
		JSONArray formats;

		try {
			JSONObject json = (JSONObject) new JSONParser().parse(jsonString);

			JSONObject fromJson = (JSONObject) json.get("from");
				Message from = new Message();
					from.setSender(util.getSenderByName((String) fromJson.get("senderName")));

					messages =  (JSONObject) fromJson.get("messages");
						formats = (JSONArray) messages.get("formats");
					from.getMessages().setFormats((String[]) formats.toArray());

					tool_tips =  (JSONObject) fromJson.get("toolTips");
						formats = (JSONArray) tool_tips.get("formats");
					from.getToolTips().setFormats((String[]) formats.toArray());

					from.setSounds((String[]) fromJson.get("sounds"));
					from.setCancelledThis((Boolean) fromJson.get("isCancelled"));
					from.setLangSource((String) fromJson.get("langSource"));
					from.setLangTarget((String) fromJson.get("langTarget"));
					from.setForceColor((Boolean) fromJson.get("isColor"));
					from.setFormatPAPI((Boolean) fromJson.get("isPAPI"));

			JSONObject toJson = (JSONObject) json.get("to");
				Message to = new Message();
					if (toJson != null) {
						to.setSender(util.getSenderByName((String) toJson.get("senderName")));

						messages =  (JSONObject) fromJson.get("messages");
							formats = (JSONArray) messages.get("formats");
						from.getMessages().setFormats((String[]) formats.toArray());

						tool_tips =  (JSONObject) fromJson.get("toolTips");
							formats = (JSONArray) tool_tips.get("formats");
						from.getToolTips().setFormats((String[]) formats.toArray());

						to.setSounds((String[]) toJson.get("sounds"));
						to.setCancelledThis((Boolean) toJson.get("isCancelled"));
						to.setLangSource((String) toJson.get("langSource"));
						to.setLangTarget((String) toJson.get("langTarget"));
						to.setForceColor((Boolean) toJson.get("isColor"));
						to.setFormatPAPI((Boolean) toJson.get("isPAPI"));
					}
				from.setTo(to);

			return from;

		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean isEmpty() {
		return this.equals(new Message());
	}

	public UUID getUUID() {
		return uuid;
	}

	////////////////////////////////
	// PROPERTY ACCESS
	private Method[] getMethod(Object obj, String methodName) {
		List<Method> methods = new ArrayList<>();

		for (Method method : obj.getClass().getDeclaredMethods()) {
			if (method.getName().equals(methodName)) {
				methods.add(method);
			}
		}

		return methods.toArray(new Method[0]);
	}

	private Object convertArgument(String arg) {
		for (Converter converter : converters) {
			try {
				return converter.convert(arg);

			} catch (Exception ignored) {
				continue;
			}
		}

		return arg;
	}

	private String[] getArguments(String arguments) {
		List<String> argList = new ArrayList<>();
		StringBuilder currentArg = new StringBuilder();
		boolean insideQuotes = false;
		for (char c : arguments.toCharArray()) {
			if (c == ',') {
				if (!insideQuotes) {
					argList.add(currentArg.toString().trim());
					currentArg = new StringBuilder();
					continue;
				}

			} else if (c == '"') {
				insideQuotes = !insideQuotes;
			}

			currentArg.append(c);
		}

		argList.add(currentArg.toString().trim());
		return argList.toArray(new String[0]);
	}

	private Object[] convertArguments(String args) {
		List<Object> convertedArgs = new ArrayList<>();
		for (String arg : getArguments(args))
			convertedArgs.add(convertArgument(arg.trim()));

		return convertedArgs.toArray();
	}

	private Object getPropertyRecursive(Object obj, String[] pathComponents, int index) throws Exception {
		if (obj == null || index >= pathComponents.length)
			return obj;

		String component = pathComponents[index];

		if (!(component.contains("(") && component.contains(")")))
			throw new IllegalAccessException("Access to fields is not allowed");

		String methodName = component.substring(0, component.indexOf("("));
		String arguments = component.substring(component.indexOf("(") + 1, component.indexOf(")"));

		Method[] methods = getMethod(obj, methodName);

		if (methods.length == 0)
			throw new NoSuchMethodException("Method " + methodName + " not found");

		Object result = null;
		Exception last_exception = null;
		Object[] convertedArgs = arguments.isEmpty() ? new Object[0] : convertArguments(arguments);

		for (Method method : methods) {
			if (convertedArgs.length == 0) {
				result = method.invoke(obj);
				break;
			}

			try {
				result = method.invoke(obj, (Object) convertedArgs);
				break;

			} catch (IllegalArgumentException ignored) {}

			try {
				result = method.invoke(obj, convertedArgs[0]);
				break;

			} catch (IllegalArgumentException ignored) {}

			try {
				String[] convertedArgsArray = new String[convertedArgs.length];

				for (int i = 0; i < convertedArgs.length; i++)
					convertedArgsArray[i] = convertedArgs[i].toString();

				result = method.invoke(obj, (Object) convertedArgsArray);
				break;

			} catch (Exception e) {
				last_exception = e;
			}
		}

		if (result == null && last_exception != null)
			throw last_exception;

		return getPropertyRecursive(result, pathComponents, index + 1);
	}

	private String[] splitPath(String propertyPath) {
		List<String> pathComponents = new ArrayList<>();
		StringBuilder currentComponent = new StringBuilder();
		boolean insideQuotes = false;

		for (char c : propertyPath.toCharArray()) {
			if (c == '.') {
				if (!insideQuotes) {
					pathComponents.add(currentComponent.toString());
					currentComponent.setLength(0); // Clear StringBuilder
				}

			} else if (c == '"') {
				insideQuotes = !insideQuotes;

			} else {
				currentComponent.append(c);
			}
		}

		pathComponents.add(currentComponent.toString());

		return pathComponents.toArray(new String[0]);
	}

	public Object property(String propertyPath) {
		try {
			String[] pathComponents = splitPath(propertyPath);
			return getPropertyRecursive(this, pathComponents, 0);

		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}
	}
}