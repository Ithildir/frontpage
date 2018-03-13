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

package com.github.ithildir.frontpage.aws.di;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.sns.AmazonSNS;

import com.github.ithildir.frontpage.aws.queue.AmazonSNSQueueManagerImpl;
import com.github.ithildir.frontpage.aws.service.AmazonDynamoDBUserServiceImpl;
import com.github.ithildir.frontpage.aws.store.AmazonS3FileStoreImpl;
import com.github.ithildir.frontpage.di.FrontpageComponent;
import com.github.ithildir.frontpage.queue.QueueManager;
import com.github.ithildir.frontpage.queue.QueueMessage;
import com.github.ithildir.frontpage.queue.QueueTopicKeys;
import com.github.ithildir.frontpage.queue.impl.DummyQueueManagerImpl;
import com.github.ithildir.frontpage.service.UserService;
import com.github.ithildir.frontpage.store.FileStore;
import com.github.ithildir.frontpage.util.ConfigurationHelper;

import dagger.Module;
import dagger.Provides;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.inject.Singleton;

import pl.touk.throwing.ThrowingConsumer;

/**
 * @author Andrea Di Giorgi
 */
@Module(includes = AwsModule.class)
public class AwsFrontpageModule {

	@Provides
	@Singleton
	public static FileStore provideFileStore(AmazonS3 amazonS3) {
		return new AmazonS3FileStoreImpl(amazonS3);
	}

	@Provides
	@Singleton
	public static QueueManager provideQueueManager(
		AmazonSNS amazonSNS, ConfigurationHelper configurationHelper) {

		if (_AWS_SAM_LOCAL) {
			return _getDummyQueueManager(configurationHelper);
		}

		return new AmazonSNSQueueManagerImpl(amazonSNS);
	}

	@Provides
	@Singleton
	public static UserService provideUserService(
		AmazonDynamoDB amazonDynamoDB,
		ConfigurationHelper configurationHelper) {

		return new AmazonDynamoDBUserServiceImpl(
			amazonDynamoDB, configurationHelper.getString(_USER_TABLE_NAME));
	}

	private static QueueManager _getDummyQueueManager(
		ConfigurationHelper configurationHelper) {

		Map<String, Supplier<Consumer<QueueMessage>>> topicConsumerSuppliers =
			new HashMap<>();

		String userBotReplyTopic = configurationHelper.getString(
			QueueTopicKeys.USER_BOT_REPLY);

		topicConsumerSuppliers.put(
			userBotReplyTopic,
			() -> {
				ThrowingConsumer<QueueMessage, Exception> throwingConsumer =
					FrontpageComponent.INSTANCE.getSendUserBotReplyConsumer();

				return throwingConsumer.uncheck();
			});

		return new DummyQueueManagerImpl(topicConsumerSuppliers);
	}

	private static final boolean _AWS_SAM_LOCAL = Boolean.parseBoolean(
		System.getenv("AWS_SAM_LOCAL"));

	private static final String _USER_TABLE_NAME = "USER_TABLE_NAME";

}