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

package com.github.ithildir.frontpage.queue.impl;

import com.github.ithildir.frontpage.queue.QueueManager;
import com.github.ithildir.frontpage.queue.QueueMessage;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Andrea Di Giorgi
 */
public class DummyQueueManagerImpl implements QueueManager {

	public DummyQueueManagerImpl(
		Map<String, Supplier<Consumer<QueueMessage>>> topicConsumerSuppliers) {

		_topicConsumerSuppliers = topicConsumerSuppliers;
	}

	@Override
	public void sendMessage(String topic, String body) {
		Supplier<Consumer<QueueMessage>> supplier = _topicConsumerSuppliers.get(
			topic);

		if (supplier == null) {
			_log.error("No consumer for \"" + topic + "\" is registered");

			return;
		}

		Consumer<QueueMessage> consumer = supplier.get();

		QueueMessage queueMessage = new DummyQueueMessageImpl(topic, body);

		consumer.accept(queueMessage);
	}

	private static final Log _log = LogFactory.getLog(
		DummyQueueManagerImpl.class);

	private final Map<String, Supplier<Consumer<QueueMessage>>>
		_topicConsumerSuppliers;

}