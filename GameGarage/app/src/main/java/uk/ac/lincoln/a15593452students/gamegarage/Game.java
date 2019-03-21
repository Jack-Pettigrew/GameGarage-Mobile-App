package uk.ac.lincoln.a15593452students.gamegarage;

import java.io.Serializable;

// Class to hold Game search result information
public class Game implements Serializable {

    // Game List Data
    private String name;
    private String image;
    private String imageScreen;
    private String deck;
    private int id;

    // Favourites Constructor
    public Game(String name, String imageScreen) {
        this.name = name;
        this.imageScreen = imageScreen;
    }

    // Json Constructor
    public Game(String name, String image, String imageScreen, String deck, int id) {
        this.name = name;
        this.image = image;
        this.imageScreen = imageScreen;
        this.deck = deck;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageScreen() {
        return imageScreen;
    }

    public String getDeck() {
        return deck;
    }
}
