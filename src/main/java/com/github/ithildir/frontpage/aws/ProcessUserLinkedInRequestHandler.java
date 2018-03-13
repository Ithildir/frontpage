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
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.event.S3EventNotification.S3BucketEntity;
import com.amazonaws.services.s3.event.S3EventNotification.S3Entity;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.event.S3EventNotification.S3ObjectEntity;

import com.github.ithildir.frontpage.di.DaggerFrontpageComponent;

import java.util.function.BiConsumer;

import pl.touk.throwing.ThrowingBiConsumer;

/**
 * @author Andrea Di Giorgi
 */
public class ProcessUserLinkedInRequestHandler
	implements RequestHandler<S3Event, Void> {

	@Override
	public Void handleRequest(S3Event s3Event, Context context) {
		for (S3EventNotificationRecord s3EventNotificationRecord :
				s3Event.getRecords()) {

			S3Entity s3Entity = s3EventNotificationRecord.getS3();

			S3BucketEntity s3BucketEntity = s3Entity.getBucket();

			String bucketName = s3BucketEntity.getName();

			S3ObjectEntity s3ObjectEntity = s3Entity.getObject();

			String fileName = s3ObjectEntity.getKey();

			_biConsumer.accept(bucketName, fileName);
		}

		return null;
	}

	private static BiConsumer<String, String> _biConsumer;

	static {
		ThrowingBiConsumer<String, String, Exception> throwingBiConsumer =
			DaggerFrontpageComponent.INSTANCE.
				getProcessUserLinkedInBiConsumer();

		_biConsumer = throwingBiConsumer.uncheck();
	}

}