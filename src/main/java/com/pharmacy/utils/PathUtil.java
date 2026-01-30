package com.pharmacy.utils;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathUtil {

	private static final Logger LOG = LoggerFactory.getLogger(PathUtil.class);

	public static String getAppPath() {
		try {
			File path = new File(PathUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI());

			if (path.isFile()) {
				return path.getParent();
			} else {
				File parent = path.getParentFile();
				return (parent != null) ? parent.getParent() : ".";
			}

		} catch (Exception e) {
			LOG.error("Failed to determine application path. Defaulting to current directory '.'", e);
			return ".";
		}
	}
}