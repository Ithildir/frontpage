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

package com.github.ithildir.frontpage.http;

import java.io.IOException;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * @author Andrea Di Giorgi
 */
@Singleton
public class HttpClient {

	@Inject
	public HttpClient() {
		_closeableHttpClient = HttpClients.createDefault();
	}

	public String get(String url, Map<String, String> headers)
		throws IOException {

		HttpGet httpGet = new HttpGet(url);

		return _execute(httpGet, headers);
	}

	public String post(
			String url, Map<String, String> headers,
			Map<String, String> formParameters)
		throws IOException {

		HttpPost httpPost = new HttpPost(url);

		List<NameValuePair> nameValuePairs = new ArrayList<>(
			formParameters.size());

		for (Map.Entry<String, String> entry : formParameters.entrySet()) {
			NameValuePair nameValuePair = new BasicNameValuePair(
				entry.getKey(), entry.getValue());

			nameValuePairs.add(nameValuePair);
		}

		UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
			nameValuePairs, StandardCharsets.UTF_8);

		httpPost.setEntity(urlEncodedFormEntity);

		return _execute(httpPost, headers);
	}

	public String post(
			String url, Map<String, String> headers, String body,
			ContentType contentType)
		throws IOException {

		HttpPost httpPost = new HttpPost(url);

		httpPost.setEntity(new StringEntity(body, contentType));

		return _execute(httpPost, headers);
	}

	private String _execute(
			HttpUriRequest httpUriRequest, Map<String, String> headers)
		throws IOException {

		for (Map.Entry<String, String> entry : headers.entrySet()) {
			httpUriRequest.addHeader(entry.getKey(), entry.getValue());
		}

		try (CloseableHttpResponse closeableHttpResponse =
				_closeableHttpClient.execute(httpUriRequest)) {

			StatusLine statusLine = closeableHttpResponse.getStatusLine();

			int statusCode = statusLine.getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				throw new HttpClientException(
					statusCode, httpUriRequest + " failed: " + statusLine);
			}

			return EntityUtils.toString(
				closeableHttpResponse.getEntity(), StandardCharsets.UTF_8);
		}
	}

	private final CloseableHttpClient _closeableHttpClient;

}