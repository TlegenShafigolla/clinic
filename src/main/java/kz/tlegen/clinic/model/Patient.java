package kz.tlegen.clinic.model;

public class Patient {
    private Long id;
    private String name;
    private int age;

    public Patient(Long id, String name, int age){
        this.id = id;
        this.name = name;
        this.age = age;
    }
    public Long getId() {
        return id;
    }
    public int getAge() {
        return age;
    }
    public String getName() {
        return name;
    }
}
