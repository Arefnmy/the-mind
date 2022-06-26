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
            m *= 300;
            return m + 3000;
        }
        m *= 400;
        return m + 1000;
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
            catch (InterruptedException e) {
                //e.printStackTrace();
            }
        }
    }
}
