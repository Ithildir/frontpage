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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Andrea Di Giorgi
 */
public class CaseInsensitiveMapTest {

	@Test
	public void testCaseInsensitive() {
		Map<String, String> map = new CaseInsensitiveMap<>();

		map.put("One", "One");
		map.put("Two", "Two");

		Assert.assertEquals("One", map.get("one"));

		Assert.assertEquals("One", map.get("oNe"));

		map.put("two", "Three");

		Assert.assertEquals("Three", map.get("Two"));
	}

	@Test
	public void testNullHandling() {
		Map<String, String> map = new CaseInsensitiveMap<>();

		map.put("One", "One");
		map.put("Two", "Two");
		map.put(null, "Three");

		Assert.assertEquals("Three", map.get(null));

		map.put(null, "Four");

		Assert.assertEquals("Four", map.get(null));

		Set<String> keys = map.keySet();

		Assert.assertEquals(keys.toString(), 3, keys.size());

		Assert.assertTrue(keys.contains("one"));
		Assert.assertTrue(keys.contains("two"));
		Assert.assertTrue(keys.contains(null));
	}

	@Test
	public void testPutAll() {
		Map<String, String> map = new HashMap<>();

		map.put("One", "One");
		map.put("one", "Three");
		map.put("Two", "Two");
		map.put(null, "Four");
		map.put(String.valueOf(20), "Five");

		Map<String, String> caseInsensitiveMap = new CaseInsensitiveMap<>();

		caseInsensitiveMap.putAll(map);

		Assert.assertEquals(
			caseInsensitiveMap.toString(), 4, caseInsensitiveMap.size());

		Set<String> keys = caseInsensitiveMap.keySet();

		Assert.assertEquals(keys.toString(), 4, keys.size());

		Assert.assertTrue(keys.contains("one"));
		Assert.assertTrue(keys.contains("two"));
		Assert.assertTrue(keys.contains(null));
		Assert.assertTrue(keys.contains(String.valueOf(20)));

		Assert.assertTrue(
			!caseInsensitiveMap.containsValue("One") ||
			!caseInsensitiveMap.containsValue("Three"));

		Assert.assertEquals("Four", caseInsensitiveMap.get(null));
	}

}