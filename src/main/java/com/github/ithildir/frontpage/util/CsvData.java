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

import com.aerse.yacsv.CSVReader;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;

import java.nio.charset.StandardCharsets;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Andrea Di Giorgi
 */
public class CsvData implements Closeable {

	public CsvData(byte[] bytes) {
		_csvReader = new CSVReader(
			new InputStreamReader(
				new ByteArrayInputStream(bytes), StandardCharsets.UTF_8));

		if (!hasNext()) {
			throw new IllegalArgumentException("Unable to get column indexes");
		}

		_columnIndexes = new CaseInsensitiveMap<>();

		String[] keys = _csvReader.next();

		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];

			_columnIndexes.put(key, i);
		}

		hasNext();
	}

	@Override
	public void close() throws IOException {
		_csvReader.close();
	}

	public LocalDate getLocalDate(String key) {
		String value = getString(key);

		if (value == null) {
			return null;
		}

		return LocalDate.parse(value, _localDateDateTimeFormatter);
	}

	public Map<String, String> getMap(String key) {
		String value = getString(key);

		if (value == null) {
			return Collections.emptyMap();
		}

		Map<String, String> map = new HashMap<>();

		for (String line : _mapSplitter.split(value)) {
			int pos = line.indexOf(':');

			if (pos == -1) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to parse \"" + line + "\"");
				}

				continue;
			}

			String entryKey = line.substring(0, pos);

			entryKey = entryKey.toLowerCase();
			entryKey = entryKey.replace('_', '-');

			String entryValue = line.substring(pos + 1);

			map.put(entryKey, entryValue);
		}

		return map;
	}

	public String getString(String key) {
		String[] values = _csvReader.next();

		int index = _columnIndexes.get(key);

		return Strings.nullToEmpty(values[index]);
	}

	public boolean hasNext() {
		return _csvReader.hasNext();
	}

	private static final Log _log = LogFactory.getLog(CsvData.class);

	private static final DateTimeFormatter _localDateDateTimeFormatter =
		DateTimeFormatter.ofPattern("MMM dd, yyyy");
	private static final Splitter _mapSplitter = Splitter.on(
		','
	).omitEmptyStrings(
	).trimResults();

	private final Map<String, Integer> _columnIndexes;
	private final CSVReader _csvReader;

}