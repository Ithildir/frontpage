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

package com.github.ithildir.frontpage.cache;

import ai.grakn.redismock.RedisServer;

import com.github.ithildir.frontpage.cache.impl.LocalCacheImpl;
import com.github.ithildir.frontpage.cache.impl.RedisCacheImpl;

import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andrea Di Giorgi
 */
public class CacheTest {

	@Before
	public void setUp() throws IOException {
		_redisServer = new RedisServer();

		_redisServer.start();

		Cache cache = new RedisCacheImpl(
			null, _redisServer.getHost(), _redisServer.getBindPort(), null);

		_cache = new LocalCacheImpl(cache);
	}

	@After
	public void tearDown() {
		_redisServer.stop();
	}

	@Test
	public void testGet() throws Exception {
		Assert.assertEquals(
			"foo", _cache.get("key", Integer.MAX_VALUE, () -> "foo"));

		Assert.assertEquals(
			"foo", _cache.get("key", Integer.MAX_VALUE, () -> "bar"));

		Thread.sleep(1000);

		Assert.assertEquals("baz", _cache.get("key", 10, () -> "baz"));

		Thread.sleep(1000);

		Assert.assertEquals(
			"baz",
			_cache.get(
				"key", 1,
				() -> {
					throw new Exception();
				}));
	}

	private Cache _cache;
	private RedisServer _redisServer;

}