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

package org.eclipse.jetty.websocket.jsr356.server;

import static org.eclipse.jetty.toolchain.test.ExtraMatchers.ordered;
import static org.junit.Assert.assertThat;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;

import org.eclipse.jetty.toolchain.test.EventQueue;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.jsr356.server.EchoCase.PartialBinary;
import org.eclipse.jetty.websocket.jsr356.server.EchoCase.PartialText;
import org.eclipse.jetty.websocket.jsr356.server.samples.BasicCloseReasonSessionSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.BasicCloseReasonSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.BasicCloseSessionReasonSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.BasicCloseSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.BasicErrorSessionSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.BasicErrorSessionThrowableSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.BasicErrorSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.BasicErrorThrowableSessionSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.BasicErrorThrowableSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.BasicOpenSessionSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.BasicOpenSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.BasicPongMessageSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.BasicTextMessageStringSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.StatelessTextMessageStringSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.beans.DateTextSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.binary.ByteBufferSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.partial.PartialTextSessionSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.partial.PartialTextSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.primitives.BooleanObjectTextSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.primitives.BooleanTextSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.primitives.ByteObjectTextSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.primitives.ByteTextSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.primitives.CharTextSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.primitives.CharacterObjectTextSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.primitives.DoubleObjectTextSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.primitives.DoubleTextSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.primitives.FloatObjectTextSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.primitives.FloatTextSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.primitives.IntParamTextSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.primitives.IntTextSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.primitives.IntegerObjectTextSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.primitives.LongObjectTextSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.primitives.LongTextSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.primitives.ShortObjectTextSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.primitives.ShortTextSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.streaming.InputStreamSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.streaming.ReaderParamSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.streaming.ReaderSocket;
import org.eclipse.jetty.websocket.jsr356.server.samples.streaming.StringReturnReaderParamSocket;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class EchoTest
{
    private static final List<EchoCase[]> TESTCASES = new ArrayList<>();

    private static WSServer server;
    private static URI serverUri;
    private static WebSocketContainer client;

    static
    {
        EchoCase.add(TESTCASES,BooleanTextSocket.class).addMessage(true).expect("true");
        EchoCase.add(TESTCASES,BooleanTextSocket.class).addMessage(false).expect("false");
        EchoCase.add(TESTCASES,BooleanTextSocket.class).addMessage(Boolean.TRUE).expect("true");
        EchoCase.add(TESTCASES,BooleanTextSocket.class).addMessage(Boolean.FALSE).expect("false");
        EchoCase.add(TESTCASES,BooleanTextSocket.class).addMessage("true").expect("true");
        EchoCase.add(TESTCASES,BooleanTextSocket.class).addMessage("TRUe").expect("true");
        EchoCase.add(TESTCASES,BooleanTextSocket.class).addMessage("Apple").expect("false");
        EchoCase.add(TESTCASES,BooleanTextSocket.class).addMessage("false").expect("false");

        EchoCase.add(TESTCASES,BooleanObjectTextSocket.class).addMessage(true).expect("true");
        EchoCase.add(TESTCASES,BooleanObjectTextSocket.class).addMessage(false).expect("false");
        EchoCase.add(TESTCASES,BooleanObjectTextSocket.class).addMessage(Boolean.TRUE).expect("true");
        EchoCase.add(TESTCASES,BooleanObjectTextSocket.class).addMessage(Boolean.FALSE).expect("false");
        EchoCase.add(TESTCASES,BooleanObjectTextSocket.class).addMessage("true").expect("true");
        EchoCase.add(TESTCASES,BooleanObjectTextSocket.class).addMessage("false").expect("false");
        EchoCase.add(TESTCASES,BooleanObjectTextSocket.class).addMessage("FaLsE").expect("false");

        EchoCase.add(TESTCASES,ByteTextSocket.class).addMessage((byte)88).expect("0x58");
        EchoCase.add(TESTCASES,ByteTextSocket.class).addMessage((byte)101).expect("0x65");
        EchoCase.add(TESTCASES,ByteTextSocket.class).addMessage((byte)202).expect("0xCA");
        EchoCase.add(TESTCASES,ByteTextSocket.class).addMessage(Byte.valueOf((byte)33)).expect("0x21");
        EchoCase.add(TESTCASES,ByteTextSocket.class).addMessage(Byte.valueOf((byte)131)).expect("0x83");
        EchoCase.add(TESTCASES,ByteTextSocket.class).addMessage(Byte.valueOf((byte)232)).expect("0xE8");

        EchoCase.add(TESTCASES,ByteObjectTextSocket.class).addMessage((byte)88).expect("0x58");
        EchoCase.add(TESTCASES,ByteObjectTextSocket.class).addMessage((byte)101).expect("0x65");
        EchoCase.add(TESTCASES,ByteObjectTextSocket.class).addMessage((byte)202).expect("0xCA");
        EchoCase.add(TESTCASES,ByteObjectTextSocket.class).addMessage(Byte.valueOf((byte)33)).expect("0x21");
        EchoCase.add(TESTCASES,ByteObjectTextSocket.class).addMessage(Byte.valueOf((byte)131)).expect("0x83");
        EchoCase.add(TESTCASES,ByteObjectTextSocket.class).addMessage(Byte.valueOf((byte)232)).expect("0xE8");

        EchoCase.add(TESTCASES,CharTextSocket.class).addMessage((char)40).expect("(");
        EchoCase.add(TESTCASES,CharTextSocket.class).addMessage((char)106).expect("j");
        EchoCase.add(TESTCASES,CharTextSocket.class).addMessage((char)126).expect("~");
        EchoCase.add(TESTCASES,CharTextSocket.class).addMessage(Character.valueOf((char)41)).expect(")");
        EchoCase.add(TESTCASES,CharTextSocket.class).addMessage(Character.valueOf((char)74)).expect("J");
        EchoCase.add(TESTCASES,CharTextSocket.class).addMessage(Character.valueOf((char)64)).expect("@");

        EchoCase.add(TESTCASES,CharacterObjectTextSocket.class).addMessage((char)40).expect("(");
        EchoCase.add(TESTCASES,CharacterObjectTextSocket.class).addMessage((char)106).expect("j");
        EchoCase.add(TESTCASES,CharacterObjectTextSocket.class).addMessage((char)126).expect("~");
        EchoCase.add(TESTCASES,CharacterObjectTextSocket.class).addMessage("E").expect("E");
        EchoCase.add(TESTCASES,CharacterObjectTextSocket.class).addMessage(Character.valueOf((char)41)).expect(")");
        EchoCase.add(TESTCASES,CharacterObjectTextSocket.class).addMessage(Character.valueOf((char)74)).expect("J");
        EchoCase.add(TESTCASES,CharacterObjectTextSocket.class).addMessage(Character.valueOf((char)64)).expect("@");

        EchoCase.add(TESTCASES,DoubleTextSocket.class).addMessage((double)3.1459).expect("3.1459");
        EchoCase.add(TESTCASES,DoubleTextSocket.class).addMessage((double)123.456).expect("123.4560");
        EchoCase.add(TESTCASES,DoubleTextSocket.class).addMessage(Double.valueOf(55)).expect("55.0000");
        EchoCase.add(TESTCASES,DoubleTextSocket.class).addMessage(Double.valueOf(1.0E8)).expect("100000000.0000");
        EchoCase.add(TESTCASES,DoubleTextSocket.class).addMessage("42").expect("42.0000");
        EchoCase.add(TESTCASES,DoubleTextSocket.class).addMessage(".123").expect("0.1230");

        EchoCase.add(TESTCASES,DoubleObjectTextSocket.class).addMessage((double)3.1459).expect("3.1459");
        EchoCase.add(TESTCASES,DoubleObjectTextSocket.class).addMessage((double)123.456).expect("123.4560");
        EchoCase.add(TESTCASES,DoubleObjectTextSocket.class).addMessage(Double.valueOf(55)).expect("55.0000");
        EchoCase.add(TESTCASES,DoubleObjectTextSocket.class).addMessage(Double.valueOf(1.0E8)).expect("100000000.0000");
        EchoCase.add(TESTCASES,DoubleObjectTextSocket.class).addMessage("42").expect("42.0000");
        EchoCase.add(TESTCASES,DoubleObjectTextSocket.class).addMessage(".123").expect("0.1230");

        EchoCase.add(TESTCASES,FloatTextSocket.class).addMessage((float)3.1459).expect("3.1459");
        EchoCase.add(TESTCASES,FloatTextSocket.class).addMessage((float)123.456).expect("123.4560");
        EchoCase.add(TESTCASES,FloatTextSocket.class).addMessage(Float.valueOf(55)).expect("55.0000");
        EchoCase.add(TESTCASES,FloatTextSocket.class).addMessage(Float.valueOf(1.0E8f)).expect("100000000.0000");
        EchoCase.add(TESTCASES,FloatTextSocket.class).addMessage("42").expect("42.0000");
        EchoCase.add(TESTCASES,FloatTextSocket.class).addMessage(".123").expect("0.1230");
        EchoCase.add(TESTCASES,FloatTextSocket.class).addMessage("50505E-6").expect("0.0505");

        EchoCase.add(TESTCASES,FloatObjectTextSocket.class).addMessage((float)3.1459).expect("3.1459");
        EchoCase.add(TESTCASES,FloatObjectTextSocket.class).addMessage((float)123.456).expect("123.4560");
        EchoCase.add(TESTCASES,FloatObjectTextSocket.class).addMessage(Float.valueOf(55)).expect("55.0000");
        EchoCase.add(TESTCASES,FloatObjectTextSocket.class).addMessage(Float.valueOf(1.0E8f)).expect("100000000.0000");
        EchoCase.add(TESTCASES,FloatObjectTextSocket.class).addMessage("42").expect("42.0000");
        EchoCase.add(TESTCASES,FloatObjectTextSocket.class).addMessage(".123").expect("0.1230");
        EchoCase.add(TESTCASES,FloatObjectTextSocket.class).addMessage("50505E-6").expect("0.0505");

        EchoCase.add(TESTCASES,IntTextSocket.class).addMessage((int)8).expect("8");
        EchoCase.add(TESTCASES,IntTextSocket.class).addMessage((int)22).expect("22");
        EchoCase.add(TESTCASES,IntTextSocket.class).addMessage("12345678").expect("12345678");

        EchoCase.add(TESTCASES,IntegerObjectTextSocket.class).addMessage((int)8).expect("8");
        EchoCase.add(TESTCASES,IntegerObjectTextSocket.class).addMessage((int)22).expect("22");
        EchoCase.add(TESTCASES,IntegerObjectTextSocket.class).addMessage("12345678").expect("12345678");

        EchoCase.add(TESTCASES,LongTextSocket.class).addMessage((int)789).expect("789");
        EchoCase.add(TESTCASES,LongTextSocket.class).addMessage((long)123456L).expect("123456");
        EchoCase.add(TESTCASES,LongTextSocket.class).addMessage(-456).expect("-456");

        EchoCase.add(TESTCASES,LongObjectTextSocket.class).addMessage((int)789).expect("789");
        EchoCase.add(TESTCASES,LongObjectTextSocket.class).addMessage((long)123456L).expect("123456");
        EchoCase.add(TESTCASES,LongObjectTextSocket.class).addMessage(-234).expect("-234");

        EchoCase.add(TESTCASES,ShortTextSocket.class).addMessage((int)4).expect("4");
        EchoCase.add(TESTCASES,ShortTextSocket.class).addMessage((long)987).expect("987");
        EchoCase.add(TESTCASES,ShortTextSocket.class).addMessage("32001").expect("32001");

        EchoCase.add(TESTCASES,ShortObjectTextSocket.class).addMessage((int)4).expect("4");
        EchoCase.add(TESTCASES,ShortObjectTextSocket.class).addMessage((int)987).expect("987");
        EchoCase.add(TESTCASES,ShortObjectTextSocket.class).addMessage(-32001L).expect("-32001");

        // PathParam based
        EchoCase.add(TESTCASES,IntParamTextSocket.class).requestPath("/echo/primitives/integer/params/5678").addMessage(1234).expect("1234|5678");
        
        // Text based
        EchoCase.add(TESTCASES,BasicTextMessageStringSocket.class).addMessage("Hello").expect("Hello");
        EchoCase.add(TESTCASES,StatelessTextMessageStringSocket.class).addMessage("Hello").expect("Hello");
        
        // ByteBuffer based
        EchoCase.add(TESTCASES,ByteBufferSocket.class).addMessage(BufferUtil.toBuffer("Hello World")).expect("Hello World");

        // InputStream based
        EchoCase.add(TESTCASES,InputStreamSocket.class).addMessage(BufferUtil.toBuffer("Hello World")).expect("Hello World");

        // Reader based
        EchoCase.add(TESTCASES,ReaderSocket.class).addMessage("Hello World").expect("Hello World");
        EchoCase.add(TESTCASES,ReaderParamSocket.class).requestPath("/echo/streaming/readerparam/OhNo").addMessage("Hello World").expect("Hello World|OhNo");
        EchoCase.add(TESTCASES,StringReturnReaderParamSocket.class).requestPath("/echo/streaming/readerparam2/OhMy").addMessage("Hello World")
                .expect("Hello World|OhMy");

        // Partial message based
        EchoCase.add(TESTCASES,PartialTextSocket.class)
          .addSplitMessage("Saved"," by ","zero")
          .expect("('Saved',false)(' by ',false)('zero',true)");
        EchoCase.add(TESTCASES,PartialTextSessionSocket.class)
          .addSplitMessage("Built"," for"," the"," future")
          .expect("('Built',false)(' for',false)(' the',false)(' future',true)");
        
        // Beans
        EchoCase.add(TESTCASES, DateTextSocket.class).addMessage("Ooops").expect("");
        
        // Pong
        EchoCase.add(TESTCASES, BasicPongMessageSocket.class).addMessage("send-ping").expect("Pong[]");
        
        // Open Events
        EchoCase.add(TESTCASES, BasicOpenSocket.class).expect("Open[]");
        EchoCase.add(TESTCASES, BasicOpenSessionSocket.class).expect("Open[Session]");
        
        // Close Events
        EchoCase.add(TESTCASES, BasicCloseSocket.class).expect("Close[]");
        EchoCase.add(TESTCASES, BasicCloseReasonSocket.class).expect("Close[Reason]");
        EchoCase.add(TESTCASES, BasicCloseReasonSessionSocket.class).expect("Close[Reason,Session]");
        EchoCase.add(TESTCASES, BasicCloseSessionReasonSocket.class).expect("Close[Session,Reason]");
        
        // Error Events
        EchoCase.add(TESTCASES, BasicErrorSocket.class).expect("Error[]");
        EchoCase.add(TESTCASES, BasicErrorSessionSocket.class).expect("Error[Session]");
        EchoCase.add(TESTCASES, BasicErrorSessionThrowableSocket.class).expect("Error[Session,Throwable]");
        EchoCase.add(TESTCASES, BasicErrorThrowableSocket.class).expect("Error[Throwable]");
        EchoCase.add(TESTCASES, BasicErrorThrowableSessionSocket.class).expect("Error[Throwable,Session]");
    }

    @BeforeClass
    public static void startServer() throws Exception
    {
        Path testdir = MavenTestingUtils.getTargetTestingPath(EchoTest.class.getName());
        server = new WSServer(testdir,"app");
        server.copyWebInf("empty-web.xml");

        for (EchoCase cases[] : TESTCASES)
        {
            for (EchoCase ecase : cases)
            {
                server.copyClass(ecase.serverPojo);
            }
        }

        server.start();
        serverUri = server.getServerBaseURI();

        WebAppContext webapp = server.createWebAppContext();
        server.deployWebapp(webapp);
    }

    @BeforeClass
    public static void startClient() throws Exception
    {
        client = ContainerProvider.getWebSocketContainer();
    }

    @AfterClass
    public static void stopServer()
    {
        server.stop();
    }

    @Parameters
    public static Collection<EchoCase[]> data() throws Exception
    {
        return TESTCASES;
    }

    private EchoCase testcase;

    public EchoTest(EchoCase testcase)
    {
        this.testcase = testcase;
        System.err.println(testcase);
    }

    @Test(timeout=2000)
    public void testEcho() throws Exception
    {
        int messageCount = testcase.getMessageCount();
        EchoClientSocket socket = new EchoClientSocket(messageCount);
        URI toUri = serverUri.resolve(testcase.path.substring(1));

        try
        {
            // Connect
            client.connectToServer(socket,toUri);
            socket.waitForConnected(2,TimeUnit.SECONDS);

            // Send Messages
            for (Object msg : testcase.messages)
            {
                if (msg instanceof PartialText)
                {
                    PartialText pt = (PartialText)msg;
                    socket.sendPartialText(pt.part,pt.fin);
                }
                else if (msg instanceof PartialBinary)
                {
                    PartialBinary pb = (PartialBinary)msg;
                    socket.sendPartialBinary(pb.part,pb.fin);
                }
                else if (msg instanceof ByteBuffer)
                {
                    socket.sendBinary((ByteBuffer) msg);
                }
                else if (msg instanceof String)
                {
                    socket.sendText((String) msg);
                }
                else
                {
                    socket.sendObject(msg);
                }
            }

            // Collect Responses
            socket.awaitAllEvents(1,TimeUnit.SECONDS);
            EventQueue<String> received = socket.eventQueue;
    
            // Validate Responses
            assertThat("Received Events", received, ordered(testcase.expectedStrings));
        }
        finally
        {
            // Close
            socket.close();
        }
    }
}
