package Library;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;

public class LibraryTests {

    static AccountManager am;

    static Library library;

    @BeforeClass
    public static void setupForTest(){
        am = new AccountManager();
        am.userList.clear();
        library = new Library();
    }

    @AfterClass
    public static void setupForUse(){

        library.curRiddleID = 0;
        library.updateRiddleID();
        am.userList.clear();
        am.saveUserData();

    }


    @Test
    public void addUserTest() {
        am.userList.clear();
        am.addUser(new User("newUser", "password", "email"));
        assertTrue(am.userList.contains(am.getUser("newUser")));
    }

    @Test
    public void addWithExistingNameTest(){
        am.userList.clear();
        am.addUser(new User("newUser", "password", "email"));
        assertTrue(am.userList.size() == 1);
        am.addUser(new User("newUser", "password", "email"));
        assertTrue(am.userList.size() == 1);
    }

    @Test
    public void isGetUserEqualToUserTest(){
        am.userList.clear();
        User user = new User("newUser", "password", "email");
        am.addUser(user);
        assertEquals(am.getUser("newUser"), user);
    }

    @Test
    public void loginUserTest(){
        am.userList.clear();
        am.addUser(new User("newUser", "password", "email"));
        assertFalse(am.loginUser("newUser", "wrongPassword"));
        assertTrue(am.loginUser("newUser", "password"));
    }

    @Test
    public void listSortUserBasedOnPointsTest(){
        am.userList.clear();
        User user1 = new User("newUser1", "password", "email");
        user1.addPoints(100);
        User user2 = new User("newUser2", "password", "email");
        user2.addPoints(200);
        am.addUser(user1);
        am.addUser(user2);
        assertEquals(am.userList.get(0), user1);
        assertEquals(am.userList.get(1), user2);
        am.sortList();
        assertEquals(am.userList.get(0), user2);
        assertEquals(am.userList.get(1), user1);
    }

    @Test
    public void empty7LeaderboardTest(){
        am.userList.clear();
        am.leaderboardEntries = 7;
        assertEquals("1. ---;---;2. ---;---;3. ---;---;4. ---;---;5. ---;---;6. ---;---;7. ---;---;", am.getLeaderBoard());
    }

    @Test
    public void leaderboard7EntryTest(){
        am.userList.clear();
        am.leaderboardEntries = 7;
        User user = new User("newUser", "password", "email");
        user.addPoints(100);
        am.addUser(user);
        assertEquals("1. newUser;100;2. ---;---;3. ---;---;4. ---;---;5. ---;---;6. ---;---;7. ---;---;", am.getLeaderBoard());
    }

    @Test
    public void leaderboard7EntryIgnore0PointsTest(){
        am.userList.clear();
        am.leaderboardEntries = 7;
        User user1 = new User("newUser1", "password", "email");
        user1.addPoints(100);
        User user2 = new User("newUser2", "password", "email");
        am.addUser(user1);
        am.addUser(user2);
        assertEquals("1. newUser1;100;2. ---;---;3. ---;---;4. ---;---;5. ---;---;6. ---;---;7. ---;---;", am.getLeaderBoard());
    }

    @Test
    public void leaderboard3EntryVariableTest(){
        am.userList.clear();
        am.leaderboardEntries = 3;
        assertEquals(am.getLeaderBoard(), "1. ---;---;2. ---;---;3. ---;---;");
    }

    @Test
    public void readSavedUsersTest(){
        am.userList.clear();
        User user1 = new User("newUser1", "password", "email");
        User user2 = new User("newUser2", "password", "email");
        am.addUser(user1);
        am.addUser(user2);
        assertTrue(am.userList.size() == 2);
        am.userList.clear();
        assertTrue(am.userList.size() == 0);
        am.loadUserData();
        assertTrue(am.userList.size() == 2);
        assertEquals(am.userList.get(0).getUsername(), user1.getUsername());
        assertEquals(am.userList.get(1).getUsername(), user2.getUsername());
    }

    @Test
    public void updateRiddleIDTest(){
        library.curRiddleID = 0;
        assertTrue(library.curRiddleID == 0);
        library.updateRiddleID();
        assertTrue(library.curRiddleID == 1);
    }

