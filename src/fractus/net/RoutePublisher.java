package fractus.net;

/**
 * Responsible for publishing route to self to server
 * @author bowenl2
 */
public class RoutePublisher
    implements Runnable {
    //private RouteManager routeManager;
    private ServerConnector serverConnection;

    public RoutePublisher(IncomingRoute incomingRoute, ServerConnector serverConnection) {
        //this.routeManager = routeManager;
        this.serverConnection = serverConnection;
    }

    @Override
    public void run() {
        while (true) {
            // Wait until Route Manager has incoming route


            // Wait until Server Connection is valid
        }
    }



}
