package com.korpicorp.tac.TemporaryAccessController.mq;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ProducerTest {

    @Mock
    Producer producerMock = mock(Producer.class);

    @Test
    void testSend() {
        producerMock.send("message".getBytes(StandardCharsets.UTF_8), 30000);
        verify(producerMock).send("message".getBytes(StandardCharsets.UTF_8), 30000);
    }
}
