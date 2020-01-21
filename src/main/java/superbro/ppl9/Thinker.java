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

    private static DataOutputStream out;
    private static int thinkerId, forkLeft, forkRight;

    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket("127.0.0.1", 3232);
        DataInputStream inp = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        PrintStream cout = System.out;
        send(Code.REGISTRATE, 0);
        thinkerId = inp.readInt();
        forkLeft = inp.readInt();
        forkRight = inp.readInt();
        for (int i = 0; i < ORDER_COUNT; i++) {
            boolean flag;
            do {
                cout.printf("Thinker %d думает\n", thinkerId);
                Thread.sleep(rand.nextInt(500) + 500);
                send(Code.REQ_GET, forkLeft);
                flag = Code.RES_REJECT == Code.values()[inp.readInt()];
                if(flag){
                    cout.println("Левая вилка не досталась");
                }
            }
            while(flag);
            cout.println("Взял левую вилку");
            do {
                cout.printf("Thinker %d думает\n", thinkerId);
                Thread.sleep(rand.nextInt(500) + 500);
                send(Code.REQ_GET, forkRight);
                flag = Code.RES_REJECT == Code.values()[inp.readInt()];
                if(flag){
                    cout.println("Правая вилка не досталась");
                }
            }
            while(flag);
            cout.println("Взял правую вилку");
            cout.printf("Thinker %d кушает\n", thinkerId);
            Thread.sleep(rand.nextInt(500) + 500);
            send(Code.REQ_RETURN, forkLeft);
            cout.printf("Thinker %d освободил левую вилку\n", thinkerId);
            Thread.sleep(rand.nextInt(500) + 200);
            send(Code.REQ_RETURN, forkRight);
            cout.printf("Thinker %d освободил правую вилку\n", thinkerId);
        }
        send(Code.EXIT, 0);
        socket.close();
    }

    private static void send(Code code, int data) throws IOException {
        out.writeInt(code.ordinal());
        out.writeInt(thinkerId);
        out.writeInt(data);
        out.flush();
    }
}
