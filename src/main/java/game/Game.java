package game;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Game {
    private final List<Bot> botList;
    private final List<Player> playerList;
    private final List<Integer> cards;
    private final GameStatus gameStatus;

    public Game(List<Bot> botList, List<Player> playerList) {
        this.botList = botList;
        this.playerList = playerList;

        gameStatus = new GameStatus(playerList.size());
        cards = new LinkedList<>();
        for (int i = 1; i <= 100 ; i++) {
            cards.add(i);
        }
    }

    public void nextLevel(){
        Collections.shuffle(cards);
        gameStatus.nextLevel();
        giveCard();
    }

    public void giveCard(){
        int level = gameStatus.getLevel();
        int j = 0;
        for (Player p : playerList){
            List<Integer> cardList = new LinkedList<>();
            for (int i = 0; i < level; i++) {
                cardList.add(cards.get(j));

                j++;
            }
            p.newDeck(cardList);
        }
    }

    public void play(Player player , int card){
        gameStatus.addHistory(player.name + " played card " + card);
        gameStatus.addCard(card , false);

        for (Bot b : botList){
            b.interrupt();
        }

        boolean shouldHeartRemove = false;
        for (Player p : playerList){
            if (!p.getCards().isEmpty() && p.getCards().getFirst() < card){
                shouldHeartRemove = true;
                while (!p.getCards().isEmpty() && p.getCards().getFirst() < card){
                    System.out.println("removed from " + p.getName() + " with card " + card);
                    int c = p.playCard();
                    gameStatus.addHistory("card " + c + " removed from " + p.getName());
                    gameStatus.addCard(c , true);
                }
            }
        }
        if (shouldHeartRemove){
            gameStatus.changeHeart(true);
        }

        boolean allEmpty = true;
        for(Player p: playerList){
            if(!p.getCards().isEmpty()) {
                allEmpty = false;
                break;
            }
        }
        if(allEmpty) {
            nextLevel();
            System.out.println(" level " + gameStatus.getLevel());
        }

        for (Bot b : botList){
            b.resetSleep();
        }

    }

    public boolean playNinja(Player player) {
        if (gameStatus.changeNinja(true)) {
            gameStatus.addHistory(player.name + " played ninja");
            for (Bot b : botList) {
                b.interrupt();
            }

            for (Player p : playerList) {
                if (!p.getCards().isEmpty()) {
                    int card = p.playCard();
                    gameStatus.addHistory(p.name + " played card " + card);
                    gameStatus.addCard(card, true);
                }
            }

            boolean allEmpty = true;//function todo
            for (Player p : playerList) {
                if (!p.getCards().isEmpty()) {
                    allEmpty = false;
                    break;
                }
            }
            if (allEmpty) {
                nextLevel();
                System.out.println(" level " + gameStatus.getLevel());
            }

            for (Bot b : botList) {
                b.resetSleep();
            }
            return true;
        }
        return false;
    }

    public GameStatus getGameStatus(){
        return gameStatus;
    }
}
