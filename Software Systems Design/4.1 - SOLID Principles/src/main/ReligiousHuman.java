package main;

public class ReligiousHuman extends Human implements Religious{
    public ReligiousHuman(String name) {
        super(name);
    }

    @Override
    public void pray() {
        System.out.println(getName() + " is praying.");
    }

    @Override
    public void getMarried() {
        System.out.println(getName() + " got married.");
    }
}
