package edu.northeastern.gymhub.Models;

public class Exercise {

    private String name;
    private String notes;
    private int weight;
    private int sets;
    private int reps;
    private int time;
    public void Exercise(){
    }

    public void setName(String name){
        this.name = name;
    }

    public void setNotes(String notes){
        this.notes = notes;
    }

    public void setWeight(int weight){
        this.weight = weight;
    }

    public void setSets(int sets){
        this.sets = sets;
    }

    public void setReps(int reps){
        this.reps = reps;
    }

    public void setTime(int time){
        this.time = time;
    }

    public String getName(){return this.name;}
    public String getNotes(){return this.notes;}
    public int getWeight(){return this.weight;}
    public int getSets(){return this.sets;}
    public int getReps(){return this.reps;}
    public int getTime(){return this.time;}
}
