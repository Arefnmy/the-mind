package game;

public class Bot extends Player implements Runnable {
    private Thread thread;
    private Game game;

    public Bot(String name){
        super(name);
    }

    public void startGame(Game game){
        this.game = game;
        thread = new Thread(this);
        thread.start();
    }

    public int getSleepTime(){
        int m = cards.getFirst() - game.getGameStatus().getLastPlayedCard();
        if (m > 50) {
            m -= 50;
            return m * 97;
        }
        return m * 53;

    }

    public int playCard(){
        int card = cards.getFirst();
        cards.remove(card);
        game.play(this , card);
        return card;
    }

    public void resetSleep(){
        thread.interrupt();
        thread = new Thread(this);
        thread.start();
    }

    public void run(){
        if (!cards.isEmpty()) {
            try {
                Thread.sleep(getSleepTime());
                int card = playCard();
                game.play(this , card);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
