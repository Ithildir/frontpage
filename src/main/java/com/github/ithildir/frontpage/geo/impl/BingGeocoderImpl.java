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

package com.github.ithildir.frontpage.geo.impl;

import com.github.ithildir.frontpage.geo.Geocoder;
import com.github.ithildir.frontpage.http.HttpClient;
import com.github.ithildir.frontpage.model.Location;
import com.github.ithildir.frontpage.util.JsonHelper;

import com.google.common.base.Strings;

import java.io.IOException;

import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Andrea Di Giorgi
 */
public class BingGeocoderImpl implements Geocoder {

	public BingGeocoderImpl(
		HttpClient httpClient, JsonHelper jsonHelper, String key) {

		_httpClient = httpClient;
		_jsonHelper = jsonHelper;

		_key = Objects.requireNonNull(key);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void geocode(Location location) throws Exception {
		StringBuilder sb = new StringBuilder();

		sb.append("https://dev.virtualearth.net/REST/v1/Locations?key=");
		sb.append(_key);
		sb.append("&maxResults=1");

		List<String> regions = location.getRegions();

		if ((regions != null) && !regions.isEmpty()) {
			_addParameter(sb, "adminDistrict", regions.get(0));
		}

		_addParameter(sb, "countryRegion", location.getCountry());
		_addParameter(sb, "locality", location.getCity());
		_addParameter(sb, "postalCode", location.getZip());

		String json = _httpClient.get(sb.toString(), Collections.emptyMap());

		Map<String, Object> map = _jsonHelper.deserialize(json);

		List<?> resourceSets = (List<?>)map.get("resourceSets");

		if ((resourceSets == null) || resourceSets.isEmpty()) {
			return;
		}

		Map<String, Object> resourceSet = (Map<String, Object>)resourceSets.get(
			0);

		List<?> resources = (List<?>)resourceSet.get("resources");

		if (resources.isEmpty()) {
			return;
		}

		Map<String, Object> resource = (Map<String, Object>)resources.get(0);

		Map<String, String> address = (Map<String, String>)resource.get(
			"address");

		location.setCity(address.get("locality"));
		location.setCountry(address.get("countryRegion"));
		location.setRegions(_getRegions(address));
		location.setZip(address.get("postalCode"));
	}

	private void _addParameter(StringBuilder sb, String key, String value)
		throws IOException {

		if (Strings.isNullOrEmpty(value)) {
			return;
		}

		sb.append('&');
		sb.append(key);
		sb.append('=');
		sb.append(URLEncoder.encode(value, StandardCharsets.UTF_8.name()));
	}

	private List<String> _getRegions(Map<String, String> address) {
		List<String> regions = new ArrayList<>();

		for (Map.Entry<String, String> entry : address.entrySet()) {
			String key = entry.getKey();

			if (!key.startsWith("adminDistrict")) {
				continue;
			}

			int index = 0;

			if (key.length() > 13) {
				index = Integer.parseInt(key.substring(13)) - 1;
			}

			regions.add(index, entry.getValue());
		}

		return regions;
	}

	private final HttpClient _httpClient;
	private final JsonHelper _jsonHelper;
	private final String _key;

}