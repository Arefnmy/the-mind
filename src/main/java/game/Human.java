package game;

public class Human extends Player{
    private boolean isHost;
    private int token;

    public Human(int token , String name , boolean isHost) {
        super(name);
        this.isHost = isHost;
        this.token = token;
    }

    public boolean isHost() {
        return isHost;
    }

    public int getToken() {
        return token;
    }
}
