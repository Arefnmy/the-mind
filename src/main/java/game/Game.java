package game;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Game {
    private final List<Bot> botList;
    private final List<Player> playerList;
    //private final List<Integer> cards;
    private final GameStatus gameStatus;

    public Game(List<Bot> botList, List<Player> humanList) {
        this.botList = botList;
        playerList = new ArrayList<>(botList);
        playerList.addAll(humanList);

        gameStatus = new GameStatus(playerList.size());
        //cards = IntStream.range(1 , 101).collect(Collectors.toList());
    }


    public void play(Player player , int card){
        gameStatus.addCard(card , false);
        for (Bot b : botList){
            b.resetSleep();
        }

        boolean isHeartRemoved = false;
        for (Player p : playerList){
            if (!p.getCards().isEmpty() && p.getCards().getFirst() < card){
                isHeartRemoved = true;
                while (p.getCards().getFirst() < card){
                    p.getCards().removeFirst();
                }
            }
        }
        if (isHeartRemoved){
            gameStatus.changeHeart(true);
        }

        //todo
    }
}
