package main;

public class GameBuilder {
    private String team1, team2;

    public String getTeam1() {
        return team1;
    }

    public String getTeam2() {
        return team2;
    }

    public GameBuilder setTeam1(String team1) {
        this.team1 = team1;
        return this;
    }

    public GameBuilder setTeam2(String team2) {
        this.team2 = team2;
        return this;
    }

    public Game build() {
        return new Game(this);
    }
}
