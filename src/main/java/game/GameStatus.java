package game;

import java.util.LinkedList;
import java.util.List;

public class GameStatus {
    private int heartNumber;
    private int ninjaNumber;
    private int level = 1;
    private final LinkedList<Integer> playedCards;

    public GameStatus(int heartNumber) {
        playedCards = new LinkedList<>();
        this.heartNumber = heartNumber;
        ninjaNumber = 2;
        level = 1;
    }

    public void addCard(int card , boolean first){
        if (first)
            playedCards.addFirst(card);
        else
            playedCards.add(card);
    }

    public int getHeartNumber() {
        return heartNumber;
    }

    public int getNinjaNumber() {
        return ninjaNumber;
    }

    public int getLevel() {
        return level;
    }

    public void changeHeart(boolean minus){
        if (minus)
            heartNumber --;
        else
            heartNumber ++;

        //todo
    }

    public void changeNinja(boolean minus){
        if (minus)
            ninjaNumber --;
        else
            ninjaNumber ++;
    }
}
