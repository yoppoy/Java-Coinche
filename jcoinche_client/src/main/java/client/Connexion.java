package client;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class Connexion {
    public void run(int port) throws IOException, InterruptedException
    {
        IoConnector connector = new NioSocketConnector();
        connector.getSessionConfig().setReadBufferSize(2048);

        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
        connector.setHandler(new client.Handler("0001 I'm new"));
        ConnectFuture future = connector.connect(new InetSocketAddress("127.0.0.1", port));
        future.awaitUninterruptibly();
        if (!future.isConnected())
        {
            return;
        }
        IoSession session = future.getSession();
        session.getConfig().setUseReadOperation(true);
        Thread t = new Thread(new BufferReader(session));
        t.start();
        session.getCloseFuture().awaitUninterruptibly();
        System.out.println(session.read().getMessage());
        connector.dispose();
    }
}
