package superbro.ppl9;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class LabServer {

    private static ExecutorService threadPool = Executors.newFixedThreadPool(5);

    private static final int total = 5;
    private static boolean[] forks;
    private static boolean[] actors;
    private static int thinkersCount = 0;
//    private static ArrayList<Fork> forks;
//    private static Map<Integer, List<Fork>> distMap;

    public static void main(String[] args) {
        forks = new boolean[total];
        actors = new boolean[total];
//        distMap = new HashMap<>();
//        for(int i = 0; i < forksCount; i++){
//            char t = (char) ('A' + i);
//            forks.add(new Fork(String.valueOf(t)));
//        }
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

//    private static int getFreeCount(){
//        return (int) forks.stream().filter(t -> !t.busy).count();
//    }

    private static void send(Code code, DataOutputStream outStream) throws IOException {
        outStream.writeInt(code.ordinal());
//        outStream.writeInt(data);
        outStream.flush();
    }

    static synchronized void tryLock(int thinkerId, int forkId, DataOutputStream out) throws IOException {
        boolean itLeft = thinkerId == forkId;
        if(itLeft){

        }
        else{
            if(!forks[right(forkId)]){
                send(Code.RES_ACCEPT, out);
                return;
            }
            else{
                send(Code.RES_REJECT, out);
                return;
            }
        }

        if (forks[forkId]) {
            out.writeInt(Code.RES_REJECT.ordinal());
            out.flush();
            return;
        }
        if(actors[left(thinkerId)]){
            out.writeInt(Code.RES_REJECT.ordinal());
            out.flush();
            return;
        }
//        if(distMap.containsKey(thinkerId)){
//        }
//        int freeCount = getFreeCount();
//        if(freeCount<2){
//            outStream.writeInt(Code.RES_REJECT.ordinal());
//            outStream.flush();
//            return;
//        }
//        List<Fork> forks = LabServer.forks.stream().filter(t -> !t.busy).limit(2).collect(Collectors.toList());
//        outStream.writeInt(Code.RES_ACCEPT.ordinal());
//        for (Fork t : forks) {
//            t.busy = true;
//            outStream.writeUTF(t.name);
//        }
//        distMap.put(thinkerId, forks);
//        outStream.flush();
    }

    static synchronized void tryRelease(int thinkerId, int forkId, DataOutputStream outStream) throws IOException {
//        if(distMap.containsKey(thinkerId)){
//            distMap.get(thinkerId).forEach(t -> t.busy = false);
//            distMap.remove(thinkerId);
//            outStream.writeInt(Code.RES_ACCEPT.ordinal());
//        }
//        else{
//            outStream.writeInt(Code.RES_REJECT.ordinal());
//        }
//        outStream.flush();
    }

    static synchronized void registrate(DataOutputStream outStream) throws IOException {
        outStream.writeInt(thinkersCount);
        outStream.writeInt(thinkersCount);
        outStream.writeInt(right(thinkersCount));
        thinkersCount++;
        outStream.flush();
    }

    private static int left(int t) {
        return (t == 0) ? (total - 1) : (t - 1);
    }
    private static int right(int t) {
        return (t == (total - 1)) ? 0 : (t + 1);
    }
}
