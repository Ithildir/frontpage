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

package com.github.ithildir.frontpage.aws.service;

import cloud.localstack.DockerTestUtils;
import cloud.localstack.docker.LocalstackDockerTestRunner;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.util.TableUtils;

import com.github.ithildir.frontpage.aws.model.AmazonDynamoDBUserImpl;
import com.github.ithildir.frontpage.model.User;
import com.github.ithildir.frontpage.service.UserService;

import java.time.LocalDate;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.unitils.reflectionassert.ReflectionAssert;

/**
 * @author Andrea Di Giorgi
 */
@RunWith(LocalstackDockerTestRunner.class)
public class AmazonDynamoDBUserServiceImplTest {

	@BeforeClass
	public static void setUpClass() {
		_amazonDynamoDB = DockerTestUtils.getClientDynamoDb();

		DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(_amazonDynamoDB);

		_createTableRequest = dynamoDBMapper.generateCreateTableRequest(
			AmazonDynamoDBUserImpl.class);

		_createTableRequest.setProvisionedThroughput(
			new ProvisionedThroughput(1L, 1L));

		_deleteTableRequest = dynamoDBMapper.generateDeleteTableRequest(
			AmazonDynamoDBUserImpl.class);

		_userService = new AmazonDynamoDBUserServiceImpl(
			_amazonDynamoDB, _createTableRequest.getTableName());
	}

	@Before
	public void setUp() throws Exception {
		CreateTableResult createTableResult = _amazonDynamoDB.createTable(
			_createTableRequest);

		TableDescription tableDescription =
			createTableResult.getTableDescription();

		TableUtils.waitUntilActive(
			_amazonDynamoDB, tableDescription.getTableName());
	}

	@After
	public void tearDown() {
		TableUtils.deleteTableIfExists(_amazonDynamoDB, _deleteTableRequest);
	}

	@Test
	public void testGetMissingUser() {
		User user = _userService.getUser("foo");

		Assert.assertNull(user);
	}

	@Test
	public void testGetUser() {
		User user = _userService.createUser("foo");

		user.setBirthday(LocalDate.of(1985, 3, 26));
		user.setEmailAddress("foo@example.com");
		user.setFirstName("Foo");
		user.setFullName("Foo Bar");
		user.setHeadline("Someone Else");
		user.setInstantMessengers(
			_getMap("skype", "fooskype", "gchat", "foogchat"));
		user.setLastName("Bar");
		user.setLinks(
			_getMap(
				"facebook", "https://facebook.com/foo", "github",
				"https://github.com/foo"));
		user.setMale(true);
		user.setSummary("Some Else's Summary");

		_userService.updateUser(user);

		ReflectionAssert.assertReflectionEquals(
			user, _userService.getUser("foo"));
	}

	private static Map<String, String> _getMap(String... values) {
		Assert.assertEquals(0, values.length % 2);

		Map<String, String> map = new HashMap<>();

		for (int i = 0; i < values.length; i += 2) {
			map.put(values[i], values[i + 1]);
		}

		return map;
	}

	private static AmazonDynamoDB _amazonDynamoDB;
	private static CreateTableRequest _createTableRequest;
	private static DeleteTableRequest _deleteTableRequest;
	private static UserService _userService;

}