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

package com.github.ithildir.frontpage.nlp.impl;

import com.github.ithildir.frontpage.http.HttpClient;
import com.github.ithildir.frontpage.nlp.NLPEngine;
import com.github.ithildir.frontpage.nlp.NLPMessage;
import com.github.ithildir.frontpage.util.JsonHelper;
import com.google.common.net.HttpHeaders;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Objects;

/**
 * @author Andrea Di Giorgi
 */
public class WitAiNLPEngineImpl implements NLPEngine {

	public WitAiNLPEngineImpl(
		HttpClient httpClient, JsonHelper jsonHelper, String token) {

		_httpClient = httpClient;
		_jsonHelper = jsonHelper;

		_token = Objects.requireNonNull(token);
	}

	@Override
	public NLPMessage interpret(String text) throws Exception {
		StringBuilder sb = new StringBuilder();

		sb.append("https://api.wit.ai/message?v=");
		sb.append(_VERSION);
		sb.append("&q=");
		sb.append(URLEncoder.encode(text, StandardCharsets.UTF_8.name()));

		String json = _httpClient.get(
			sb.toString(),
			Collections.singletonMap(
				HttpHeaders.AUTHORIZATION, "Bearer " + _token));

		return _jsonHelper.deserialize(json, WitAiNLPMessage.class);
	}

	private static final String _VERSION = "20180422";

	private final HttpClient _httpClient;
	private final JsonHelper _jsonHelper;
	private final String _token;

}