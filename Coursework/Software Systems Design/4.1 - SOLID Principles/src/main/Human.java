package main;
import main.Language;

class Human {
    private final String name;

    public Human(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    String sayHello(Language lang) {
        return lang.HelloWord();
    }
}
