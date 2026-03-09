package model;

public class Candidate {
    private int id;
    private String name;
    private String photo;
    private int votes;

    public Candidate() {
    }

    public Candidate(int id, String name, String photo, int votes) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.votes = votes;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public int getVotes() {
        return votes;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }
}