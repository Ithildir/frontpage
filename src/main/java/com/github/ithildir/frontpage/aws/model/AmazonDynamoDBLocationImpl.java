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

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

import com.github.ithildir.frontpage.model.Location;

import java.util.List;

/**
 * @author Andrea Di Giorgi
 */
@DynamoDBDocument
public class AmazonDynamoDBLocationImpl implements Location {

	@Override
	public String getCity() {
		return _city;
	}

	@Override
	public String getCountry() {
		return _country;
	}

	@Override
	public List<String> getRegions() {
		return _regions;
	}

	@Override
	public String getZip() {
		return _zip;
	}

	@Override
	public void setCity(String city) {
		_city = city;
	}

	@Override
	public void setCountry(String country) {
		_country = country;
	}

	@Override
	public void setRegions(List<String> regions) {
		_regions = regions;
	}

	@Override
	public void setZip(String zip) {
		_zip = zip;
	}

	private String _city;
	private String _country;
	private List<String> _regions;
	private String _zip;

}