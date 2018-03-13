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

package com.github.ithildir.frontpage.user.intent.di;

import com.github.ithildir.frontpage.user.intent.GreetingsUserIntentBotReplierImpl;
import com.github.ithildir.frontpage.user.intent.ResumeUserIntentBotReplierImpl;
import com.github.ithildir.frontpage.user.intent.UserIntentBotReplier;
import com.github.ithildir.frontpage.util.LanguageHelper;

import dagger.Module;
import dagger.Provides;

import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;

import javax.inject.Singleton;

/**
 * @author Andrea Di Giorgi
 */
@Module
public class UserIntentModule {

	@IntoMap
	@Provides
	@Singleton
	@StringKey("greetings")
	public static UserIntentBotReplier provideGreetingsUserIntentBotReplier(
		LanguageHelper languageHelper) {

		return new GreetingsUserIntentBotReplierImpl(languageHelper);
	}

	@IntoMap
	@Provides
	@Singleton
	@StringKey("resume")
	public static UserIntentBotReplier provideResumeUserIntentBotReplier(
		LanguageHelper languageHelper) {

		return new ResumeUserIntentBotReplierImpl(languageHelper);
	}

}