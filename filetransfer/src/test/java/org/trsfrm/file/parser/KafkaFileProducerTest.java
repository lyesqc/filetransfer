package org.trsfrm.file.parser;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.trsfrm.file.FileParser;
import org.trsfrm.kafka.KafkaFileProducer;
import org.trsfrm.model.FileSettingsToSend;

public class KafkaFileProducerTest {

	@Mock
	KafkaProducer producer;
	@Mock
	FileParser parser;
	@Mock
	Future<RecordMetadata> future;

	@Test
	public void standardSendWithSendError() throws InterruptedException, ExecutionException {
		KafkaFileProducer kafkaThred = new KafkaFileProducer(new FileSettingsToSend(null, null, parser), producer,
				"topic");
		List<String> listData = new ArrayList<String>(Arrays.asList("a"));
		/**
		 * mock not sending data
		 */
		when(parser.loadFile(anyObject())).thenReturn(listData.iterator());
		doReturn(true).when(parser).movFile(anyString(), anyObject(), Mockito.anyBoolean());
		when(parser.movFile(anyString(), anyObject())).thenReturn(true);
		int i = kafkaThred.call();
		ArgumentCaptor<Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
		verify(producer).send(anyObject(), callbackCaptor.capture());
		Callback callback = callbackCaptor.getValue();
		callback.onCompletion(null, null);
		i = kafkaThred.call();
		verify(parser, times(2)).movFile(anyObject(), anyObject(), Mockito.anyBoolean());
		assertEquals(-1, i);
	}

	@Test
	public void sendOK() throws InterruptedException, ExecutionException {
		KafkaFileProducer kafkaThred = new KafkaFileProducer(new FileSettingsToSend(null, null, parser), producer,
				"topic");
		List<String> listData = new ArrayList<String>(Arrays.asList("a", "b"));
		/**
		 * mock not sending data
		 */
		when(parser.loadFile(anyObject())).thenReturn(listData.iterator());
		doReturn(true).when(parser).movFile(anyString(), anyObject(), Mockito.anyBoolean());
		when(parser.movFile(anyString(), anyObject())).thenReturn(true);
		int i = kafkaThred.call();
		ArgumentCaptor<Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
		verify(producer, times(2)).send(anyObject(), callbackCaptor.capture());
		Callback callback = callbackCaptor.getValue();
		i = kafkaThred.call();
		verify(parser, times(2)).movFile(anyObject(), anyObject(), Mockito.anyBoolean());
		assertEquals(0, i);
	}

	@Test
	public void sendWithException() throws InterruptedException, ExecutionException {
		KafkaFileProducer kafkaThred = new KafkaFileProducer(new FileSettingsToSend(null, null, parser), producer,
				"topic");
		List<String> listData = new ArrayList<String>(Arrays.asList("a", "b"));
		/**
		 * mock not sending data
		 */
		when(parser.loadFile(anyObject())).thenReturn(listData.iterator());
		doReturn(true).when(parser).movFile(anyString(), anyObject(), Mockito.anyBoolean());
		when(parser.movFile(anyString(), anyObject())).thenReturn(true);

		doAnswer(new Answer() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Object[] args = invocation.getArguments();
				kafkaThred.onCompleteKafkaSend(null, null);
				throw new Exception();

			}
		}).when(producer).send(anyObject(), anyObject());

		int i = kafkaThred.call();
		i = kafkaThred.call();
		verify(parser, times(2)).movFile(anyObject(), anyObject(), Mockito.anyBoolean());
		assertEquals(-1, i);
	}

	@Before
	public void initMock() {
		MockitoAnnotations.initMocks(this);
	}
}
