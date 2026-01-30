package com.pharmacy.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TemplateUtil {

	public static String loadTemplate(String file_name) {
		try (java.io.InputStream is = TemplateUtil.class.getClassLoader().getResourceAsStream(file_name)) {

			if (is == null)
				throw new RuntimeException("File not found: " + file_name);

			return new String(is.readAllBytes(), StandardCharsets.UTF_8);

		} catch (IOException e) {
			throw new RuntimeException("Error read template: " + e.getMessage(), e);
		}
	}

}
