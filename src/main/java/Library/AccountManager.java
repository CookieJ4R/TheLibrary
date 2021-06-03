package Library;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AccountManager {

    public ArrayList<User> userList = new ArrayList<>();

    public int leaderboardEntries = 7;

    public AccountManager(){

        try{
            loadUserData();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public boolean addUser(User user){
        if(userList.contains(getUser(user.getUsername())))
            return false;

        userList.add(user);
        saveUserData();
        return true;
    }

    public User getUser(String username){
        for(User user : userList)
            if(user.getUsername().equals(username))
                return user;
        return null;
    }

    public boolean loginUser(String username, String password){

        User tmp = getUser(username);
        if(tmp != null && tmp.getPassword().equals(password))
            return true;

        return false;

    }

    public void sortList(){
        userList.sort((u1, u2) -> u1.compareTo(u2));
    }

    public String getLeaderBoard(){
        int count = 0;
        List<User> leaderboard = userList.subList(0, Math.min(userList.size(), leaderboardEntries));
        String leaderboardString = "";
        for(int i = 0; i < leaderboard.size(); i++) {
            User tmpUser = leaderboard.get(i);
            if(tmpUser.getPoints() != 0){
                count++;
                leaderboardString += i + 1 + ". " + tmpUser.getUsername() + ";" + tmpUser.getPoints() + ";";
            }
        }

        for(int i = count; i < leaderboardEntries; i++)
            leaderboardString += i + 1 + ". ---;---;";

        return leaderboardString;
    }


    public void loadUserData(){
        try (FileInputStream fip = new FileInputStream(System.getProperty("user.dir") + "/users.ser")) {
            try (ObjectInputStream ois = new ObjectInputStream(fip)) {
                userList = (ArrayList<User>) ois.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveUserData(){
        try(FileOutputStream fop = new FileOutputStream(System.getProperty("user.dir") + "/users.ser")){
            ObjectOutputStream oos = new ObjectOutputStream(fop);
            oos.writeObject(userList);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
