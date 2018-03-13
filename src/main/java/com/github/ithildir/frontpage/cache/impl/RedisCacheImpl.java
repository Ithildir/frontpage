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

package com.github.ithildir.frontpage.cache.impl;

import com.github.ithildir.frontpage.cache.Cache;
import com.github.ithildir.frontpage.cache.CacheEntry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.nio.charset.StandardCharsets;

import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

/**
 * @author Andrea Di Giorgi
 */
public class RedisCacheImpl extends BaseCacheImpl {

	public RedisCacheImpl(
		Cache parentCache, String host, int port, String password) {

		super(parentCache);

		_jedisPool = new JedisPool(
			new GenericObjectPoolConfig(), Objects.requireNonNull(host), port,
			Protocol.DEFAULT_TIMEOUT, password);
	}

	@Override
	protected CacheEntry getCacheEntry(String key) throws Exception {
		byte[] bytes = null;

		try (Jedis jedis = _jedisPool.getResource()) {
			bytes = jedis.get(_getBinaryKey(key));
		}

		if (bytes == null) {
			return null;
		}

		try (ObjectInputStream objectInputStream = new ObjectInputStream(
				new ByteArrayInputStream(bytes))) {

			return (CacheEntry)objectInputStream.readObject();
		}
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	@Override
	protected void putCacheEntry(String key, CacheEntry cacheEntry)
		throws Exception {

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				byteArrayOutputStream)) {

			objectOutputStream.writeObject(cacheEntry);
		}

		try (Jedis jedis = _jedisPool.getResource()) {
			jedis.set(_getBinaryKey(key), byteArrayOutputStream.toByteArray());
		}
	}

	private byte[] _getBinaryKey(String key) {
		return key.getBytes(StandardCharsets.UTF_8);
	}

	private static final Log _log = LogFactory.getLog(RedisCacheImpl.class);

	private final JedisPool _jedisPool;

}