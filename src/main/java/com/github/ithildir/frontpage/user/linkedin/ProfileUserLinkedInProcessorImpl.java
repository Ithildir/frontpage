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

import com.github.ithildir.frontpage.geo.Geocoder;
import com.github.ithildir.frontpage.model.Location;
import com.github.ithildir.frontpage.model.User;
import com.github.ithildir.frontpage.user.UserGenderDetector;
import com.github.ithildir.frontpage.util.CsvData;
import com.github.ithildir.frontpage.util.LocaleHelper;

/**
 * @author Andrea Di Giorgi
 */
public class ProfileUserLinkedInProcessorImpl implements UserLinkedInProcessor {

	public ProfileUserLinkedInProcessorImpl(
		Geocoder geocoder, LocaleHelper localeHelper,
		UserGenderDetector userGenderDetector) {

		_geocoder = geocoder;
		_localeHelper = localeHelper;
		_userGenderDetector = userGenderDetector;
	}

	@Override
	public void process(User user, CsvData csvData) throws Exception {
		String oldFirstName = user.getFirstName();
		String firstName = csvData.getString("First Name");

		user.setBirthday(csvData.getLocalDate("Birth Date"));
		user.setFirstName(firstName);
		user.setHeadline(csvData.getString("Headline"));
		user.setInstantMessengers(csvData.getMap("Instant Messengers"));
		user.setLastName(csvData.getString("Last Name"));

		Location location = user.getLocation();

		location.setCountry(csvData.getString("Country"));
		location.setZip(csvData.getString("Zip Code"));

		_geocoder.geocode(location);

		if (!firstName.equalsIgnoreCase(oldFirstName)) {
			String countryCode = _localeHelper.getCountryCode(
				location.getCountry());

			user.setMale(
				_userGenderDetector.detectMale(firstName, countryCode));
		}

		user.setSummary(csvData.getString("Summary"));
	}

	private final Geocoder _geocoder;
	private final LocaleHelper _localeHelper;
	private final UserGenderDetector _userGenderDetector;

}