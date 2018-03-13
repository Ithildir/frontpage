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

package com.github.ithildir.frontpage.user.intent;

import com.github.ithildir.frontpage.constants.BotAttachmentContentTypes;
import com.github.ithildir.frontpage.model.BotActivity;
import com.github.ithildir.frontpage.model.BotAttachment;
import com.github.ithildir.frontpage.model.BotCardAction;
import com.github.ithildir.frontpage.model.BotHeroCard;
import com.github.ithildir.frontpage.model.User;
import com.github.ithildir.frontpage.util.LanguageHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author Andrea Di Giorgi
 */
public class GreetingsUserIntentBotReplierImpl implements UserIntentBotReplier {

	public GreetingsUserIntentBotReplierImpl(LanguageHelper languageHelper) {
		_languageHelper = languageHelper;
	}

	@Override
	public BotActivity[] buildReplyBotActivities(
			User user, Supplier<BotActivity> botActivitySupplier)
		throws Exception {

		return new BotActivity[] {
			_getGreetingsBotActivity(user, botActivitySupplier),
			_getSuggestionsBotActivity(user, botActivitySupplier)
		};
	}

	private List<BotCardAction> _getButtonBotCardActions(
		User user, String... keys) {

		List<BotCardAction> botCardActions = new ArrayList<>(keys.length);

		for (String key : keys) {
			BotCardAction botCardAction = new BotCardAction();

			String title = _languageHelper.format(key, user);

			botCardAction.setTitle(title);

			botCardAction.setType("imBack");

			botCardActions.add(botCardAction);
		}

		return botCardActions;
	}

	private BotActivity _getGreetingsBotActivity(
		User user, Supplier<BotActivity> botActivitySupplier) {

		BotActivity botActivity = botActivitySupplier.get();

		botActivity.setText(
			_languageHelper.format("hi-i-am-x-personal-chat-bot", user));

		return botActivity;
	}

	private BotActivity _getSuggestionsBotActivity(
		User user, Supplier<BotActivity> botActivitySupplier) {

		BotActivity botActivity = botActivitySupplier.get();

		BotAttachment botAttachment = new BotAttachment();

		BotHeroCard botHeroCard = new BotHeroCard();

		botHeroCard.setButtons(
			_getButtonBotCardActions(
				user, "tell-me-about-her", "send-her-a-message",
				"show-me-her-resume"));

		botAttachment.setContent(botHeroCard);

		botAttachment.setContentType(BotAttachmentContentTypes.HERO_CARD);

		botActivity.setAttachments(Collections.singletonList(botAttachment));

		botActivity.setText(
			_languageHelper.get("here-are-some-suggestions-to-start"));

		return botActivity;
	}

	private final LanguageHelper _languageHelper;

}