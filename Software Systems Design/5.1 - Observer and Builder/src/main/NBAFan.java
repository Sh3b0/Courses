package main;

public class NBAFan implements Observer<Game> {
    @Override
    public void update(Game game) {
        System.out.println(game.toString());
    }
}
