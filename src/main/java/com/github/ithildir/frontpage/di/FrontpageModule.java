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

package com.github.ithildir.frontpage.di;

import com.github.ithildir.frontpage.auth.BotAccessTokenCache;
import com.github.ithildir.frontpage.auth.BotDirectLineTokenGenerator;
import com.github.ithildir.frontpage.auth.OpenIdMetadataCache;
import com.github.ithildir.frontpage.cache.Cache;
import com.github.ithildir.frontpage.cache.impl.LocalCacheImpl;
import com.github.ithildir.frontpage.cache.impl.RedisCacheImpl;
import com.github.ithildir.frontpage.functions.AcceptUserBotMessageBiConsumer;
import com.github.ithildir.frontpage.geo.Geocoder;
import com.github.ithildir.frontpage.geo.impl.BingGeocoderImpl;
import com.github.ithildir.frontpage.http.HttpClient;
import com.github.ithildir.frontpage.nlp.NLPEngine;
import com.github.ithildir.frontpage.nlp.impl.WitAiNLPEngineImpl;
import com.github.ithildir.frontpage.queue.QueueManager;
import com.github.ithildir.frontpage.queue.QueueTopicKeys;
import com.github.ithildir.frontpage.user.UserGenderDetector;
import com.github.ithildir.frontpage.user.linkedin.CausesUserLinkedInProcessorImpl;
import com.github.ithildir.frontpage.user.linkedin.LanguagesUserLinkedInProcessorImpl;
import com.github.ithildir.frontpage.user.linkedin.ProfileUserLinkedInProcessorImpl;
import com.github.ithildir.frontpage.user.linkedin.SkillsUserLinkedInProcessorImpl;
import com.github.ithildir.frontpage.user.linkedin.UserLinkedInProcessor;
import com.github.ithildir.frontpage.util.CaseInsensitiveMap;
import com.github.ithildir.frontpage.util.ConfigurationHelper;
import com.github.ithildir.frontpage.util.JsonHelper;
import com.github.ithildir.frontpage.util.LocaleHelper;
import com.github.ithildir.frontpage.util.comparator.PriorityKeyComparator;

import dagger.Module;
import dagger.Provides;

import java.util.Comparator;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author Andrea Di Giorgi
 */
@Module
public class FrontpageModule {

	@Provides
	@Singleton
	public static AcceptUserBotMessageBiConsumer
		provideAcceptUserBotMessageConsumer(
			ConfigurationHelper configurationHelper, JsonHelper jsonHelper,
			@Named("bot") OpenIdMetadataCache openIdMetadataCache,
			QueueManager queueManager) {

		String botAppId = configurationHelper.getString(_USER_BOT_APP_ID);
		String botReplyQueueTopic = configurationHelper.getString(
			QueueTopicKeys.USER_BOT_REPLY);

		return new AcceptUserBotMessageBiConsumer(
			botAppId, botReplyQueueTopic, jsonHelper, openIdMetadataCache,
			queueManager);
	}

	@Named("bot")
	@Provides
	@Singleton
	public static OpenIdMetadataCache
		provideAcceptUserBotMessageOpenIdMetadataCache(
			Cache cache, ConfigurationHelper configurationHelper,
			HttpClient httpClient, JsonHelper jsonHelper) {

		return new OpenIdMetadataCache(
			_BOT_OPEN_ID_CONFIGURATION_URL, _BOT_OPEN_ID_MAX_AGE, cache,
			httpClient, jsonHelper);
	}

	@Provides
	@Singleton
	public static BotAccessTokenCache provideBotAccessTokenCache(
		Cache cache, ConfigurationHelper configurationHelper,
		HttpClient httpClient, JsonHelper jsonHelper) {

		String botAppId = configurationHelper.getString(_USER_BOT_APP_ID);
		String botAppPassword = configurationHelper.getString(
			_USER_BOT_APP_PASSWORD);

		return new BotAccessTokenCache(
			botAppId, botAppPassword, cache, httpClient, jsonHelper);
	}

