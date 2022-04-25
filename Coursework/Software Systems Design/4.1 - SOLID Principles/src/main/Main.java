package main;

import main.Languages.English;

public class Main {
    public static void main(String[] args) {
        // A human with no special needs
        Human h1 = new Human("anonymous");
        Language lang = new English();
        System.out.println(h1.getName() + " says: " + h1.sayHello(lang));

        // An employable human
        EmployableHuman h2 = new EmployableHuman("ahmed", 1000);
        h2.becomeEmployee();
        h2.changeSalary(500);
        h2.getSalary();
        h2.calculateTax(15);
        h2.ownCompany();

        // An active human
        ActiveHuman h3 = new ActiveHuman("john");
        h3.playSports();
        h3.addHobby("Swimming");
        h3.getHobbies();

        // A religious human
        ReligiousHuman h4 = new ReligiousHuman("jane");
        h4.pray();
        h4.getMarried();
    }
}
