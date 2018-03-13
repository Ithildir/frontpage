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

import com.google.common.collect.ForwardingMap;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrea Di Giorgi
 */
public class CaseInsensitiveMap<V> extends ForwardingMap<String, V> {

	@Override
	public V get(Object key) {
		return super.get(_fixKey(key));
	}

	@Override
	public V put(String key, V value) {
		return super.put(_fixKey(key), value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends V> map) {
		standardPutAll(map);
	}

	@Override
	public V remove(Object object) {
		return super.remove(_fixKey(object));
	}

	@Override
	protected Map<String, V> delegate() {
		return _delegateMap;
	}

	private String _fixKey(Object key) {
		if (key == null) {
			return null;
		}

		String keyString = key.toString();

		keyString = keyString.trim();
		keyString = keyString.toLowerCase();

		return keyString;
	}

	private final Map<String, V> _delegateMap = new HashMap<>();

}