package com.nico.store.store.utils;

import org.apache.logging.log4j.util.Strings;

import java.util.Set;

/**
 * @author Duy Trần Thế
 * @version 1.0
 * @datetime 1/1/2022 11:05 PM
 */
public class StringUtils {

	private StringUtils() {
		throw new IllegalStateException(this.getClass().getSimpleName());
	}

	public static String toStringWithDelimiterForSet(Set<String> strings, String delimiter) {
		if (!strings.isEmpty()) {
			return String.join(delimiter, strings);
		}
		return Strings.EMPTY;
	}
}
