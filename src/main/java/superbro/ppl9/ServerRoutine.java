package superbro.ppl9;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerRoutine implements Runnable {

    private Socket socket;
    DataInputStream inStream;
    DataOutputStream outStream;

    public ServerRoutine(Socket socket) throws IOException {
        this.socket = socket;
        inStream = new DataInputStream(socket.getInputStream());
        outStream = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            boolean flag = true;
            while (flag) {
                int thinkerId = 0;
                int forkId = 0;
                Code command = Code.values()[inStream.readInt()];
                switch(command){
                    case REGISTRATE:
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
                        flag = false;
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
