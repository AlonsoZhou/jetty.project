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

package org.eclipse.jetty.websocket.jsr356.messages;

import java.nio.ByteBuffer;

import javax.websocket.MessageHandler;
import javax.websocket.MessageHandler.Partial;

import org.eclipse.jetty.websocket.common.message.MessageSink;
import org.eclipse.jetty.websocket.common.util.Utf8PartialBuilder;

/**
 * Partial TEXT MessageAppender for MessageHandler.Partial interface
 */
public class TextPartialMessage implements MessageSink
{
    private final MessageHandler.Partial<String> partialHandler;
    private final Utf8PartialBuilder utf8Partial;

    public TextPartialMessage(Partial<String> handler)
    {
        this.partialHandler = handler;
        this.utf8Partial = new Utf8PartialBuilder();
    }

    @Override
    public void accept(ByteBuffer payload, Boolean fin)
    {
        String partialText = utf8Partial.toPartialString(payload);

        // No decoders for Partial messages per JSR-356 (PFD1 spec)
        partialHandler.onMessage(partialText, fin);

        if (fin)
        {
            utf8Partial.reset();
        }
    }
}
