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

package com.github.ithildir.frontpage.util;

import com.github.ithildir.frontpage.auth.OpenIdConfiguration;
import com.github.ithildir.frontpage.model.BotActivity;
import com.github.ithildir.frontpage.model.BotChannelAccount;
import com.github.ithildir.frontpage.model.BotConversationAccount;
import com.github.ithildir.frontpage.nlp.NLPEntity;
import com.github.ithildir.frontpage.nlp.impl.WitAiNLPMessage;

import java.nio.charset.StandardCharsets;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import java.util.Arrays;

import org.apache.commons.io.IOUtils;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Andrea Di Giorgi
 */
public class JsonHelperTest {

	@BeforeClass
	public static void setUpClass() {
		_jsonHelper = new JsonHelper();
	}

	@Test
	public void testDeserializeBotActivity() throws Exception {
		BotActivity botActivity = _deserialize(
			"bot_activity.json", BotActivity.class);

		Assert.assertEquals("channel's name/id", botActivity.getChannelId());

		BotConversationAccount botConversationAccount =
			botActivity.getConversation();

		Assert.assertEquals("abcd1234", botConversationAccount.getId());
		Assert.assertEquals(
			"conversation's name", botConversationAccount.getName());

		_assertEquals(botActivity.getFrom(), "1234abcd", "user's name");
		_assertEquals(botActivity.getRecipient(), "12345678", "bot's name");

		Assert.assertEquals("bf3cc9a2f5de...", botActivity.getId());

		Assert.assertEquals("message", botActivity.getType());
		Assert.assertEquals(
			"https://smba.trafficmanager.net/apis",
			botActivity.getServiceUrl());
		Assert.assertEquals("Haircut on Saturday", botActivity.getText());
		Assert.assertEquals("message", botActivity.getType());

		Assert.assertEquals(
			OffsetDateTime.of(
				2016, 10, 19, 20, 17, 52, 289190200, ZoneOffset.UTC),
			botActivity.getTimestamp());
	}

	@Test
	public void testDeserializeOpenIdConfiguration() throws Exception {
		OpenIdConfiguration openIdConfiguration = _deserialize(
			"open_id_configuration.json", OpenIdConfiguration.class);

		Assert.assertEquals(
			"https://api.botframework.com", openIdConfiguration.getIssuer());
		Assert.assertEquals(
			"https://login.botframework.com/v1/.well-known/keys",
			openIdConfiguration.getJwksUri());
		Assert.assertArrayEquals(
			new String[] {"RS256"},
			openIdConfiguration.getSupportedAlgorithms());
	}

	@Test
	public void testDeserializeWitAiMessage() throws Exception {
		WitAiNLPMessage witAiNLPMessage = _deserialize(
			"wit_ai_message.json", WitAiNLPMessage.class);

		NLPEntity[] nlpEntities = witAiNLPMessage.getEntities("datetime");

		Assert.assertEquals(
			Arrays.toString(nlpEntities), 2, nlpEntities.length);

		NLPEntity nlpEntity = nlpEntities[0];

		Assert.assertEquals(
			"2014-07-01T00:00:00.000-07:00", nlpEntity.getValue("from"));
		Assert.assertEquals(
			"2014-07-02T00:00:00.000-07:00", nlpEntity.getValue("to"));

		nlpEntity = nlpEntities[1];

		Assert.assertEquals(
			"2014-07-04T00:00:00.000-07:00", nlpEntity.getValue("from"));
		Assert.assertEquals(
			"2014-07-05T00:00:00.000-07:00", nlpEntity.getValue("to"));

		nlpEntities = witAiNLPMessage.getEntities("metric");

		Assert.assertEquals(
			Arrays.toString(nlpEntities), 1, nlpEntities.length);

		nlpEntity = nlpEntities[0];

		Assert.assertEquals("metric_visitor", nlpEntity.getValue("value"));
	}

	private static void _assertEquals(
		BotChannelAccount botChannelAccount, String id, String name) {

		Assert.assertEquals(id, botChannelAccount.getId());
		Assert.assertEquals(name, botChannelAccount.getName());
	}

	private static <T> T _deserialize(String name, Class<T> clazz)
		throws Exception {

		String json = IOUtils.toString(
			JsonHelper.class.getResourceAsStream("dependencies/" + name),
			StandardCharsets.UTF_8);

		return _jsonHelper.deserialize(json, clazz);
	}

	private static JsonHelper _jsonHelper;

}