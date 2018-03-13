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

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.ConversionSchemas;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;

import com.github.ithildir.frontpage.aws.model.AmazonDynamoDBUserImpl;
import com.github.ithildir.frontpage.model.User;
import com.github.ithildir.frontpage.service.UserService;

/**
 * @author Andrea Di Giorgi
 */
public class AmazonDynamoDBUserServiceImpl implements UserService {

	public AmazonDynamoDBUserServiceImpl(
		AmazonDynamoDB amazonDynamoDB, String tableName) {

		DynamoDBMapperConfig.Builder builder = DynamoDBMapperConfig.builder();

		builder.setConversionSchema(ConversionSchemas.V2);
		builder.setTableNameOverride(
			TableNameOverride.withTableNameReplacement(tableName));

		_dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB, builder.build());
	}

	@Override
	public User createUser(String userId) {
		AmazonDynamoDBUserImpl amazonDynamoDBUserImpl =
			new AmazonDynamoDBUserImpl();

		amazonDynamoDBUserImpl.setUserId(userId);

		return amazonDynamoDBUserImpl;
	}

	@Override
	public User getUser(String userId) {
		return _dynamoDBMapper.load(AmazonDynamoDBUserImpl.class, userId);
	}

	@Override
	public void updateUser(User user) {
		_dynamoDBMapper.save(user);
	}

	private final DynamoDBMapper _dynamoDBMapper;

}