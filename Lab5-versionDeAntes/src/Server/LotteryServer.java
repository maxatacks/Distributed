package Server;

import Server.LotteryDraw;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class LotteryServer {
    private static final int PORT = 12345;
    private static final int DRAW_INTERVAL_SECONDS = 5;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final List<LotteryDraw> draws = new ArrayList<>();
    private final Map<Integer, LotteryDraw> drawHistory = new HashMap<>();
    private int nextDrawId = 1;

    public static void main(String[] args) {
        LotteryServer server = new LotteryServer();
        server.start();
    }

    public void start() {
        scheduleDraws();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Lottery Server is running...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void scheduleDraws() {
        Runnable drawTask = () -> {
            LotteryDraw draw = new LotteryDraw(nextDrawId++);
            draw.setWinningNumber(generateRandomNumber());
            draw.calculateWinners();
            drawHistory.put(draw.getDrawId(), draw);
            draws.add(draw);
            System.out.println("Draw " + draw.getDrawId() + " completed with winning number: " + draw.getWinningNumber());
        };
        scheduler.scheduleAtFixedRate(drawTask, 0, DRAW_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    private int generateRandomNumber() {
        //Intervalo de 0 a 5
        //return new Random().nextInt(5);
        return 1;
    }

    private class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String request = in.readLine();
                String response = handleRequest(request);
                out.println(response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String handleRequest(String request) {
            // Dividir la solicitud en partes utilizando el espacio como delimitador
            String[] parts = request.split(" ");
            if (parts.length != 4 || !parts[0].equalsIgnoreCase("REGISTER")) {
                return "Error: Formato de solicitud inválido.";
            }

            String email = parts[1];
            int number;
            int drawId;

            try {
                number = Integer.parseInt(parts[2]);
                drawId = Integer.parseInt(parts[3]);
            } catch (NumberFormatException e) {
                return "Error: Número o drawId inválido.";
            }

            // Validar que el número esté dentro del rango permitido (0-255)
            if (number < 0 || number > 255) {
                return "Error: El número debe estar entre 0 y 255.";
            }

            // Buscar el sorteo correspondiente en el historial
            LotteryDraw draw = drawHistory.get(drawId);
            if (draw == null) {
                return "Error: Sorteo no encontrado.";
            }

            // Crear un nuevo usuario con los datos proporcionados
            LotteryUser user = new LotteryUser(email, number, drawId);

            // Intentar agregar al usuario como participante del sorteo
            boolean success = draw.addParticipant(user);
            if (success) {
                return "Registro exitoso.";
            } else {
                return "Error: Ya está registrado con ese número para este sorteo.";
            }
        }

    }
}
