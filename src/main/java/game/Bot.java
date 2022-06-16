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

    public long getSleepTime(){
        return 0;
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

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
