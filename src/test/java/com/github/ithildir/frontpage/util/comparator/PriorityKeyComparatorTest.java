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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Andrea Di Giorgi
 */
public class PriorityKeyComparatorTest {

	@Test
	public void testCompare() {
		List<String> keys = new ArrayList<>();

		keys.add("foo");
		keys.add("bar");
		keys.add("baz");
		keys.add("qux");
		keys.add("quux");
		keys.add("corge");
		keys.add("grault");
		keys.add("garply");
		keys.add("waldo");
		keys.add("fred");
		keys.add("plugh");
		keys.add("xyzzy");
		keys.add("thud");

		keys.sort(new PriorityKeyComparator("waldo", "quux", "garply"));

		Assert.assertEquals(
			Arrays.asList(
				"waldo", "quux", "garply", "bar", "baz", "corge", "foo", "fred",
				"grault", "plugh", "qux", "thud", "xyzzy"),
			keys);
	}

}