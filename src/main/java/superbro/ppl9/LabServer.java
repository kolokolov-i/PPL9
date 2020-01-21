package superbro.ppl9;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class LabServer {

    private static ExecutorService threadPool = Executors.newFixedThreadPool(5);
    private static final Semaphore semaphore = new Semaphore(1);

    private static final int total = 5;
    private static boolean[] forks;
    private static boolean[] actors;
    private static boolean[] seats;

    public static void main(String[] args) {
        forks = new boolean[total];
        actors = new boolean[total];
        seats = new boolean[total];
        try (ServerSocket serverSocket = new ServerSocket(3232)) {
            while (true) {
                Socket socket = serverSocket.accept();
                try {
                    ServerRoutine routine = new ServerRoutine(socket);
                    threadPool.execute(routine);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static synchronized void tryLock(int thinkerId, int forkId, DataOutputStream out) throws IOException, InterruptedException {
        semaphore.acquire();
        boolean itLeft = thinkerId == forkId;
        if(itLeft){
            if(actors[left(forkId)]){
                send(Code.RES_REJECT, out);
            }
            else{
                send(Code.RES_ACCEPT, out);
                actors[thinkerId] = true;
                forks[forkId] = true;
            }
        }
        else{
            if(!forks[right(forkId)]){
                send(Code.RES_ACCEPT, out);
                actors[thinkerId] = true;
                forks[forkId] = true;
            }
            else{
                send(Code.RES_REJECT, out);
            }
        }
        semaphore.release();
    }

    static synchronized void tryRelease(int thinkerId, int forkId, DataOutputStream outStream) throws InterruptedException {
        semaphore.acquire();
        forks[forkId] = false;
        if(thinkerId == left(forkId)){
            actors[thinkerId] = false;
        }
        semaphore.release();
    }

    static synchronized void registrate(DataOutputStream outStream) throws IOException, InterruptedException {
        semaphore.acquire();
        int thinkerId = 0;
        for (int i = 0; i < total; i++) {
            if(!seats[i]){
                seats[i] = true;
                thinkerId = i;
                break;
            }
        }
        outStream.writeInt(thinkerId);
        outStream.writeInt(thinkerId);
        int rf = right(thinkerId);
        outStream.writeInt(rf);
        outStream.flush();
        semaphore.release();
    }

    static synchronized void exitThinker(int thinkerId) {
        actors[thinkerId] = false;
        seats[thinkerId] = false;
    }

    private static int left(int t) {
        return (t == 0) ? (total - 1) : (t - 1);
    }
    private static int right(int t) {
        return (t == (total - 1)) ? 0 : (t + 1);
    }

    private static void send(Code code, DataOutputStream outStream) throws IOException {
        outStream.writeInt(code.ordinal());
        outStream.flush();
    }
}
