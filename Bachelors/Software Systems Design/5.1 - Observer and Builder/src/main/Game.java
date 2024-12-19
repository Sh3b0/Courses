package main;

public class Game {
    private final String team1, team2;

    public Game(GameBuilder b) {
        this.team1 = b.getTeam1();
        this.team2 = b.getTeam2();
    }

    public void play() {
        System.out.println("main.Game is being played...");
    }

    @Override
    public String toString() {
        return "main.Game{" +
                "team1='" + team1 + '\'' +
                ", team2='" + team2 + '\'' +
                '}';
    }
}
