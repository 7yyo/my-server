package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

@Data
public class Server {

  private ServerBootstrap sb;
  private int port;
  private static AtomicInteger connId = new AtomicInteger(0);

  public Server(int port) {
    this.port = port;
  }

  public static void main(String[] args) {
    Server svr = new Server(8000);
    svr.run();
  }

  public void run() {

    NioEventLoopGroup boss = new NioEventLoopGroup();
    NioEventLoopGroup worker = new NioEventLoopGroup();

    ServerBootstrap sb = new ServerBootstrap();
    sb.group(boss, worker)
        .channel(NioServerSocketChannel.class)
        .childHandler(
            new ChannelInitializer<NioSocketChannel>() {
              protected void initChannel(NioSocketChannel ch) {
                ch.pipeline().addLast(new Conn(connId));
              }
            });

    sb.bind(this.port);
    this.sb = sb;
  }
}
