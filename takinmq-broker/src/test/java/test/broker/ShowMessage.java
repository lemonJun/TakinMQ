package test.broker;

import java.io.File;
import java.io.IOException;

import com.takin.mq.message.FileMessage;
import com.takin.mq.message.MessageAndOffset;

public class ShowMessage {
    public static void main(String[] args) {
        try {
            FileMessage filemessage = new FileMessage(new File("D:\\takinmq\\test-1\\00000000000000000000.queue"), true);
            long offset = 0;
            while (true) {
                MessageAndOffset msg = filemessage.read(offset, 1);
                if (msg == null) {
                    System.out.println("finish");
                    break;
                }
                offset = msg.getOffset();
                System.out.println(msg.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
