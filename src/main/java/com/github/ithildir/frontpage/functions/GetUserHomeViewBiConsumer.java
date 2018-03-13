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

import com.fizzed.rocker.runtime.StringBuilderOutput;

import com.github.ithildir.frontpage.auth.BotDirectLineToken;
import com.github.ithildir.frontpage.auth.BotDirectLineTokenGenerator;
import com.github.ithildir.frontpage.http.HttpRequest;
import com.github.ithildir.frontpage.http.HttpResponse;
import com.github.ithildir.frontpage.model.User;
import com.github.ithildir.frontpage.service.UserService;
import com.github.ithildir.frontpage.user.UserAvatarRetriever;
import com.github.ithildir.frontpage.util.LanguageHelper;
import com.github.ithildir.frontpage.views.UserView;

import java.nio.charset.StandardCharsets;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;

import pl.touk.throwing.ThrowingBiConsumer;

/**
 * @author Andrea Di Giorgi
 */
@Singleton
public class GetUserHomeViewBiConsumer
	implements ThrowingBiConsumer<HttpRequest, HttpResponse, Exception> {

	@Inject
	public GetUserHomeViewBiConsumer(
		BotDirectLineTokenGenerator botDirectLineTokenGenerator,
		LanguageHelper languageHelper, UserAvatarRetriever userAvatarRetriever,
		@Named("userLink") Comparator<String> userLinkComparator,
		UserService userService) {

		_botDirectLineTokenGenerator = botDirectLineTokenGenerator;
		_languageHelper = languageHelper;
		_userAvatarRetriever = userAvatarRetriever;
		_userLinkComparator = userLinkComparator;
		_userService = userService;
	}

	@Override
	public void accept(HttpRequest httpRequest, HttpResponse httpResponse)
		throws Exception {

		UserView userView = _getUserView(httpRequest);

		StringBuilderOutput stringBuilderOutput = userView.render(
			StringBuilderOutput.FACTORY);

		httpResponse.setBody(stringBuilderOutput.toString());

		httpResponse.setHeaders(
			Collections.singletonMap(
				HttpHeaders.CONTENT_TYPE, _HTML_CONTENT_TYPE));
	}

	private UserView _getUserView(HttpRequest httpRequest) throws Exception {
		String userId = httpRequest.getParam("id");

		User user = _userService.getUser(userId);

		String viewTitle = user.getFullName();
		String greeting = _languageHelper.format("hi-i-am-x", user);
		String headline = user.getHeadline();
		String avatarUrl = _userAvatarRetriever.getUrl(user, 50);

		Map<String, String> links = new TreeMap<>(_userLinkComparator);

		links.putAll(user.getLinks());

		BotDirectLineToken botDirectLineToken =
			_botDirectLineTokenGenerator.generate();

		return UserView.template(
			_languageHelper, viewTitle, userId, greeting, headline, avatarUrl,
			links, botDirectLineToken.getToken());
	}

	private static final String _HTML_CONTENT_TYPE;

	static {
		ContentType contentType = ContentType.TEXT_HTML.withCharset(
			StandardCharsets.UTF_8);

		_HTML_CONTENT_TYPE = contentType.toString();
	}

	private final BotDirectLineTokenGenerator _botDirectLineTokenGenerator;
	private final LanguageHelper _languageHelper;
	private final UserAvatarRetriever _userAvatarRetriever;
	private final Comparator<String> _userLinkComparator;
	private final UserService _userService;

}