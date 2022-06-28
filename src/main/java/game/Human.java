package game;

public class Human extends Player{
    private final String token;

    public Human(String token , String name) {
        super(name);
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
