package superbro.ppl9;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Random;

public class Thinker {

    private static final int ORDER_COUNT = 10;
    private static Random rand = new Random(System.currentTimeMillis());

    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket("127.0.0.1", 3232);
        DataInputStream inp = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        PrintStream cout = System.out;
        int thinkerId = rand.nextInt(100);
        for (int i = 0; i < ORDER_COUNT; i++) {
            boolean flag;
            do {
                cout.printf("Thinker %d думает\n", thinkerId);
                Thread.sleep(rand.nextInt(500) + 500);
                out.writeInt(thinkerId);
                out.writeInt(Code.ReqLock.ordinal());
                out.flush();
                flag = Code.ResReject == Code.values()[inp.readInt()];
                if(flag){
                    cout.println("Объекты не достались");
                }
            }
            while(flag);
            String f1 = inp.readUTF();
            String f2 = inp.readUTF();
            cout.printf("Thinker %d взял объекты %s %s\n", thinkerId, f1, f2);
            cout.printf("Thinker %d думает\n", thinkerId);
            Thread.sleep(rand.nextInt(500) + 500);
            out.writeInt(thinkerId);
            out.writeInt(Code.ReqRelease.ordinal());
            out.flush();
            if(Code.ResAccept.ordinal() == inp.readInt()){
                cout.printf("Thinker %d освободил объекты\n", thinkerId);
            }
            else{
                cout.printf("Не удалось освободить объекты\n");
            }
        }
        out.writeInt(thinkerId);
        out.writeInt(Code.Exit.ordinal());
        out.flush();
        socket.close();
    }
}
