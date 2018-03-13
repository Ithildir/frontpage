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

package com.github.ithildir.frontpage.auth;

import com.github.ithildir.frontpage.cache.Cache;
import com.github.ithildir.frontpage.http.HttpClient;
import com.github.ithildir.frontpage.util.JsonHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Andrea Di Giorgi
 */
public class BotAccessTokenCache {

	public BotAccessTokenCache(
		String botAppId, String botAppPassword, Cache cache,
		HttpClient httpClient, JsonHelper jsonHelper) {

		_botAppId = Objects.requireNonNull(botAppId);
		_botAppPassword = Objects.requireNonNull(botAppPassword);

		_cache = cache;
		_httpClient = httpClient;
		_jsonHelper = jsonHelper;
	}

	public BotAccessToken getBotAccessToken() throws Exception {
		return _cache.get(
			_CACHE_KEY, BotAccessToken::getMaxAge, this::_getBotAccessToken);
	}

	public BotAccessToken getNewBotAccessToken() throws Exception {
		BotAccessToken botAccessToken = _getBotAccessToken();

		_cache.put(_CACHE_KEY, botAccessToken);

		return botAccessToken;
	}

	private BotAccessToken _getBotAccessToken() throws Exception {
		Map<String, String> formParameters = new HashMap<>();

		formParameters.put("client_id", _botAppId);
		formParameters.put("client_secret", _botAppPassword);
		formParameters.put("grant_type", "client_credentials");
		formParameters.put("scope", "https://api.botframework.com/.default");

		String json = _httpClient.post(
			_URL, Collections.emptyMap(), formParameters);

		return _jsonHelper.deserialize(json, BotAccessToken.class);
	}

	private static final String _CACHE_KEY =
		BotAccessTokenCache.class.getName();

	private static final String _URL =
		"https://login.microsoftonline.com/botframework.com/oauth2/v2.0/token";

	private final String _botAppId;
	private final String _botAppPassword;
	private final Cache _cache;
	private final HttpClient _httpClient;
	private final JsonHelper _jsonHelper;

}