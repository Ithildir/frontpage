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

package com.github.ithildir.frontpage.aws.http;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import com.github.ithildir.frontpage.http.HttpResponse;

import java.util.Map;

/**
 * @author Andrea Di Giorgi
 */
public class APIGatewayProxyResponseEventHttpResponseImpl
	implements HttpResponse {

	public APIGatewayProxyResponseEventHttpResponseImpl(
		APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent) {

		_apiGatewayProxyResponseEvent = apiGatewayProxyResponseEvent;
	}

	@Override
	public void setBody(String body) {
		_apiGatewayProxyResponseEvent.setBody(body);
	}

	@Override
	public void setHeaders(Map<String, String> headers) {
		_apiGatewayProxyResponseEvent.setHeaders(headers);
	}

	@Override
	public void setStatusCode(Integer statusCode) {
		_apiGatewayProxyResponseEvent.setStatusCode(statusCode);
	}

	@Override
	public String toString() {
		return _apiGatewayProxyResponseEvent.toString();
	}

	private final APIGatewayProxyResponseEvent _apiGatewayProxyResponseEvent;

}