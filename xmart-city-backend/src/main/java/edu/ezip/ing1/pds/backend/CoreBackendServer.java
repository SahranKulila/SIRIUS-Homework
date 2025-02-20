package edu.ezip.ing1.pds.backend;

import edu.ezip.commons.connectionpool.config.impl.ConnectionPoolImpl;
import edu.ezip.ing1.pds.backend.config.CoreBackendServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;


// cat insert-request.json | socat tcp:localhost:45065 -
// cat select-request.json | socat tcp:localhost:45065 -
// mvn clean compile assembly:single install

public class CoreBackendServer implements Runnable
{
    private final static String LoggingLabel = "C o r e - B a c k e n d - S e r v e r";
    private final Logger logger = LoggerFactory.getLogger(LoggingLabel);
    private final static String coreBackendServerConfigDefaultFileName = "core-backend-server.yaml";
    private static final String threadName = "core-backend-server";
    private static final String dbEditorIsPGSQLHere = "mysql";

    private final CoreBackendServerConfiguration config = withConfiguration();

    private final ServerSocket coreServerSocket;
    private final Thread coreThread;
    private final Set<RequestHandler> requestHandlers = Collections.synchronizedSet(new LinkedHashSet<RequestHandler>());
    private volatile boolean topToStop = false;
    private int requestHandlerCreatedSoFar = 0;

    private ConnectionPoolImpl connectionPool = ConnectionPoolImpl.getInstance(dbEditorIsPGSQLHere);

    private final CoreBackendServerConfiguration withConfiguration () {
        final Yaml yaml = new Yaml(new Constructor(CoreBackendServerConfiguration.class));
        final InputStream nptStrm =
                this.getClass().getClassLoader().getResourceAsStream(coreBackendServerConfigDefaultFileName);
        logger.debug("Load config file : {}", coreBackendServerConfigDefaultFileName);
        final CoreBackendServerConfiguration configHere =  yaml.load(nptStrm);
        logger.debug("Configuration loaded : {}", configHere.toString());
        return configHere;
    }

    public CoreBackendServer() throws IOException, SQLException {
        coreServerSocket = new ServerSocket(config.getListenPort());
        coreServerSocket.setSoTimeout(5000);
        logger.debug("Configuration loaded : {}", coreServerSocket.toString());
        coreThread = new Thread(this, threadName);
        coreThread.start();
    }

    public void join() throws InterruptedException {
        coreThread.join();
    }

    @Override
    public void run() {
        while ( !topToStop ) {
            try {
                logger.trace("{} {}", topToStop, connectionPool.available());
                if (0 < connectionPool.available()) {
                    final Socket accept = coreServerSocket.accept();
                    final RequestHandler requestHandler = new RequestHandler(
                                    accept,
                                    connectionPool.get(),
                                    requestHandlerCreatedSoFar++,
                                    this);

                    requestHandlers.add(requestHandler);
                }
            }
            catch (SocketTimeoutException es) {
                logger.trace("Timeout on accept : topToStop = {}", topToStop) ;
            }
            catch (IOException e) {
                logger.error("There is I/O mess here : exception tells  {}", e) ;
                topToStop = true;
            }
        }
        logger.debug("Main Thread in Core Backend Server is terminated - topToStop = {}", topToStop) ;
    }

    public synchronized void completeRequestHandler(final RequestHandler requestHandler) {
        try {
            connectionPool.release(requestHandler.getConnection());
        } catch (InterruptedException e) {
            logger.error("Something wrong while releasing the connection : exception tells  {}", e) ;
        }
        try {
            requestHandler.getSocket().close();
        } catch (IOException e) {
            logger.error("There is I/O error while closing the client socket. Just to inform you. Let's continue anyway. exception tells  {}", e) ;
        }
        requestHandlers.remove(requestHandler);

    }
    public synchronized void stop() {
        logger.trace("Stop() called within Core Backend Server ... ");
        topToStop = true;
        try {
            connectionPool.terminatePool();
        } catch (SQLException e) {
            logger.error("Something wrong while terminating the pool : exception tells  {}", e) ;
        }
    }
}
