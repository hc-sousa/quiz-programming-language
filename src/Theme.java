
import java.util.HashMap;

public class Theme{
    private String theme;
    private HashMap<String,Question> questions = new HashMap<>();

    public Theme(String theme, HashMap<String, Question> questions){
        this.theme = theme;
        this.questions = questions;
    }

    public String getTheme(){
        return this.theme;
    }

    public HashMap<String,Question> getQuestions(){
        return this.questions;
    }

    public void setQuestions(HashMap<String,Question> qst){
        this.questions = qst;
    }



    public String toString(){
        return this.theme + this.questions;
    }
}
