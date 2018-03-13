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

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.interfaces.RSAKeyProvider;

import com.github.ithildir.frontpage.cache.Cache;
import com.github.ithildir.frontpage.http.HttpClient;
import com.github.ithildir.frontpage.util.JsonHelper;

import java.net.URL;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import java.util.Collections;
import java.util.Objects;

/**
 * @author Andrea Di Giorgi
 */
public class OpenIdMetadataCache implements RSAKeyProvider {

	public OpenIdMetadataCache(
		String configurationUrl, long maxAge, Cache cache,
		HttpClient httpClient, JsonHelper jsonHelper) {

		_configurationUrl = Objects.requireNonNull(configurationUrl);
		_maxAge = maxAge;
		_cache = cache;
		_httpClient = httpClient;
		_jsonHelper = jsonHelper;
	}

	public String getIssuer() throws Exception {
		OpenIdConfiguration openIdConfiguration = _getOpenIdConfiguration();

		return openIdConfiguration.getIssuer();
	}

	@Override
	public RSAPrivateKey getPrivateKey() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getPrivateKeyId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public RSAPublicKey getPublicKeyById(String keyId) {
		try {
			OpenIdMetadata openIdMetadata = _getOpenIdMetadata(keyId);

			return openIdMetadata.getRSAPublicKey();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private OpenIdConfiguration _getOpenIdConfiguration() throws Exception {
		return _cache.get(
			OpenIdMetadataCache.class.getName() + "#openIdConfiguration",
			_maxAge,
			() -> {
				String json = _httpClient.get(
					_configurationUrl, Collections.emptyMap());

				return _jsonHelper.deserialize(json, OpenIdConfiguration.class);
			});
	}

	private OpenIdMetadata _getOpenIdMetadata(String keyId) throws Exception {
		return _cache.get(
			OpenIdMetadataCache.class.getName() + "#" + keyId, _maxAge,
			() -> {
				OpenIdConfiguration openIdConfiguration =
					_getOpenIdConfiguration();

				JwkProvider jwkProvider = new UrlJwkProvider(
					new URL(openIdConfiguration.getJwksUri()));

				Jwk jwk = jwkProvider.get(keyId);

				return new OpenIdMetadata(
					(RSAPublicKey)jwk.getPublicKey(),
					jwk.getAdditionalAttributes());
			});
	}

	private final Cache _cache;
	private final String _configurationUrl;
	private final HttpClient _httpClient;
	private final JsonHelper _jsonHelper;
	private final long _maxAge;

}