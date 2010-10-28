/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fractus.net;

/**
 * Responsible for publishing route to self to server
 * @author bowenl2
 */
public class RoutePublisher
    implements Runnable {
    private RouteManager routeManager;
    private ServerConnection serverConnection;

    public RoutePublisher(RouteManager routeManager, ServerConnection serverConnection) {
        this.routeManager = routeManager;
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
