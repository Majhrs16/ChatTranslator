package majhrs16.cht.util.cache.internal;
/*
import org.bukkit.configuration.file.FileConfiguration;

import majhrs16.cht.ChatTranslator;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
*/

@Deprecated
public class _TextsHandler {/*
	private static final Pattern pattern = Pattern.compile("%([\\.\\-A-Za-z0-9_]+)%");
	private FileConfiguration messages;

	public void reload() {
		messages = ChatTranslator.getInstance().messages.get();
		
		// Inicia el procesamiento recursivo desde la clase raíz Texts
		initConfigurables(Texts.class);
		formatFormattedVariables(Texts.class);
	}

	private void initConfigurables(Class<?> clazz) {
		if (clazz == null || clazz == Object.class) {
			return;
		}

		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(Config.class)) {
				Config annotation = field.getAnnotation(Config.class);
				String path = annotation.value();
				String value = null;

				if (!path.isEmpty() && messages.contains(path)) {
					if (messages.isList(path)) {
						value = String.join("\n", messages.getStringList(path));

					} else if (messages.isString(path)) {
						value = messages.getString(path);
					}

					if (value != null && value.isEmpty()) {
						value = null;
					}
				}

				try {
					field.setAccessible(true);
					field.set(null, value); // Usar null como objeto en este contexto

				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}

		for (Class<?> clazz2 : clazz.getDeclaredClasses()) {
			initConfigurables(clazz2);
		}
	}

	private void formatFormattedVariables(Class<?> clazz) {
		if (clazz == null || clazz == Object.class) {
			return;
		}

		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(Config.class)) {
				String value = null;

				try {
					field.setAccessible(true);
					value = (String) field.get(null); // Obtener el valor ya asignado

				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}

				if (value != null) {
					// Realizar el formateo aquí y actualizar el campo con el valor formateado
					String formattedValue = formatStringVariable(value);

					try {
						field.set(null, formattedValue);

					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}

		for (Class<?> clazz2 : clazz.getDeclaredClasses()) {
			formatFormattedVariables(clazz2);
		}
	}

	private String formatStringVariable(String input) {
		Matcher matcher;

		while ((matcher = pattern.matcher(input)).find()) {
			String match	   = matcher.group(1).replace("-", "_").toUpperCase();
			String replacement = getNestedVariable(match);

			if (replacement == null)
				continue;

			input = input.replace(matcher.group(0), replacement);
		}

		return input;
	}

	private String getNestedVariable(String variableName) {
		String packageName, fieldName;
		int lastDotIndex = variableName.lastIndexOf('.');
		String packageRoot = "majhrs16.cht.util.cache.internal.Texts"; 

		if (lastDotIndex >= 0) {
			packageName = packageRoot + "$" + variableName.substring(0, lastDotIndex).replace('.', '$');
			fieldName   = variableName.substring(lastDotIndex + 1);

		} else {
			packageName = packageRoot;
			fieldName   = variableName;
		}

		try {
			Class<?> clazz = Class.forName(packageName);
			Field field = clazz.getField(fieldName);
			field.setAccessible(true);
			return (String) field.get(null);

		} catch (ClassNotFoundException | NoSuchFieldException  e) {
			return null;

		} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
*/ }