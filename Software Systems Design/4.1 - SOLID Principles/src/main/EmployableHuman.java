package main;

public class EmployableHuman extends Human implements Employable {
    private int salary;

    public EmployableHuman(String name, int salary) {
        super(name);
        this.salary = salary;
    }

    @Override
    public void calculateTax(int percentage) {
        System.out.println(getName() + " Taxes = " + percentage / 100.0 * salary + "$");
    }

    @Override
    public void ownCompany() {
        System.out.println(getName() + " now owns a company!");
    }

    @Override
    public void becomeEmployee() {
        System.out.println(getName() + " is now an employee!");
    }

    public void changeSalary(int delta){
        salary += delta;
    }

    public void getSalary(){
        System.out.println(getName() + " salary = " + salary);
    }
}
