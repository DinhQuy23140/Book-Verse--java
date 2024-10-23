package com.example.bookverse.Class;

public class Person {
    int birth_year, death_year;
    String name;

    public Person(String name, int death_year, int birth_year) {
        this.name = name;
        this.death_year = death_year;
        this.birth_year = birth_year;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBirth_year() {
        return birth_year;
    }

    public void setBirth_year(int birth_year) {
        this.birth_year = birth_year;
    }

    public int getDeath_year() {
        return death_year;
    }

    public void setDeath_year(int death_year) {
        this.death_year = death_year;
    }
}
