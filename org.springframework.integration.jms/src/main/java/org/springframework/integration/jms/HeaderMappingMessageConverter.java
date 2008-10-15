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

package org.springframework.integration.jms;

import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.integration.adapter.MessageHeaderMapper;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;
import org.springframework.integration.message.MessagingException;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;

/**
 * A {@link MessageConverter} implementation that delegates to an existing
 * converter as well as an implementation of {@link MessageHeaderMapper}.
 * 
 * @author Mark Fisher
 */
public class HeaderMappingMessageConverter implements MessageConverter {

	private final Log logger = LogFactory.getLog(this.getClass());

	private final MessageConverter converter;

	private final MessageHeaderMapper<javax.jms.Message> headerMapper;


	public HeaderMappingMessageConverter(MessageConverter converter) {
		this(converter, null);
	}

	public HeaderMappingMessageConverter(MessageConverter converter, MessageHeaderMapper<javax.jms.Message> headerMapper) {
		this.converter = (converter != null ? converter : new SimpleMessageConverter());
		this.headerMapper = (headerMapper != null ? headerMapper : new DefaultJmsHeaderMapper());
	}


	public Object fromMessage(javax.jms.Message jmsMessage) throws JMSException, MessageConversionException {
		Object payload = this.converter.fromMessage(jmsMessage);
		Map<String, Object> headerMap = this.headerMapper.mapToMessageHeaders(jmsMessage); 
		Message<?> message = MessageBuilder.withPayload(payload).copyHeaders(headerMap).build();
		if (logger.isDebugEnabled()) {
			logger.debug("converted JMS Message [" + jmsMessage + "] to integration Message [" + message + "]");
		}
		return message;
	}

	public javax.jms.Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
		if (!(object instanceof Message<?>)) {
			throw new MessagingException("expected a '" + Message.class.getName() +
					"', but received '" + object.getClass() + "'");
		}
		Message<?> message = (Message<?>) object;
		javax.jms.Message jmsMessage = this.converter.toMessage(message.getPayload(), session);
		this.headerMapper.mapFromMessageHeaders(message.getHeaders(), jmsMessage);
		if (logger.isDebugEnabled()) {
			logger.debug("converted integration Message [" + message + "] to JMS Message [" + jmsMessage + "]");
		}
		return jmsMessage;
	}

}
