package Library;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import io.javalin.Javalin;
import io.javalin.serversentevent.SseClient;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Library {

    public Riddle curRiddle;
    int curRiddleID;

    public ConcurrentLinkedQueue<SseClient> clients = new ConcurrentLinkedQueue<>();

    public Library(){

        AccountManager am = new AccountManager();

        try {
            BufferedReader idReader = new BufferedReader(new InputStreamReader(new FileInputStream(System.getProperty("user.dir") + "/curRiddleID.txt"), "UTF-8"));
            curRiddleID = Integer.parseInt(idReader.readLine());

            curRiddle = readRiddleFromJSON(curRiddleID);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Javalin app = Javalin.create().enableStaticFiles("/public").start(7777);

        app.get("/getcurriddle", ctx -> {
            ctx.header("currentRiddle", "curRid");
            if(curRiddle != null)
                ctx.html(curRiddle.getRiddleText());
            else
                ctx.html("No more riddles available at the moment!<br><br>Please check back later");
        });

        app.post("/submitanswer", ctx -> {
           String answer = ctx.formParam("answer");
            ctx.header("riddleSolveAttempt", "true");
           if(curRiddle.checkAnswer(answer)){
               String username = ctx.cookie("Library");
                solveRiddle(am, username);
               ctx.result("rightAnswer");
           }else
               ctx.result("wrongAnswer");

        });

        app.sse("/sse", sseClient -> {
            clients.add(sseClient);
            sseClient.onClose(() -> clients.remove(sseClient));
        });

        app.post("/login", ctx -> {
            String username = ctx.formParam("username");
            ctx.header("login", "login");
            if(am.loginUser(username, ctx.formParam("password")))
                ctx.cookie("Library", username);
            else
                ctx.result("wrongCredentials");

        });

        app.post("/logout", ctx -> {
            ctx.removeCookie("Library");
        });

        app.post("/register", ctx -> {

            if(am.addUser(new User(ctx.formParam("username"), ctx.formParam("password"), ctx.formParam("email")))) {
                ctx.result("success");
                ctx.cookie("Library", ctx.formParam("username"));
            }
            else
                ctx.result("username already in use");
        });

        app.get("/getleaderboard", ctx -> {
            ctx.header("leaderboard", "points");
            ctx.html(am.getUser(ctx.cookie("Library")).getPoints() + "|" + am.getLeaderBoard());
        });

        app.get("/loadriddlewithnumber", ctx -> {
            ctx.header("archiveRiddle", "withNumber");
            int riddleID = curRiddleID - 1;
            if(!ctx.queryParam("riddleid").equals(""))
                riddleID = Integer.valueOf(ctx.queryParam("riddleid"));
            if(riddleID >= curRiddleID)
                ctx.html("This Riddle has not been solved yet!<br>Current Riddle has ID: " + curRiddleID);
            else {
                Riddle riddle = readRiddleFromJSON(riddleID);
                if (riddle != null)
                    ctx.html("Riddle Nr: " + riddle.riddleID + "<br><br>" + riddle.getRiddleText() + "<br><br>Correct Answer: " + riddle.answer + "<br><br>Solved by: " + riddle.getSolvedBy() + "<br>Solved on: " + riddle.getSolvedOn());
                else
                    ctx.html("There is no Riddle with this ID.");
            }
        });

    }

    public Riddle readRiddleFromJSON(int id) throws FileNotFoundException, UnsupportedEncodingException {
        File f = new File(System.getProperty("user.dir") + "/riddles/Riddle" + id + ".json");
        if(f.exists()) {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
            return gson.fromJson(reader,Riddle.class);
        }
        return null;
    }

    public void writeRiddleToJSON() throws FileNotFoundException, UnsupportedEncodingException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String outputString = gson.toJson(curRiddle);
        PrintWriter writer = new PrintWriter(new File(System.getProperty("user.dir") + "/riddles/Riddle" + curRiddleID + ".json"), "UTF-8");
        writer.write(outputString);
        writer.flush();
        writer.close();
    }

    private void updateRiddleOnClients(){
        for(SseClient client : clients){
            if(curRiddle != null)
                client.sendEvent(curRiddle.getRiddleText());
            else
                client.sendEvent("No more riddles available at the moment!<br><br>Please check back later...");
        }
    }

    public void updateRiddleID(){
        curRiddleID++;
        PrintWriter writer;
        try {
            writer = new PrintWriter(new File(System.getProperty("user.dir") + "/curRiddleID.txt"), "UTF-8");
            writer.write(String.valueOf(curRiddleID));
            writer.flush();
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void solveRiddle(AccountManager am, String username) throws FileNotFoundException, UnsupportedEncodingException {
        curRiddle.setSolvedBy(username);
        curRiddle.setSolvedOn(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
        am.getUser(username).addPoints(100);
        am.sortList();
        am.saveUserData();
        writeRiddleToJSON();
        updateRiddleID();
        curRiddle = readRiddleFromJSON(curRiddleID);
        updateRiddleOnClients();
    }

}
