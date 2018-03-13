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

package com.github.ithildir.frontpage.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Andrea Di Giorgi
 */
@Singleton
public class JsonHelper {

	@Inject
	public JsonHelper() {
		_objectMapper = new ObjectMapper();

		_objectMapper.disable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);
		_objectMapper.disable(
			DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		_objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		_objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
		_objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);

		_objectMapper.registerModule(new JavaTimeModule());

		_objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

		TypeFactory typeFactory = _objectMapper.getTypeFactory();

		_mapType = typeFactory.constructMapType(
			Map.class, String.class, Object.class);
	}

	public Map<String, Object> deserialize(String json) throws Exception {
		return _objectMapper.readValue(json, _mapType);
	}

	public <T> T deserialize(String json, Class<T> clazz) throws Exception {
		return _objectMapper.readValue(json, clazz);
	}

	public String serialize(Object object) throws Exception {
		return _objectMapper.writeValueAsString(object);
	}

	private MapType _mapType;
	private final ObjectMapper _objectMapper;

}