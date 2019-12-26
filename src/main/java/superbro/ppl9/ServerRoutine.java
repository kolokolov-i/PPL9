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
                int thinkerId = inStream.readInt();
                Code command = Code.values()[inStream.readInt()];
                switch(command){
                    case ReqLock:
                        LabServer.tryLock(thinkerId, outStream);
                        break;
                    case ReqRelease:
                        LabServer.tryRelease(thinkerId, outStream);
                        break;
                    case Exit:
                        flag = false;
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
