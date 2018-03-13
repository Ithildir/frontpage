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

import java.io.Serializable;

import java.util.Objects;
import java.util.function.ToLongFunction;

import org.apache.commons.logging.Log;

import pl.touk.throwing.ThrowingSupplier;

/**
 * @author Andrea Di Giorgi
 */
public abstract class BaseCacheImpl implements Cache {

	public BaseCacheImpl(Cache parentCache) {
		_parentCache = parentCache;
	}

	@Override
	public <T extends Serializable> T get(
			String key, long maxAge,
			ThrowingSupplier<T, Exception> valueSupplier)
		throws Exception {

		return get(key, value -> maxAge, valueSupplier);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Serializable> T get(
			String key, ToLongFunction<T> maxAgeFunction,
			ThrowingSupplier<T, Exception> valueSupplier)
		throws Exception {

		T value = null;

		Log log = getLog();

		CacheEntry cacheEntry = getCacheEntry(key);

		if (cacheEntry == null) {
			if (_parentCache == null) {
				if (log.isDebugEnabled()) {
					log.debug(
						"Cache miss for '" + key + "', generating new value");
				}

				value = valueSupplier.get();
			}
			else {
				if (log.isDebugEnabled()) {
					log.debug(
						"Cache miss for '" + key + "', asking parent cache");
				}

				value = _parentCache.get(key, maxAgeFunction, valueSupplier);
			}

			put(key, value);
		}
		else if (cacheEntry.isExpired(
					maxAgeFunction.applyAsLong((T)cacheEntry.getValue()))) {

			if (log.isDebugEnabled()) {
				log.debug(
					"Cache hit expired for '" + key +
						"', generating new value: " + cacheEntry);
			}

			try {
				value = valueSupplier.get();

				put(key, value);
			}
			catch (Exception e) {
				if (log.isWarnEnabled()) {
					log.warn(
						"Unable to generate new value for '" + key +
							"', returning expired " + cacheEntry,
						e);
				}

				value = (T)cacheEntry.getValue();
			}
		}
		else {
			if (log.isDebugEnabled()) {
				log.debug("Cache hit for '" + key + "': " + cacheEntry);
			}

			value = (T)cacheEntry.getValue();
		}

		return value;
	}

	@Override
	public <T extends Serializable> void put(String key, T value)
		throws Exception {

		CacheEntry cacheEntry = new CacheEntry(value);

		putCacheEntry(Objects.requireNonNull(key), cacheEntry);

		if (_parentCache != null) {
			_parentCache.put(key, value);
		}
	}

	protected abstract CacheEntry getCacheEntry(String key) throws Exception;

	protected abstract Log getLog();

	protected abstract void putCacheEntry(String key, CacheEntry cacheEntry)
		throws Exception;

	private Cache _parentCache;

}