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

package com.github.ithildir.frontpage.nlp.impl;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.github.ithildir.frontpage.nlp.NLPEntity;
import com.github.ithildir.frontpage.nlp.NLPMessage;

import java.util.Map;

/**
 * @author Andrea Di Giorgi
 */
public class WitAiNLPMessage implements NLPMessage {

	public WitAiNLPMessage(
		@JsonProperty("entities") Map<String, WitAiNLPEntity[]> entities) {

		_entities = entities;
	}

	@Override
	public NLPEntity[] getEntities(String name) {
		return _entities.get(name);
	}

	@Override
	public String getIntent() {
		NLPEntity[] nlpEntities = getEntities("intent");

		if (nlpEntities != null) {
			NLPEntity nlpEntity = nlpEntities[0];

			return nlpEntity.getValue("value");
		}

		nlpEntities = getEntities("greetings");

		if (nlpEntities != null) {
			NLPEntity nlpEntity = nlpEntities[0];

			if (Boolean.parseBoolean(nlpEntity.getValue("value"))) {
				return "greetings";
			}
		}

		return null;
	}

	private final Map<String, WitAiNLPEntity[]> _entities;

}