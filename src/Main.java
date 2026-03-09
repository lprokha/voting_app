import server.VoteServer;

public class Main {
    public static void main(String[] args) throws Exception {
        VoteServer server = new VoteServer("localhost", 9888);
        server.start();
    }
}