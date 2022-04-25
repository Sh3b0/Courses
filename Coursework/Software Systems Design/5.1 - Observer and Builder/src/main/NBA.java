package main;

import java.util.ArrayList;
import java.util.List;

public class NBA {
    List<Observer<Game>> observers;
    List<Game> games;

    public NBA() {
        this.observers = new ArrayList<>();
        this.games = new ArrayList<>();
    }

    public void subscribe(Observer<Game> observer) {
        observers.add(observer);
    }

    public void unsubscribe(Observer<Game> observer) {
        observers.remove(observer);
    }

    public void notifyObservers(Game game) {
        for (Observer<Game> observer : observers) {
            observer.update(game);
        }
    }
}
