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

import com.github.ithildir.frontpage.auth.BotAccessToken;
import com.github.ithildir.frontpage.auth.BotAccessTokenCache;
import com.github.ithildir.frontpage.http.HttpClient;
import com.github.ithildir.frontpage.http.HttpClientException;
import com.github.ithildir.frontpage.model.BotActivity;
import com.github.ithildir.frontpage.model.BotChannelAccount;
import com.github.ithildir.frontpage.model.BotConversationAccount;
import com.github.ithildir.frontpage.model.User;
import com.github.ithildir.frontpage.nlp.NLPEngine;
import com.github.ithildir.frontpage.nlp.NLPMessage;
import com.github.ithildir.frontpage.queue.QueueMessage;
import com.github.ithildir.frontpage.service.UserService;
import com.github.ithildir.frontpage.user.intent.UserIntentBotReplier;
import com.github.ithildir.frontpage.util.JsonHelper;

import java.io.IOException;

import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;

import pl.touk.throwing.ThrowingConsumer;

/**
 * @author Andrea Di Giorgi
 */
@Singleton
public class SendUserBotReplyConsumer
	implements ThrowingConsumer<QueueMessage, Exception> {

	@Inject
	public SendUserBotReplyConsumer(
		BotAccessTokenCache botAccessTokenCache, HttpClient httpClient,
		Map<String, UserIntentBotReplier> userIntentBotRepliers,
		JsonHelper jsonHelper, @Named("user") NLPEngine nlpEngine,
		UserService userService) {

		_botAccessTokenCache = botAccessTokenCache;
		_httpClient = httpClient;
		_userIntentBotRepliers = userIntentBotRepliers;
		_jsonHelper = jsonHelper;
		_nlpEngine = nlpEngine;
		_userService = userService;
	}

	@Override
	public void accept(QueueMessage queueMessage) throws Exception {
		BotActivity botActivity = _jsonHelper.deserialize(
			queueMessage.getBody(), BotActivity.class);

		String url = _getReplyURL(botActivity);

		BotAccessToken botAccessToken =
			_botAccessTokenCache.getBotAccessToken();

		BotActivity[] replyBotActivities = _getReplyBotActivities(botActivity);

		for (BotActivity replyBotActivity : replyBotActivities) {
			String json = _jsonHelper.serialize(replyBotActivity);

			try {
				_sendBotReply(url, json, botAccessToken);
			}
			catch (HttpClientException hce) {
				if (hce.getCode() == HttpStatus.SC_UNAUTHORIZED) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Access token " + botAccessToken +
								" unexpectedly expired",
							hce);
					}

					botAccessToken =
						_botAccessTokenCache.getNewBotAccessToken();

					_sendBotReply(url, json, botAccessToken);
				}
			}
		}
	}

	private BotActivity[] _getReplyBotActivities(BotActivity botActivity)
		throws Exception {

		BotChannelAccount fromBotChannelAccount = botActivity.getFrom();

		User user = _userService.getUser(fromBotChannelAccount.getId());

		NLPMessage nlpMessage = _nlpEngine.interpret(botActivity.getText());

		UserIntentBotReplier userIntentBotReplier = _userIntentBotRepliers.get(
			nlpMessage.getIntent());

		return userIntentBotReplier.buildReplyBotActivities(
			user,
			() -> {
				BotActivity replyBotActivity = new BotActivity();

				replyBotActivity.setFrom(botActivity.getRecipient());
				replyBotActivity.setRecipient(fromBotChannelAccount);
				replyBotActivity.setType("message");

				return replyBotActivity;
			});
	}

	private String _getReplyURL(BotActivity botActivity) throws IOException {
		StringBuilder sb = new StringBuilder();

		sb.append(botActivity.getServiceUrl());

		if (sb.charAt(sb.length() - 1) != '/') {
			sb.append('/');
		}

		sb.append("v3/conversations/");

		BotConversationAccount botConversationAccount =
			botActivity.getConversation();

		sb.append(botConversationAccount.getId());

		sb.append("/activities");

		return sb.toString();
	}

	private void _sendBotReply(
			String url, String body, BotAccessToken botAccessToken)
		throws IOException {

		String authorization =
			botAccessToken.getTokenType() + " " +
				botAccessToken.getAccessToken();

		_httpClient.post(
			url,
			Collections.singletonMap(HttpHeaders.AUTHORIZATION, authorization),
			body, ContentType.APPLICATION_JSON);
	}

	private static final Log _log = LogFactory.getLog(
		SendUserBotReplyConsumer.class);

	private final BotAccessTokenCache _botAccessTokenCache;
	private final HttpClient _httpClient;
	private final JsonHelper _jsonHelper;
	private final NLPEngine _nlpEngine;
	private Map<String, UserIntentBotReplier> _userIntentBotRepliers;
	private final UserService _userService;

}