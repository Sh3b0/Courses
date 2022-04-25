package main;

import java.util.ArrayList;
import java.util.List;

class ActiveHuman extends Human implements Active{
    private final ArrayList<String> hobbies = new ArrayList<String>();;
    public ActiveHuman(String name) {
        super(name);
    }

    @Override
    public void playSports() {
        System.out.println("I'm playing sports!");
    }

    @Override
    public void addHobby(String hobby) {
        hobbies.add(hobby);
    }

    public void getHobbies() {
        System.out.println(getName() + " Hobbies: ");
        for(String hobby : hobbies){
            System.out.print(hobby + " ");
        }
        System.out.println();
    }
}
