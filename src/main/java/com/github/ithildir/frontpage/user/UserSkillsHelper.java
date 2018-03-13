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

package com.github.ithildir.frontpage.user;

import com.github.ithildir.frontpage.cache.Cache;
import com.github.ithildir.frontpage.http.HttpClient;
import com.github.ithildir.frontpage.util.JsonHelper;
import com.github.ithildir.frontpage.util.NamedUrl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Andrea Di Giorgi
 */
@Singleton
public class UserSkillsHelper {

	@Inject
	public UserSkillsHelper(
		Cache cache, HttpClient httpClient, JsonHelper jsonHelper) {

		_cache = cache;
		_httpClient = httpClient;
		_jsonHelper = jsonHelper;
	}

	public Collection<NamedUrl> getAllProgrammingLanguages() throws Exception {
		Map<String, NamedUrl> allProgrammingLanguages = _cache.get(
			_CACHE_KEY, _CACHE_MAX_AGE, this::_getAllProgrammingLanguages);

		return Collections.unmodifiableCollection(
			allProgrammingLanguages.values());
	}

	public List<NamedUrl> getProgrammingLanguages(List<String> names)
		throws Exception {

		List<NamedUrl> programmingLanguages = new ArrayList<>(names.size());

		Map<String, NamedUrl> allProgrammingLanguages = _cache.get(
			_CACHE_KEY, _CACHE_MAX_AGE, this::_getAllProgrammingLanguages);

		for (String name : names) {
			NamedUrl namedUrl = allProgrammingLanguages.get(
				_getAllProgrammingLanguagesKey(name));

			if (namedUrl != null) {
				programmingLanguages.add(namedUrl);
			}
		}

		return programmingLanguages;
	}

	private HashMap<String, NamedUrl> _getAllProgrammingLanguages()
		throws Exception {

		HashMap<String, NamedUrl> allProgrammingLanguages = new HashMap<>();

		String json = _httpClient.get(_URL, Collections.emptyMap());

		Map<String, Object> map = _jsonHelper.deserialize(json);

		List<Map<String, Object>> elementMaps =
			(List<Map<String, Object>>)map.get("itemListElement");

		for (Map<String, Object> elementMap : elementMaps) {
			Map<String, String> itemMap = (Map<String, String>)elementMap.get(
				"item");

			String name = itemMap.get("name");
			String url = itemMap.get("@id");

			allProgrammingLanguages.put(
				_getAllProgrammingLanguagesKey(name), new NamedUrl(name, url));
		}

		return allProgrammingLanguages;
	}

	private String _getAllProgrammingLanguagesKey(String name) {
		return name.toLowerCase();
	}

	private static final String _CACHE_KEY =
		UserSkillsHelper.class.getName() + "#programmingLanguages";

	private static final long _CACHE_MAX_AGE = TimeUnit.DAYS.toMillis(7);

	private static final String _URL =
		"https://github.com/scienceai/list-of-programming-languages/raw" +
			"/master/data/data.json";

	private final Cache _cache;
	private final HttpClient _httpClient;
	private final JsonHelper _jsonHelper;

}