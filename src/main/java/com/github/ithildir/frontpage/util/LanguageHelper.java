/**
 * Copyright (c) 2018 Andrea Di Giorgi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.ithildir.frontpage.util;

import com.github.ithildir.frontpage.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Andrea Di Giorgi
 */
@Singleton
public class LanguageHelper {

	@Inject
	public LanguageHelper() {
		_map = new HashMap<>();

		ClassLoader classLoader = LanguageHelper.class.getClassLoader();

		try (BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(
					classLoader.getResourceAsStream(
						"content/Language.properties"),
					StandardCharsets.UTF_8))) {

			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				int pos = line.indexOf('=');

				if (pos == -1) {
					continue;
				}

				String key = line.substring(0, pos);
				String value = line.substring(pos + 1);

				List<String> values = _map.get(key);

				if (values == null) {
					values = new ArrayList<>();

					_map.put(key, values);
				}

				values.add(value);
			}
		}
		catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	public String format(String key, String... arguments) {
		return format(key, null, arguments);
	}

	public String format(String key, User user, String... arguments) {
		String pattern = get(key);

		int pos = 0;

		StringBuilder sb = new StringBuilder(
			16 * arguments.length + pattern.length());

		int start = pattern.indexOf('{');

		while (start != -1) {
			int endIndex = start + 2;

			if ((endIndex > pattern.length()) ||
				(pattern.charAt(endIndex) != '}')) {

				throw new UnsupportedOperationException();
			}

			sb.append(pattern, pos, start);

			String argumentValue = null;

			char argumentChar = pattern.charAt(start + 1);

			if (Character.isDigit(argumentChar)) {
				int argumentIndex = pattern.charAt(start + 1) - '0';

				argumentValue = arguments[argumentIndex];
			}
			else {
				argumentValue = _getUserArgumentValue(user, argumentChar);
			}

			sb.append(argumentValue);

			pos = endIndex + 1;

			start = pattern.indexOf('{', pos);
		}

		if (pos < pattern.length()) {
			sb.append(pattern, pos, pattern.length());
		}

		return sb.toString();
	}

	public String get(String key) {
		List<String> values = _map.get(key);

		if (values == null) {
			throw new IllegalArgumentException();
		}

		ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();

		int i = threadLocalRandom.nextInt(values.size());

		return values.get(i);
	}

	private String _getUserArgumentValue(User user, char argument) {
		if (argument == _USER_FIRST_NAME) {
			return user.getFirstName();
		}
		else if (argument == _USER_FULL_NAME) {
			return user.getFullName();
		}
		else if (argument == _USER_HER_OR_HIM) {
			String key = "her";

			if (user.isMale()) {
				key = "him";
			}

			return get(key);
		}
		else {
			throw new IllegalArgumentException(
				"Unknown user argument character '" + argument + "'");
		}
	}

	private static final char _USER_FIRST_NAME = 'f';

	private static final char _USER_FULL_NAME = 'F';

	private static final char _USER_HER_OR_HIM = 'h';

	private final Map<String, List<String>> _map;

}