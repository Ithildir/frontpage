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

import com.github.ithildir.frontpage.aws.di.AwsFrontpageModule;
import com.github.ithildir.frontpage.functions.AcceptUserBotMessageBiConsumer;
import com.github.ithildir.frontpage.functions.GetUserHomeViewBiConsumer;
import com.github.ithildir.frontpage.functions.ProcessUserLinkedInBiConsumer;
import com.github.ithildir.frontpage.functions.SendUserBotReplyConsumer;
import com.github.ithildir.frontpage.user.intent.di.UserIntentModule;

import dagger.Component;

import javax.inject.Singleton;

/**
 * @author Andrea Di Giorgi
 */
@Component(
	modules = {
		AwsFrontpageModule.class, FrontpageModule.class, UserIntentModule.class
	}
)
@Singleton
public interface FrontpageComponent {

	public static final FrontpageComponent INSTANCE =
		DaggerFrontpageComponent.create();

	public AcceptUserBotMessageBiConsumer getAcceptUserBotMessageBiConsumer();

	public ProcessUserLinkedInBiConsumer getProcessUserLinkedInBiConsumer();

	public SendUserBotReplyConsumer getSendUserBotReplyConsumer();

	public GetUserHomeViewBiConsumer getUserHomeViewBiConsumer();

}