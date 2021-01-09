package server;

import common.FileDeleteMessage;
import common.FileListMessage;
import common.FileMessage;
import common.FileRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.file.Files;
import java.nio.file.Paths;

public class MainHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FileRequest) {
            FileRequest fr = (FileRequest) msg;
            if (Files.exists(Paths.get("server_storage/" + fr.getFilename()))) {
                FileMessage fm = new FileMessage(Paths.get("server_storage/" + fr.getFilename()));
                ctx.writeAndFlush(fm);
            }
        }

        //загрузкa файла на сервак
        if (msg instanceof FileMessage) {
            Request.fileMessage((FileMessage) msg);
            ctx.writeAndFlush(new FileListMessage(Request.getArrFileInStorage()));
        }

        //удаление файла с сервера
        if (msg instanceof FileDeleteMessage){
            Request.fileDeleteMessage((FileDeleteMessage) msg);
            ctx.writeAndFlush(new FileListMessage(Request.getArrFileInStorage()));
        }


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
