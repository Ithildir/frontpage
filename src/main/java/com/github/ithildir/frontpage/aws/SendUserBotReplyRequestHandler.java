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

package com.github.ithildir.frontpage.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNSRecord;

import com.github.ithildir.frontpage.aws.queue.AmazonSNSQueueMessageImpl;
import com.github.ithildir.frontpage.di.DaggerFrontpageComponent;
import com.github.ithildir.frontpage.queue.QueueMessage;

import java.util.function.Consumer;

import pl.touk.throwing.ThrowingConsumer;

/**
 * @author Andrea Di Giorgi
 */
public class SendUserBotReplyRequestHandler
	implements RequestHandler<SNSEvent, Void> {

	@Override
	public Void handleRequest(SNSEvent snsEvent, Context context) {
		for (SNSRecord snsRecord : snsEvent.getRecords()) {
			_consumer.accept(new AmazonSNSQueueMessageImpl(snsRecord.getSNS()));
		}

		return null;
	}

	private static Consumer<QueueMessage> _consumer;

	static {
		ThrowingConsumer<QueueMessage, Exception> throwingConsumer =
			DaggerFrontpageComponent.INSTANCE.getSendUserBotReplyConsumer();

		_consumer = throwingConsumer.uncheck();
	}

}