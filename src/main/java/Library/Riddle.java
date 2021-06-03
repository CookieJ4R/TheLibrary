package Library;

public class Riddle {

    public int riddleID;
    public String riddleText;
    public String answer;

    private String solvedBy;
    private String solvedOn;


    public String getRiddleText(){
        return riddleText;
    }

    public boolean checkAnswer(String answer) { return this.answer.equals(answer); }

    public void setSolvedBy(String solvedBy){
        this.solvedBy = solvedBy;
    }

    public String getSolvedBy(){
        return solvedBy;
    }

    public void setSolvedOn(String solvedOn){
        this.solvedOn = solvedOn;
    }

    public String getSolvedOn() {
        return solvedOn;
    }



}
