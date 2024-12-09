package Server;

import java.util.*;

public class LotteryDraw {
    private final int drawId;
    private int winningNumber;
    private int prizePool;
    private final Map<Integer, List<LotteryUser>> participants = new HashMap<>();

    public LotteryDraw(int drawId) {
        this.drawId = drawId;
        this.prizePool = 0;
    }

    public int getDrawId() {
        return drawId;
    }

    public int getWinningNumber() {
        return winningNumber;
    }

    public void setWinningNumber(int winningNumber) {
        this.winningNumber = winningNumber;
    }

    public boolean addParticipant(LotteryUser user) {
        int number = user.getNumber();
        String email = user.getEmail();

        // Verificar si el usuario ya está registrado con el mismo número
        List<LotteryUser> users = participants.get(number);
        if (users != null) {
            for (LotteryUser existingUser : users) {
                if (existingUser.getEmail().equals(email)) {
                    return false; // El usuario ya está registrado con este número
                }
            }
        }

        // Agregar el usuario a la lista de participantes para el número elegido
        participants.computeIfAbsent(number, k -> new ArrayList<>()).add(user);

        // Incrementar el fondo del premio en 100 SEK
        prizePool += 100;
        return true;
    }



    public void calculateWinners() {
        List<LotteryUser> winners = participants.getOrDefault(winningNumber, Collections.emptyList());
        if (!winners.isEmpty()) {
            int prizePerWinner = prizePool / winners.size();
            for (LotteryUser winner : winners) {
                System.out.println("Enviando correo a: " + winner.getEmail());
                System.out.println("Asunto: ¡Felicidades, has ganado!");
                System.out.println("Cuerpo: Estimado usuario, nos complace informarle que ha ganado el sorteo con el número " + winningNumber + ". Su premio es de " + prizePerWinner + " SEK.");
            }
        } else {
            System.out.println("No hubo ganadores en el sorteo " + drawId + ". El fondo de " + prizePool + " SEK se acumula para el próximo sorteo.");
        }
    }

}
