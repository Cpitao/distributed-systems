import org.apache.zookeeper.*;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.data.Stat;

public class DataMonitor implements Watcher, AsyncCallback.VoidCallback, AsyncCallback.StatCallback {

    private final DataMonitorListener listener;
    private final Watcher chainedWatcher;
    private final String znode;
    private final ZooKeeper zk;
    public boolean dead;
    private int childrenCount=0;

    public DataMonitor(ZooKeeper zk, String znode, Watcher chainedWatcher,
                       DataMonitorListener listener) {
        this.zk = zk;
        this.znode = znode;
        this.chainedWatcher = chainedWatcher;
        this.listener = listener;

        zk.addWatch("/z", this, AddWatchMode.PERSISTENT_RECURSIVE, this, null);


//        zk.exists(znode, true, this, null);
    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        boolean exists;
        Code code = Code.get(rc);
        if (code == Code.OK)
            exists = true;
        else if (code == Code.NONODE)
            exists = false;
        else if (code == Code.SESSIONEXPIRED || code == Code.NOAUTH) {
            dead = true;
            listener.closing(rc);
            return;
        } else return;

        byte[] b = null;
        if (exists) {
            try {
                b = zk.getData(znode, false, null);
                listener.exists(b);
            } catch (InterruptedException e) {
                return;
            } catch (KeeperException e) {
                e.printStackTrace();
            }
        } else {
            listener.deleted();
        }
    }

    @Override
    public void process(WatchedEvent event) {
        String path = event.getPath();
        if (event.getType() == Event.EventType.None) {
            switch(event.getState()) {
                case SyncConnected:
                    break;
                case Expired:
                    dead = true;
                    listener.closing(Code.SessionExpired);
                    break;
            }
        } else {
            if (path != null && (path.equals(znode) || path.startsWith(znode + "/"))) {
                displayChildrenCount(znode);
                zk.exists(znode, false, this, null);
            }
        }
    }

    private void displayChildrenCount(String path) {
        try {
            childrenCount = zk.getAllChildrenNumber(path);
            System.out.println("All children: " + childrenCount);
        } catch (KeeperException e) {
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx) {
        zk.exists(znode, false, this, null);
    }

    public void printZnodeTree(String path) {
        try {
            if (zk.exists(path, false) == null) return;
            if (zk.getChildren(path, false).size() > 0)
            System.out.print(path + ": ");
            for (String child : zk.getChildren(path, false)) {
                System.out.print(child + " ");
            }
            System.out.println();
            for (String child : zk.getChildren(path, false)) {
                printZnodeTree(path + "/" + child);
            }
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    public interface DataMonitorListener {
        void exists(byte data[]);

        void closing(int rc);

        void deleted();
    }
}