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

import com.github.ithildir.frontpage.model.User;
import com.github.ithildir.frontpage.service.UserService;
import com.github.ithildir.frontpage.store.FileStore;
import com.github.ithildir.frontpage.user.linkedin.UserLinkedInProcessor;
import com.github.ithildir.frontpage.util.CsvData;
import com.github.ithildir.frontpage.util.FileHelper;
import com.github.ithildir.frontpage.util.StreamHelper;

import com.google.common.io.ByteStreams;

import java.io.InputStream;

import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pl.touk.throwing.ThrowingBiConsumer;

/**
 * @author Andrea Di Giorgi
 */
@Singleton
public class ProcessUserLinkedInBiConsumer
	implements ThrowingBiConsumer<String, String, Exception> {

	@Inject
	public ProcessUserLinkedInBiConsumer(
		FileHelper fileHelper, FileStore fileStore, StreamHelper streamHelper,
		Map<String, UserLinkedInProcessor> userLinkedInProcessors,
		UserService userService) {

		_fileHelper = fileHelper;
		_fileStore = fileStore;
		_streamHelper = streamHelper;
		_userLinkedInProcessors = userLinkedInProcessors;
		_userService = userService;
	}

	@Override
	public void accept(String bucketName, String fileName) throws Exception {
		String extension = _fileHelper.getExtension(fileName);

		if (!extension.equals("zip")) {
			if (_log.isInfoEnabled()) {
				_log.info(
					"Ignoring file \"" + fileName + "\" from bucket \"" +
						bucketName + "\" since it is not a ZIP file");
			}

			return;
		}

		String userId = fileName.substring(0, fileName.length() - 4);

		User user = _userService.getUser(userId);

		if (user == null) {
			user = _userService.createUser(userId);
		}

		try (InputStream inputStream = _fileStore.getInputStream(
				bucketName, fileName);
			ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {

			ZipEntry zipEntry = null;

			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				if (zipEntry.isDirectory()) {
					continue;
				}

				UserLinkedInProcessor userLinkedInProcessor =
					_userLinkedInProcessors.get(zipEntry.getName());

				if (userLinkedInProcessor == null) {
					continue;
				}

				byte[] bytes = ByteStreams.toByteArray(zipInputStream);

				try (CsvData csvData = new CsvData(bytes)) {
					userLinkedInProcessor.process(user, csvData);
				}
			}

			_streamHelper.drain(inputStream);
		}

		_userService.updateUser(user);
	}

	private static final Log _log = LogFactory.getLog(
		ProcessUserLinkedInBiConsumer.class);

	private final FileHelper _fileHelper;
	private final FileStore _fileStore;
	private final StreamHelper _streamHelper;
	private final Map<String, UserLinkedInProcessor> _userLinkedInProcessors;
	private final UserService _userService;

}