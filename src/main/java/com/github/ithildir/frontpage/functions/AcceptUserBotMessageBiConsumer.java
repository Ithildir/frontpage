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

package com.github.ithildir.frontpage.functions;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;

import com.github.ithildir.frontpage.auth.OpenIdMetadataCache;
import com.github.ithildir.frontpage.http.HttpRequest;
import com.github.ithildir.frontpage.http.HttpResponse;
import com.github.ithildir.frontpage.model.BotActivity;
import com.github.ithildir.frontpage.queue.QueueManager;
import com.github.ithildir.frontpage.util.JsonHelper;

import com.google.common.base.Strings;

import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;

import pl.touk.throwing.ThrowingBiConsumer;

/**
 * @author Andrea Di Giorgi
 */
public class AcceptUserBotMessageBiConsumer
	implements ThrowingBiConsumer<HttpRequest, HttpResponse, Exception> {

	public AcceptUserBotMessageBiConsumer(
		String botAppId, String botReplyQueueTopic, JsonHelper jsonHelper,
		OpenIdMetadataCache openIdMetadataCache, QueueManager queueManager) {

		_jsonHelper = jsonHelper;
		_openIdMetadataCache = openIdMetadataCache;
		_queueManager = queueManager;

		_botAppId = Objects.requireNonNull(botAppId);
		_botReplyQueueTopic = Objects.requireNonNull(botReplyQueueTopic);
	}

	@Override
	public void accept(HttpRequest httpRequest, HttpResponse httpResponse)
		throws Exception {

		String body = httpRequest.getBody();

		BotActivity botActivity = _jsonHelper.deserialize(
			body, BotActivity.class);

		if (!Objects.equals(botActivity.getType(), "message")) {
			return;
		}

		if (!_isAuthorized(botActivity, httpRequest)) {
			httpResponse.setStatusCode(HttpStatus.SC_UNAUTHORIZED);

			return;
		}

		_queueManager.sendMessage(_botReplyQueueTopic, body);
	}

	private boolean _isAuthorized(
			BotActivity botActivity, HttpRequest httpRequest)
		throws Exception {

		String authorization = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);

		if (Strings.isNullOrEmpty(authorization)) {
			_log.error(
				"Request " + httpRequest + " does not have a " +
					HttpHeaders.AUTHORIZATION + " header");

			return false;
		}

		int pos = authorization.indexOf(' ');

		if (pos == -1) {
			_log.error(
				"Request " + httpRequest + " does not have a valid " +
					HttpHeaders.AUTHORIZATION + " header");

			return false;
		}

		String scheme = authorization.substring(0, pos);
		String token = authorization.substring(pos + 1);

		if (!scheme.equalsIgnoreCase("bearer") || token.isEmpty()) {
			_log.error(
				"Request " + httpRequest + " does not have a valid " +
					HttpHeaders.AUTHORIZATION + " header");

			return false;
		}

		Algorithm algorithm = Algorithm.RSA256(_openIdMetadataCache);

		Verification verification = JWT.require(algorithm);

		verification.acceptExpiresAt(System.currentTimeMillis() + 500);
		verification.acceptNotBefore(0);
		verification.withAudience(_botAppId);
		verification.withClaim("serviceurl", botActivity.getServiceUrl());
		verification.withIssuer(_openIdMetadataCache.getIssuer());

		JWTVerifier jwtVerifier = verification.build();

		try {
			DecodedJWT decodedJWT = jwtVerifier.verify(token);

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Request " + httpRequest +
						" authorized succesfully with token " +
							decodedJWT.getId());
			}
		}
		catch (JWTVerificationException jwtve) {
			_log.error(
				"Request " + httpRequest + " authorization failed", jwtve);

			return false;
		}

		return true;
	}

	private static final Log _log = LogFactory.getLog(
		AcceptUserBotMessageBiConsumer.class);

	private final String _botAppId;
	private final String _botReplyQueueTopic;
	private final JsonHelper _jsonHelper;
	private final OpenIdMetadataCache _openIdMetadataCache;
	private final QueueManager _queueManager;

}