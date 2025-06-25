package org.poweimo.mq.routers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.poweimo.mq.Message;
import org.poweimo.mq.enums.RouteResolution;
import org.poweimo.mq.handlers.ExceptionHandler;
import org.poweimo.mq.handlers.MessageHandler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoutingKeyRouterTest {

    private RoutingKeyRouter router;

    @BeforeEach
    void setUp() {
        router = new RoutingKeyRouter();
    }

    @Test
    void testRouteMessageToRegisteredHandler() {
        String routingKey = "test.key";
        MessageHandler handler = mock(MessageHandler.class);
        Message message = mock(Message.class);
        when(message.getRoutingKey()).thenReturn(routingKey);

        router.registerRoutingKeyHandler(routingKey, handler);

        RouteResolution result = router.route(message);

        verify(handler, times(1)).handleMessage(message);
        assertEquals(RouteResolution.ACKNOWLEDGE, result);
    }

    @Test
    void testMultipleRoutingKeyHandlers() {
        String key1 = "key.1";
        String key2 = "key.2";
        MessageHandler handler1 = mock(MessageHandler.class);
        MessageHandler handler2 = mock(MessageHandler.class);

        Message message1 = mock(Message.class);
        Message message2 = mock(Message.class);

        when(message1.getRoutingKey()).thenReturn(key1);
        when(message2.getRoutingKey()).thenReturn(key2);

        router.registerRoutingKeyHandler(key1, handler1);
        router.registerRoutingKeyHandler(key2, handler2);

        RouteResolution result1 = router.route(message1);
        RouteResolution result2 = router.route(message2);

        verify(handler1, times(1)).handleMessage(message1);
        verify(handler2, times(1)).handleMessage(message2);
        assertEquals(RouteResolution.ACKNOWLEDGE, result1);
        assertEquals(RouteResolution.ACKNOWLEDGE, result2);
    }

    @Test
    void testBuilderCreatesRouterWithHandlers() {
        String routingKey = "builder.key";
        MessageHandler handler = mock(MessageHandler.class);
        MessageRouter unknownHandler = mock(MessageRouter.class);
        ExceptionHandler exceptionHandler = mock(ExceptionHandler.class);

        RoutingKeyRouter builtRouter = RoutingKeyRouter.builder()
                .handler(routingKey, handler)
                .unknownMessageHandler(unknownHandler)
                .exceptionHandler(exceptionHandler)
                .build();

        Message message = mock(Message.class);
        when(message.getRoutingKey()).thenReturn(routingKey);

        RouteResolution result = builtRouter.route(message);

        verify(handler, times(1)).handleMessage(message);
        assertEquals(RouteResolution.ACKNOWLEDGE, result);
        assertSame(unknownHandler, builtRouter.getUnknownMessageHandler());
        assertSame(exceptionHandler, builtRouter.getExceptionHandler());
    }

    @Test
    void testRouteUnknownKeyWithUnknownHandler() {
        String unknownKey = "unknown.key";
        Message message = mock(Message.class);
        when(message.getRoutingKey()).thenReturn(unknownKey);

        MessageRouter unknownHandler = mock(MessageRouter.class);
        when(unknownHandler.route(message)).thenReturn(RouteResolution.REQUEUE);

        router.setUnknownMessageHandler(unknownHandler);

        RouteResolution result = router.route(message);

        verify(unknownHandler, times(1)).route(message);
        assertEquals(RouteResolution.REQUEUE, result);
    }

    @Test
    void testRouteUnknownKeyNoUnknownHandler() {
        String unknownKey = "no.handler.key";
        Message message = mock(Message.class);
        when(message.getRoutingKey()).thenReturn(unknownKey);

        // No unknownMessageHandler set
        RouteResolution result = router.route(message);

        assertEquals(RouteResolution.DLQ, result);
    }

    @Test
    void testHandleExceptionWithAndWithoutExceptionHandler() {
        String routingKey = "exception.key";
        MessageHandler handler = mock(MessageHandler.class);
        Message message = mock(Message.class);
        when(message.getRoutingKey()).thenReturn(routingKey);

        router.registerRoutingKeyHandler(routingKey, handler);

        // Simulate handler throwing exception
        RuntimeException ex = new RuntimeException("Handler error");
        doThrow(ex).when(handler).handleMessage(message);

        // Case 1: ExceptionHandler is set
        ExceptionHandler exceptionHandler = mock(ExceptionHandler.class);
        when(exceptionHandler.handleException(message, ex)).thenReturn(RouteResolution.REQUEUE);
        router.setExceptionHandler(exceptionHandler);

        RouteResolution resultWithHandler = router.route(message);
        verify(exceptionHandler, times(1)).handleException(message, ex);
        assertEquals(RouteResolution.REQUEUE, resultWithHandler);

        // Case 2: ExceptionHandler is not set
        router.setExceptionHandler(null);

        // Need to re-mock to reset invocation count
        MessageHandler handler2 = mock(MessageHandler.class);
        Message message2 = mock(Message.class);
        when(message2.getRoutingKey()).thenReturn(routingKey);
        doThrow(ex).when(handler2).handleMessage(message2);
        router.registerRoutingKeyHandler(routingKey, handler2);

        RouteResolution resultWithoutHandler = router.route(message2);
        assertEquals(RouteResolution.DLQ, resultWithoutHandler);
    }
}