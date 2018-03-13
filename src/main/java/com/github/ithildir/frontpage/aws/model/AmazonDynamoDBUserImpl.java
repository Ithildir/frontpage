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

package com.github.ithildir.frontpage.aws.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;

import com.github.ithildir.frontpage.aws.util.LocalDateDynamoDBTypeConverter;
import com.github.ithildir.frontpage.model.User;

import java.time.LocalDate;

import java.util.List;
import java.util.Map;

/**
 * @author Andrea Di Giorgi
 */
@DynamoDBTable(tableName = "user")
public class AmazonDynamoDBUserImpl implements User {

	@DynamoDBTypeConverted(converter = LocalDateDynamoDBTypeConverter.class)
	@Override
	public LocalDate getBirthday() {
		return _birthday;
	}

	@Override
	public List<String> getCauses() {
		return _causes;
	}

	@Override
	public String getEmailAddress() {
		return _emailAddress;
	}

	@Override
	public String getFirstName() {
		return _firstName;
	}

	@Override
	public String getFullName() {
		return _fullName;
	}

	@Override
	public String getHeadline() {
		return _headline;
	}

	@Override
	public Map<String, String> getInstantMessengers() {
		return _instantMessengers;
	}

	@Override
	public Map<String, String> getLanguages() {
		return _languages;
	}

	@Override
	public String getLastName() {
		return _lastName;
	}

	@Override
	public Map<String, String> getLinks() {
		return _links;
	}

	@Override
	public AmazonDynamoDBLocationImpl getLocation() {
		if (_location == null) {
			_location = new AmazonDynamoDBLocationImpl();
		}

		return _location;
	}

	@Override
	public List<String> getSkills() {
		return _skills;
	}

	@Override
	public String getSummary() {
		return _summary;
	}

	@DynamoDBHashKey
	@Override
	public String getUserId() {
		return _userId;
	}

	@Override
	public boolean isMale() {
		return _male;
	}

	@Override
	public void setBirthday(LocalDate birthday) {
		_birthday = birthday;
	}

	@Override
	public void setCauses(List<String> causes) {
		_causes = causes;
	}

	@Override
	public void setEmailAddress(String emailAddress) {
		_emailAddress = emailAddress;
	}

	@Override
	public void setFirstName(String firstName) {
		_firstName = firstName;
	}

	@Override
	public void setFullName(String fullName) {
		_fullName = fullName;
	}

	@Override
	public void setHeadline(String headline) {
		_headline = headline;
	}

	@Override
	public void setInstantMessengers(Map<String, String> instantMessengers) {
		_instantMessengers = instantMessengers;
	}

	@Override
	public void setLanguages(Map<String, String> languages) {
		_languages = languages;
	}

	@Override
	public void setLastName(String lastName) {
		_lastName = lastName;
	}

	@Override
	public void setLinks(Map<String, String> links) {
		_links = links;
	}

	public void setLocation(AmazonDynamoDBLocationImpl location) {
		_location = location;
	}

	@Override
	public void setMale(boolean male) {
		_male = male;
	}

	@Override
	public void setSkills(List<String> skills) {
		_skills = skills;
	}

	@Override
	public void setSummary(String summary) {
		_summary = summary;
	}

	public void setUserId(String userId) {
		_userId = userId;
	}

	private LocalDate _birthday;
	private List<String> _causes;
	private String _emailAddress;
	private String _firstName;
	private String _fullName;
	private String _headline;
	private Map<String, String> _instantMessengers;
	private Map<String, String> _languages;
	private String _lastName;
	private Map<String, String> _links;
	private AmazonDynamoDBLocationImpl _location;
	private boolean _male;
	private List<String> _skills;
	private String _summary;
	private String _userId;

}