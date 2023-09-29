import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Executor implements Watcher, Runnable, DataMonitor.DataMonitorListener {


    private final String[] exec;
    private final ZooKeeper zk;
    private Process child;

    private static final String znode = "/z";
    private final DataMonitor dm;

    public static void main(String[] args) {
        String hostPort = args[0];
        String exec[] = new String[args.length - 1];
        System.arraycopy(args, 1, exec, 0, exec.length);
        try {
            new Executor(hostPort, znode, exec).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Executor(String hostPort, String znode, String exec[]) throws IOException {
        this.exec = exec;
        zk = new ZooKeeper(hostPort, 3000, this);
        dm = new DataMonitor(zk, znode, null, this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    try {
                        if (br.readLine().equals("t")) {
                            dm.printZnodeTree(znode);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void run() {
        try {
            synchronized(this) {
                while (!dm.dead) {
                    wait();
                }
            }
        } catch (InterruptedException e) {
        }
    }

    @Override
    public void process(WatchedEvent event) {
        dm.process(event);
    }

    @Override
    public void exists(byte[] data) {
        if (child == null) {
            try {
                System.out.println("Starting process");
                child = Runtime.getRuntime().exec(exec);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void deleted() {
        if (child != null) {
            System.out.println("Killing process");
            child.destroy();
            try {
                child.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            child = null;
        }
    }

    @Override
    public void closing(int rc) {
        synchronized (this) {
            notifyAll();
        }
    }
}
