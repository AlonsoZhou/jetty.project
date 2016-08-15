//
//  ========================================================================
//  Copyright (c) 1995-2016 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.websocket.jsr356;

import java.net.URI;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;
import javax.websocket.MessageHandler;

import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.common.test.DummyConnection;
import org.eclipse.jetty.websocket.jsr356.client.EmptyClientEndpointConfig;
import org.eclipse.jetty.websocket.jsr356.handlers.ByteArrayWholeHandler;
import org.eclipse.jetty.websocket.jsr356.handlers.ByteBufferPartialHandler;
import org.eclipse.jetty.websocket.jsr356.handlers.LongMessageHandler;
import org.eclipse.jetty.websocket.jsr356.handlers.StringWholeHandler;
import org.eclipse.jetty.websocket.jsr356.samples.DummyEndpoint;
import org.junit.Before;
import org.junit.Test;

public class JsrSessionTest
{
    private ClientContainer container;
    private JsrSession session;

    @Before
    public void initSession()
    {
        String id = JsrSessionTest.class.getSimpleName();
        URI requestURI = URI.create("ws://localhost/" + id);
        WebSocketPolicy policy = WebSocketPolicy.newClientPolicy();
        DummyConnection connection = new DummyConnection(policy);
        ClientEndpointConfig config = new EmptyClientEndpointConfig();
        ConfiguredEndpoint ei = new ConfiguredEndpoint(new DummyEndpoint(), config);
        session = new JsrSession(container, id, requestURI, ei, connection);
    }

    @Test
    public void testMessageHandlerBinary() throws DeploymentException
    {
        session.addMessageHandler(new ByteBufferPartialHandler());
    }

    @Test
    public void testMessageHandlerBoth() throws DeploymentException
    {
        session.addMessageHandler(new StringWholeHandler());
        session.addMessageHandler(new ByteArrayWholeHandler());
    }

    @Test
    public void testMessageHandlerReplaceTextHandler() throws DeploymentException
    {
        MessageHandler oldText = new StringWholeHandler();
        session.addMessageHandler(oldText); // add a TEXT handler
        session.addMessageHandler(new ByteArrayWholeHandler()); // add BINARY handler
        session.removeMessageHandler(oldText); // remove original TEXT handler
        session.addMessageHandler(new LongMessageHandler()); // add new TEXT handler
    }

    @Test
    public void testMessageHandlerText() throws DeploymentException
    {
        session.addMessageHandler(new StringWholeHandler());
    }
}
