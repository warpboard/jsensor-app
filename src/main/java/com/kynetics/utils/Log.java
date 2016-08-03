/**
 * Copyright 2014 - Kynetics, LLC
 */
package com.kynetics.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Diego Rondini
 *
 */
public enum Log {
	INSTANCE;

	private static final Logger log = Logger.getLogger("SensorsApp");

	public static int e(String tag, String msg, Throwable t) {
		return e(tag, msg);
	}
	
	public static int e(String tag, String msg) {
		log.log(Level.SEVERE, msg);
		return 0;
	}

	public static int w(String tag, String msg) {
		log.log(Level.WARNING, msg);
		return 0;
	}

	public static int d(String tag, String msg) {
		log.log(Level.FINE, msg);
		return 0;
	}

	public static int i(String tag, String msg) {
		log.log(Level.INFO, msg);
		return 0;
	}
}
