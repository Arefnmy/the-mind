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
        int m = Math.abs(cards.getFirst() - game.getGameStatus().getLastPlayedCard());
        if (m > 50) {
            m -= 50;
            m *= 300;
            return m + 4000;
        }
        m *= 400;
        return m + 1500;
    }

    public void interrupt(){
       thread.interrupt();
    }

    public void resetSleep(){
        thread = new Thread(this);
        thread.start();
    }

    public void run(){
        if (!cards.isEmpty()) {
            try {
                Thread.sleep(getSleepTime());
                int card = playCard();
                game.play(this , card);
            }
            catch (InterruptedException ignored) {

            }
        }
    }
}
