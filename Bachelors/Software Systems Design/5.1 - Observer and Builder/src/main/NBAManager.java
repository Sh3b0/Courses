package main;

import java.util.Scanner;

public class NBAManager {
    public NBA nba;
    private final Scanner scanner;

    public NBAManager() {
        this.nba = new NBA();
        this.scanner = new Scanner(System.in);
    }

    void addGame() {
        System.out.println("Enter game in format \"team1 team2\"");
        String line = scanner.nextLine();
        String[] teams = line.split(" ");
        if(teams.length != 2){
            System.out.println("Wrong format");
            return;
        }
        GameBuilder gb = new GameBuilder();
        nba.games.add(gb.setTeam1(teams[0]).setTeam2(teams[1]).build());
        this.nba.notifyObservers(nba.games.get(nba.games.size() - 1));
    }

    void subscribeFan(Observer<Game> observer) {
        nba.subscribe(observer);
    }

}
