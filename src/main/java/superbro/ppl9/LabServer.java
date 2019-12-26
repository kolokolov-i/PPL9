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

    private static List<Fork> forks;
    private static Map<Integer, List<Fork>> distMap;

    public static void main(String[] args) {
        int forksCount = 5;
        forks = new ArrayList<>();
        distMap = new HashMap<>();
        for(int i = 0; i < forksCount; i++){
            char t = (char) ('A' + i);
            forks.add(new Fork(String.valueOf(t)));
        }
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

    private static int getFreeCount(){
        return (int) forks.stream().filter(t -> !t.busy).count();
    }

    static synchronized void tryLock(int thinkerId, DataOutputStream outStream) throws IOException {
        if(distMap.containsKey(thinkerId)){
        }
        int freeCount = getFreeCount();
        if(freeCount<2){
            outStream.writeInt(Code.ResReject.ordinal());
            outStream.flush();
            return;
        }
        List<Fork> forks = LabServer.forks.stream().filter(t -> !t.busy).limit(2).collect(Collectors.toList());
        outStream.writeInt(Code.ResAccept.ordinal());
        for (Fork t : forks) {
            t.busy = true;
            outStream.writeUTF(t.name);
        }
        distMap.put(thinkerId, forks);
        outStream.flush();
    }

    static synchronized void tryRelease(int thinkerId, DataOutputStream outStream) throws IOException {
        if(distMap.containsKey(thinkerId)){
            distMap.get(thinkerId).forEach(t -> t.busy = false);
            distMap.remove(thinkerId);
            outStream.writeInt(Code.ResAccept.ordinal());
        }
        else{
            outStream.writeInt(Code.ResReject.ordinal());
        }
        outStream.flush();
    }
}
