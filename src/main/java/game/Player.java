package game;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class Player {
    protected String name;
    protected final LinkedList<Integer> cards;
    protected boolean isHost;

    protected Player(String name){
        cards = new LinkedList<>();
        this.name = name;
    }

    public void newDeck(List<Integer> deck){
        cards.addAll(deck);
        Collections.sort(cards);
        System.out.println(" player " + this.name + " cards " + cards);
    }

    public String getName() {
        return name;
    }

    public LinkedList<Integer> getCards() {
        return cards;
    }
}
