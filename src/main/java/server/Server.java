package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Data;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

@Data
public class Server {

    private static final Logger logger = LoggerFactory.getLogger("logger");
    private int port;
    private static AtomicInteger connId = new AtomicInteger(0);

    public Server(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        logger.info("welcome to my-server.");
        Server svr = new Server(4000);
        svr.run();
    }

    @SneakyThrows
    public void run() {

        ServerBootstrap sb = new ServerBootstrap();
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            sb.group(boss, worker);
            sb.channel(NioServerSocketChannel.class);
            sb.childHandler(
                    new ChannelInitializer<NioSocketChannel>() {
                        protected void initChannel(NioSocketChannel ch) {
                            ch.pipeline().addLast(new Conn(connId));
                        }
                    });
            ChannelFuture cf = sb.bind(this.port).addListener(
                    future -> {
                        if (!future.isSuccess()) {
                            logger.info("bind port {} failed, exit.", this.port);
                        }
                    });
            cf.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
