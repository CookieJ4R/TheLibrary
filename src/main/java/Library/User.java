package Library;

import java.io.Serializable;

public class User implements Serializable, Comparable<User> {

    private String username;
    private String password;
    private String email;

    private int points;


    public User(String username, String password, String email){
        this.username = username;
        this.password = password;
        this.email = email;

        this.points = 0;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    @Override
    public int compareTo(User o) {
        if(this.points < o.points)
            return 1;
        if(this.points > o.points)
            return -1;
        return 0;
    }
}
