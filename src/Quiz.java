
import java.util.ArrayList;
import java.util.List;

// trivial quiz class, basically just question list and score
public class Quiz {
    public String id;
    public List<Question> questions;
    public Double score;

    public Quiz(String id, List<Question> questions) {
        this.id = id;
        this.questions = questions;
        for (Question q : questions)
            score += q.getScore();
    }

    public Quiz(String id) {
        this(id, new ArrayList<>());
    }

    public void add(Question question) {
        String qId = question.getId();
        for (Question q : questions) {
            if (q.getId().equals(qId)) {
                System.out.printf("Question with ID '%s' already exists in quiz '%s'.\n", qId, id);
                return;
            }
        }
        questions.add(question);
    }

    public void removeQuestionById(String qId) {
        int idx = getQuestionIdxById(qId);
        if (idx != -1)
            questions.remove(idx);
    }

    public Double getScore() {
        return score;
    }

    private int getQuestionIdxById(String qId) {
        for (int i = 0; i < questions.size(); i++)
            if (questions.get(i).getId().equals(qId))
                return i;
        System.out.println("Invalid question ID.");
        return -1;
    }
}
