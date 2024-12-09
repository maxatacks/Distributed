package Server;

public class LotteryUser {
    private final String email;
    private final int number;
    private final int drawId;

    public LotteryUser(String email, int number, int drawId) {
        this.email = email;
        this.number = number;
        this.drawId = drawId;
    }

    public String getEmail() {
        return email;
    }

    public int getNumber() {
        return number;
    }

    public int getDrawId() {
        return drawId;
    }
}

