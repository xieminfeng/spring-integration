/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.integration.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.integration.core.Message;
import org.springframework.integration.util.BoundedHashMap;
import org.springframework.util.Assert;

/**
 * Map-based implementation of {@link MessageStore} that enforces capacity.
 * 
 * @author Mark Fisher
 */
public class SimpleMessageStore implements MessageStore {

	private final Map<Object, Message<?>> map;


	public SimpleMessageStore(int capacity) {
		this.map = new BoundedHashMap<Object, Message<?>>(capacity);
	}


	public Message<?> put(Object key, Message<?> message) {
		Assert.notNull(key, "'key' must not be null");
		Assert.notNull(message, "'message' must not be null");
		return this.map.put(key, message);
	}

	public Message<?> get(Object key) {
		return (key != null) ? this.map.get(key) : null;
	}

	public List<Message<?>> list() {
		return new ArrayList<Message<?>>(this.map.values());
	}

	public Message<?> remove(Object key) {
		return (key != null) ? this.map.remove(key) : null;
	}

	public int size() {
		return this.map.size();
	}

}
