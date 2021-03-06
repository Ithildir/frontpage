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

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import java.io.Serializable;

/**
 * @author Andrea Di Giorgi
 */
public class BotAccessToken implements Serializable {

	public BotAccessToken(
		@JsonProperty("access_token") String accessToken,
		@JsonProperty("expires_in") int maxAge,
		@JsonProperty("token_type") String tokenType) {

		_accessToken = accessToken;
		_tokenType = tokenType;

		_maxAge = maxAge * 1000;
	}

	public String getAccessToken() {
		return _accessToken;
	}

	public int getMaxAge() {
		return _maxAge;
	}

	public String getTokenType() {
		return _tokenType;
	}

	@Override
	public String toString() {
		ToStringHelper toStringHelper = MoreObjects.toStringHelper(this);

		toStringHelper.add("accessToken", getAccessToken());
		toStringHelper.add("maxAge", getMaxAge());
		toStringHelper.add("tokenType", getTokenType());

		return toStringHelper.toString();
	}

	private static final long serialVersionUID = -3405143612194406502L;

	private final String _accessToken;
	private final int _maxAge;
	private final String _tokenType;

}