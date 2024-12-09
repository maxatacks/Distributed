package Client;

import com.google.gson.Gson;
import java.io.*;
import java.net.*;
import java.util.Map;

import java.io.*;
import java.net.*;

public class LotteryClient {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        LotteryClient client = new LotteryClient();
        client.sendRegisterRequest("user@example.com", 1, 1);
    }

    public void sendRegisterRequest(String email, int number, int drawId) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String request = "REGISTER " + email + " " + number + " " + drawId;
            out.println(request);
            String response = in.readLine();
            System.out.println("Server response: " + response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
