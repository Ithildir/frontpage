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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Andrea Di Giorgi
 */
public class BotDirectLineToken {

	public BotDirectLineToken(
		@JsonProperty("conversationId") String conversationId,
		@JsonProperty("expires_in") long expiresIn,
		@JsonProperty("token") String token) {

		_conversationId = conversationId;
		_expiresIn = expiresIn;
		_token = token;
	}

	public String getConversationId() {
		return _conversationId;
	}

	public long getExpiresIn() {
		return _expiresIn;
	}

	public String getToken() {
		return _token;
	}

	private final String _conversationId;
	private final long _expiresIn;
	private final String _token;

}