package game;

import java.util.LinkedList;

public class GameStatus {
    private int heartNumber;
    private int ninjaNumber;
    private int level;
    private final LinkedList<Integer> playedCards;

    public GameStatus(int heartNumber) {
        playedCards = new LinkedList<>();
        this.heartNumber = heartNumber;
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

    public void nextLevel(){
        level ++;
        if (level % 3 == 0 && level != 12) ninjaNumber ++;

        if (level % 3 == 2 && level != 11) heartNumber ++;

//        switch (level){
//            case 2 :
//            case 5 :
//            case 8 :
//                ninjaNumber ++;
//                break;
//            case 3 :
//            case 6:
//            case 9:
//                heartNumber ++;
//                break;
//        }

    }

    public int getLastPlayedCard(){
        if (playedCards.isEmpty()) return 0;
        else return playedCards.getLast();
    }
}
