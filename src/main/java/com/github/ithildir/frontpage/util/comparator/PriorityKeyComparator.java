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

package com.github.ithildir.frontpage.util.comparator;

import java.util.Comparator;

/**
 * @author Andrea Di Giorgi
 */
public class PriorityKeyComparator implements Comparator<String> {

	public PriorityKeyComparator(String... priorityKeys) {
		_priorityKeys = priorityKeys;
	}

	@Override
	public int compare(String key1, String key2) {
		int priority1 = _getPriority(key1);
		int priority2 = _getPriority(key2);

		if (priority1 == priority2) {
			return key1.compareTo(key2);
		}

		return priority1 - priority2;
	}

	private int _getPriority(String key) {
		for (int i = 0; i < _priorityKeys.length; i++) {
			if (key.equals(_priorityKeys[i])) {
				return i;
			}
		}

		return Integer.MAX_VALUE;
	}

	private final String[] _priorityKeys;

}