    @Test
    public void loadRiddleTest(){
        Riddle riddle = null;
        try {
            riddle = library.readRiddleFromJSON(1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        assertEquals(1, riddle.riddleID);

    }

    @Test
    public void riddleSolvedTest(){
        am.userList.clear();
        am.addUser(new User("RiddleSolver","passwort", "email"));
        library.curRiddleID = 1;
        try {
            Riddle riddle = library.readRiddleFromJSON(library.curRiddleID);
            library.curRiddle = riddle;
            assertEquals(riddle, library.curRiddle);
            library.solveRiddle(am, "RiddleSolver");
            assertNotEquals(riddle, library.curRiddle);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void riddleWithID0ShouldNotExistTest(){
        Riddle riddle = null;
        try {
            riddle = library.readRiddleFromJSON(0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        assertEquals(null, riddle);
    }

    @Test
    public void riddleCorrectAnswerTest(){
        Riddle riddle = new Riddle();
        riddle.answer = "rightAnswer";
        assertTrue(riddle.checkAnswer("rightAnswer"));
    }

    @Test
    public void riddleWrongAnswerTest(){
        Riddle riddle = new Riddle();
        riddle.answer = "rightAnswer";
        assertFalse(riddle.checkAnswer("wrongAnswer"));
    }

    @Test
    public void setgetSolvedByTest(){
        Riddle riddle = new Riddle();
        riddle.setSolvedBy("me");
        assertEquals("me", riddle.getSolvedBy());
    }

    @Test
    public void setgetSolvedOnTest(){
        Riddle riddle = new Riddle();
        riddle.setSolvedOn("today");
        assertEquals("today", riddle.getSolvedOn());
    }

    @Test
    public void riddleIDTest(){
        Riddle riddle = new Riddle();
        riddle.riddleID = 1;
        assertEquals(1, riddle.riddleID);
    }

    @Test
    public void riddleTextTest(){
        Riddle riddle = new Riddle();
        riddle.riddleText = "Riddle Test";
        assertEquals("Riddle Test", riddle.getRiddleText());
    }

    @Test
    public void writeToJsonTest(){
        try {
            library.curRiddle = library.readRiddleFromJSON(1);
            library.curRiddleID = 1;
            library.curRiddle.setSolvedBy("");
            assertEquals(library.curRiddle.getSolvedBy(), "");
            library.writeRiddleToJSON();
            library.curRiddle.setSolvedBy("Me");
            assertEquals(library.curRiddle.getSolvedBy(), "Me");
            library.curRiddle  = library.readRiddleFromJSON(1);
            assertEquals("", library.curRiddle.getSolvedBy());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void compareUserEqualTest(){
        User user1 = new User("User1", "password", "email");
        User user2 = new User("User2", "password", "email");

        user1.addPoints(100);
        user2.addPoints(100);

        assertEquals(0, user1.compareTo(user2));
    }

    @Test
    public void compareUserGreaterTest(){
        User user1 = new User("User1", "password", "email");
        User user2 = new User("User2", "password", "email");

        user1.addPoints(200);
        user2.addPoints(100);

        assertEquals(-1, user1.compareTo(user2));
    }

    @Test
    public void compareUserSmallerTest(){
        User user1 = new User("User1", "password", "email");
        User user2 = new User("User2", "password", "email");

        user1.addPoints(100);
        user2.addPoints(200);

        assertEquals(1, user1.compareTo(user2));
    }

    @Test
    public void QueueIsEmptyTest(){
        assertEquals(0, library.clients.size());
    }

    @Test
    public void initalPointsTest(){
        User user = new User("user", "password", "email");
        assertEquals(0, user.getPoints());
    }

    @Test
    public void addPointsToUserTest(){
        User user = new User("user", "password", "email");
        user.addPoints(100);
        assertEquals(100, user.getPoints());
    }

    @Test
    public void getUsernameTest(){
        User user = new User("user", "password", "email");
        assertEquals("user", user.getUsername());
    }

    @Test
    public void getPasswordTest(){
        User user = new User("user", "password", "email");
        assertEquals("password", user.getPassword());
    }

    @Test
    public void getEmailTest(){
        User user = new User("User", "password", "email");
        assertEquals("email", user.getEmail());
    }

    @Test
    public void caseSensitiveAnswerTest(){
        Riddle riddle = new Riddle();
        riddle.answer = "Answer";
        assertFalse(riddle.checkAnswer("answer"));
    }

    @Test
    public void getUserTest(){
        User user = new User("user", "password", "email");
        am.userList.clear();
        am.addUser(user);
        assertEquals(user, am.getUser("user"));
    }

    @Test
    public void userNotOnLeaderBoardTest(){
        am.userList.clear();
        am.leaderboardEntries = 3;
        for(int i = 0; i < 4; i++){
            User user = new User("user" + i, "password", "email");
            user.addPoints(i * 100);
            am.addUser(user);
        }
        am.sortList();
        assertFalse(am.getLeaderBoard().contains("user0"));
    }

    @Test
    public void userOnLeaderboardTest(){
        am.userList.clear();
        am.leaderboardEntries = 3;
        for(int i = 0; i < 4; i++){
            User user = new User("user" + i, "password", "email");
            user.addPoints(i * 100);
            am.addUser(user);
        }
        am.sortList();
        assertTrue(am.getLeaderBoard().contains("user2"));
    }

    @Test
    public void fullLeaderboardNoPlaceholderTest(){
        am.userList.clear();
        am.leaderboardEntries = 2;
        for(int i = 0; i < 2; i++){
            User user = new User("user" + i, "password", "email");
            user.addPoints(100 + (i * 100));
            am.addUser(user);
        }
        am.sortList();
        assertFalse(am.getLeaderBoard().contains("---"));
    }



}
