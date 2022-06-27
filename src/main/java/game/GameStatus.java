package game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GameStatus {
    private int heartNumber;
    private int ninjaNumber;
    private int level;
    private final LinkedList<Integer> playedCards;
    private final List<String> history;

    public GameStatus(int playerNumber) {
        history = new ArrayList<>();
        playedCards = new LinkedList<>();
        heartNumber = playerNumber;
        ninjaNumber = 2;
        level = 0;
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
            heartNumber--;
        else
            heartNumber ++;
        addHistory(" Hearts : " + heartNumber);
    }

    public boolean changeNinja(boolean minus){
        if (minus) {
            if (ninjaNumber == 0)
                return false;
            ninjaNumber--;
        }
        else
            ninjaNumber ++;
        addHistory(" Ninjas : " + ninjaNumber);
        return true;
    }

    public void nextLevel(){
        playedCards.clear();
        level ++;
        addHistory(" Level : " + level);

        if (level % 3 == 0 && level != 12) changeNinja(false);

        if (level % 3 == 2 && level != 11) changeHeart(false);

    }

    public int getLastPlayedCard(){
        if (playedCards.isEmpty()) return 0;
        else return playedCards.getLast();
    }

    public LinkedList<Integer> getPlayedCards() {
        return playedCards;
    }

    public void addHistory(String message){
        synchronized (history) {
            history.add(message);
        }
    }

    public List<String> getHistory() {
        return history;
    }
}
