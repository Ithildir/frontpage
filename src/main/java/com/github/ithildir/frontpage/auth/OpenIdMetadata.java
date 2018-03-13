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

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import java.io.Serializable;

import java.security.interfaces.RSAPublicKey;

import java.util.Map;

/**
 * @author Andrea Di Giorgi
 */
public class OpenIdMetadata implements Serializable {

	public OpenIdMetadata(
		RSAPublicKey rsaPublicKey, Map<String, Object> attributes) {

		_rsaPublicKey = rsaPublicKey;
		_attributes = attributes;
	}

	public Object getAttribute(String key) {
		return _attributes.get(key);
	}

	public RSAPublicKey getRSAPublicKey() {
		return _rsaPublicKey;
	}

	@Override
	public String toString() {
		ToStringHelper toStringHelper = MoreObjects.toStringHelper(this);

		toStringHelper.add("rsaPublicKey", getRSAPublicKey());
		toStringHelper.add("attributes", _attributes);

		return toStringHelper.toString();
	}

	private static final long serialVersionUID = 9159517244793953900L;

	private final Map<String, Object> _attributes;
	private final RSAPublicKey _rsaPublicKey;

}