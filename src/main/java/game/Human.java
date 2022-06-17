package game;

public class Human extends Player{
    private int token;

    public Human(int token , String name) {
        super(name);
        this.token = token;
    }

    public int getToken() {
        return token;
    }
}
