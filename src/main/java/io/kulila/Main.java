package io.kulila;
import io.kulila.server.Server;
import io.kulila.server.ServerFX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {

        //Server.main(args);
        ServerFX.main(args);

    }
}