	@Provides
	@Singleton
	public static BotDirectLineTokenGenerator
		provideBotDirectLineTokenGenerator(
			ConfigurationHelper configurationHelper, HttpClient httpClient,
			JsonHelper jsonHelper) {

		String secret = configurationHelper.getString(
			_USER_BOT_DIRECT_LINE_SECRET);

		return new BotDirectLineTokenGenerator(httpClient, jsonHelper, secret);
	}

	@Provides
	@Singleton
	public static Cache provideCache(ConfigurationHelper configurationHelper) {
		String host = configurationHelper.getString(_REDIS_HOST);
		int port = configurationHelper.getInteger(_REDIS_PORT);
		String password = configurationHelper.getString(_REDIS_PASSWORD);

		Cache cache = new RedisCacheImpl(null, host, port, password);

		return new LocalCacheImpl(cache);
	}

	@Provides
	@Singleton
	public static Geocoder provideGeocoder(
		ConfigurationHelper configurationHelper, HttpClient httpClient,
		JsonHelper jsonHelper) {

		String key = configurationHelper.getString(_BING_GEOCODER_KEY);

		return new BingGeocoderImpl(httpClient, jsonHelper, key);
	}

	@Named("userLink")
	@Provides
	@Singleton
	public static Comparator<String> provideUserLinkComparator(
		ConfigurationHelper configurationHelper) {

		String[] priorityKeys = configurationHelper.getStrings(
			_USER_LINK_PRIORITY_KEYS);

		return new PriorityKeyComparator(priorityKeys);
	}

	@Provides
	@Singleton
	public static Map<String, UserLinkedInProcessor>
		provideUserLinkedInProcessors(
			Geocoder geocoder, LocaleHelper localeHelper,
			UserGenderDetector userGenderDetector) {

		Map<String, UserLinkedInProcessor> userLinkedInProcessors =
			new CaseInsensitiveMap<>();

		userLinkedInProcessors.put(
			"Causes You Care About.csv", new CausesUserLinkedInProcessorImpl());
		userLinkedInProcessors.put(
			"Languages.csv", new LanguagesUserLinkedInProcessorImpl());
		userLinkedInProcessors.put(
			"Profile.csv",
			new ProfileUserLinkedInProcessorImpl(
				geocoder, localeHelper, userGenderDetector));
		userLinkedInProcessors.put(
			"Skills.csv", new SkillsUserLinkedInProcessorImpl());

		return userLinkedInProcessors;
	}

	@Named("user")
	@Provides
	@Singleton
	public static NLPEngine provideUserNLPEngine(
		ConfigurationHelper configurationHelper, HttpClient httpClient,
		JsonHelper jsonHelper) {

		String token = configurationHelper.getString(_USER_WIT_AI_TOKEN);

		return new WitAiNLPEngineImpl(httpClient, jsonHelper, token);
	}

	private static final String _BING_GEOCODER_KEY = "BING_GEOCODER_KEY";

	private static final String _BOT_OPEN_ID_CONFIGURATION_URL =
		"https://login.botframework.com/v1/.well-known/openidconfiguration";

	private static final int _BOT_OPEN_ID_MAX_AGE = 432000000;

	private static final String _REDIS_HOST = "REDIS_HOST";

	private static final String _REDIS_PASSWORD = "REDIS_PASSWORD";

	private static final String _REDIS_PORT = "REDIS_PORT";

	private static final String _USER_BOT_APP_ID = "USER_BOT_APP_ID";

	private static final String _USER_BOT_APP_PASSWORD =
		"USER_BOT_APP_PASSWORD";

	private static final String _USER_BOT_DIRECT_LINE_SECRET =
		"USER_BOT_DIRECT_LINE_SECRET";

	private static final String _USER_LINK_PRIORITY_KEYS =
		"USER_LINK_PRIORITY_KEYS";

	private static final String _USER_WIT_AI_TOKEN = "USER_WIT_AI_TOKEN";

}