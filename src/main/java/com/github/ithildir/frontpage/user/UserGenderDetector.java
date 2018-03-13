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

import com.github.ithildir.frontpage.http.HttpClient;
import com.github.ithildir.frontpage.util.JsonHelper;

import com.google.common.base.Strings;

import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;

import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Andrea Di Giorgi
 */
@Singleton
public class UserGenderDetector {

	@Inject
	public UserGenderDetector(HttpClient httpClient, JsonHelper jsonHelper) {
		_httpClient = httpClient;
		_jsonHelper = jsonHelper;
	}

	public boolean detectMale(String firstName, String countryCode)
		throws Exception {

		StringBuilder sb = new StringBuilder();

		sb.append("https://api.genderize.io/?name=");
		sb.append(URLEncoder.encode(firstName, StandardCharsets.UTF_8.name()));

		if (!Strings.isNullOrEmpty(countryCode)) {
			sb.append("&country_id=");
			sb.append(
				URLEncoder.encode(countryCode, StandardCharsets.UTF_8.name()));
		}

		String json = _httpClient.get(sb.toString(), Collections.emptyMap());

		Map<String, Object> map = _jsonHelper.deserialize(json);

		String gender = (String)map.get("gender");

		if (gender.equals("male")) {
			return true;
		}

		return false;
	}

	private final HttpClient _httpClient;
	private final JsonHelper _jsonHelper;

}