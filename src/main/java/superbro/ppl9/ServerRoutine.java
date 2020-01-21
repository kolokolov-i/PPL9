package superbro.ppl9;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerRoutine implements Runnable {

    private DataInputStream inStream;
    private DataOutputStream outStream;

    ServerRoutine(Socket socket) throws IOException {
        inStream = new DataInputStream(socket.getInputStream());
        outStream = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            boolean flag = true;
            while (flag) {
                int thinkerId = 0;
                int forkId;
                Code command = Code.values()[inStream.readInt()];
                switch(command){
                    case REGISTRATE:
                        inStream.readInt();
                        inStream.readInt();
                        LabServer.registrate(outStream);
                        break;
                    case REQ_GET:
                        thinkerId = inStream.readInt();
                        forkId = inStream.readInt();
                        LabServer.tryLock(thinkerId, forkId, outStream);
                        break;
                    case REQ_RETURN:
                        thinkerId = inStream.readInt();
                        forkId = inStream.readInt();
                        LabServer.tryRelease(thinkerId, forkId, outStream);
                        break;
                    case EXIT:
                        LabServer.exitThinker(thinkerId);
                        flag = false;
                        break;
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
