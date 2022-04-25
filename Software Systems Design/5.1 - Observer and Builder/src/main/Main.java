package main;

public class Main {
    public static void main(String[] args) {
        NBAManager manager = new NBAManager();
        NBAFan fan = new NBAFan();
        manager.subscribeFan(fan);
        manager.addGame();
    }
}
