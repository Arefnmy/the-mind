package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

        System.out.println(playerList.size());
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
        System.out.println(player.name + " played card " + card);
        gameStatus.addCard(card , false);

        for (Bot b : botList){
            b.interrupt();
        }

        boolean shouldHeartRemove = false;
        for (Player p : playerList){
            if (!p.getCards().isEmpty() && p.getCards().getFirst() < card){
                shouldHeartRemove = true;
//                gameStatus.changeHeart(true);
//                shouldHeartRemove = false;

                while (!p.getCards().isEmpty() && p.getCards().getFirst() < card){
                    System.out.println("removed from " + p.getName() + " with card " + card);
//                    p.getCards().removeFirst();
                    gameStatus.addCard(p.getCards().removeFirst() , true);
                }
            }
        }
        if (shouldHeartRemove){
            gameStatus.changeHeart(true);
        }

        for (Bot b : botList){
            b.resetSleep();
        }

        //todo
    }

    public void playNinja(Player player){
        gameStatus.changeNinja(true);
        for (Player p : playerList){
            if (!p.getCards().isEmpty())
                gameStatus.addCard(p.getCards().removeFirst() , true);
        }
    }

    public GameStatus getGameStatus(){
        return gameStatus;
    }
}
