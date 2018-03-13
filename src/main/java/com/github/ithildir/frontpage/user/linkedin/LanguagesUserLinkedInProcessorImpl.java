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

package com.github.ithildir.frontpage.user.linkedin;

import com.github.ithildir.frontpage.constants.UserConstants;
import com.github.ithildir.frontpage.model.User;
import com.github.ithildir.frontpage.util.CsvData;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Andrea Di Giorgi
 */
public class LanguagesUserLinkedInProcessorImpl
	implements UserLinkedInProcessor {

	@Override
	public void process(User user, CsvData csvData) throws Exception {
		Map<String, String> languages = new HashMap<>();

		do {
			languages.put(csvData.getString("name"), _getProficiency(csvData));
		}
		while (csvData.hasNext());

		user.setLanguages(languages);
	}

	private String _getProficiency(CsvData csvData) {
		String proficiency = csvData.getString("proficiency");

		if (proficiency.equalsIgnoreCase("Elementary proficiency")) {
			proficiency = UserConstants.PROFICIENCY_ELEMENTARY;
		}
		else if (proficiency.equalsIgnoreCase("Limited working proficiency")) {
			proficiency = UserConstants.PROFICIENCY_LIMITED_WORKING;
		}
		else if (proficiency.equalsIgnoreCase(
					"Professional working proficiency")) {

			proficiency = UserConstants.PROFICIENCY_PROFESSIONAL_WORKING;
		}
		else if (proficiency.equalsIgnoreCase(
					"Full professional proficiency")) {

			proficiency = UserConstants.PROFICIENCY_FULL_PROFESSIONAL;
		}
		else if (proficiency.equalsIgnoreCase(
					"Native or bilingual proficiency")) {

			proficiency = UserConstants.PROFICIENCY_NATIVE_OR_BILINGUAL;
		}
		else if (_log.isWarnEnabled()) {
			_log.warn(
				"Unable to convert unknown proficiency \"" + proficiency +
					"\"");
		}

		return proficiency;
	}

	private static final Log _log = LogFactory.getLog(
		LanguagesUserLinkedInProcessorImpl.class);

}