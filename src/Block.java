
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Collections;

public class Block {
    public String id;
    public List<Question> questions;
    public int idx;
    public Double score;
    public List<Double> questionUserScores;

    public Block(String id, List<Question> questions) {
        this.id = id;
        this.questions = questions;
        questionUserScores = new ArrayList<>();
        for (Question q : questions) {
            score += q.getScore();
            questionUserScores.add(0.0);
        }
        idx = 0;
        score = 0.0;
    }

    @Override
    public String toString() {
        return String.format("%s (%d questions)", id, questions.size());
    }

    public Block(String id) {
        this(id, new ArrayList<>());
    }

    public void add(Question question) {
        String qId = question.getId();
        for (Question q : questions) {
            if (q.getId().equals(qId)) {
                System.out.printf("Question with ID '%s'  already exists in block '%s'.\n", qId, id);
                return;
            }
        }
        questions.add(question);
        questionUserScores.add(0.0);
        score += question.getScore();
    }

    public void remove(Question q) {
        remove(q.getId());
    }

    public void remove(String qId) {
        int i;
        boolean found = false;
        for (i = 0; i < questions.size(); i++) {
            if (questions.get(i).getId().equals(qId)) {
                found = true;
                break;
            }
        }
        if (found) {
            score -= questions.get(i).getScore();
            questions.remove(i);
        } else {
            System.err.printf(
                "Couldn't remove question '%s' from block '%s': block has no question with ID '%s'\n",
                qId, id, qId
            );
        }
    }

    public void execute() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Block " + id);
        System.out.println(
                ">'{question ID}' to navigate to another question.\n"
                        + "'{answer}' to answer the current question.\n"
                        + "    QMC Example: A1\n"
                        + "    QM Example: L1-R1,L2-R2,L3-R3\n"
                        + "'.' to leave this block."
        );
        String prefix;
        while (true) {
            for (int i = 0; i < questions.size(); i++) {
                prefix = i == idx ? "> " : "  ";
                System.out.println(prefix + questions.get(i).getId());
            }
            Question current = questions.get(idx);
            System.out.println(current);
            String input = sc.nextLine();
            if (input.length() == 0)
                input = "\0";
            char startChar = input.charAt(0);
            if (startChar == '>') { // go to another question
                String qId = input.substring(1);
                idx = getQuestionIdxById(qId);
                current = questions.get(idx);
            } else if (startChar == '.') { // finish this block
                return;
            } else { // answer the current question
                if (input.equals("\0"))
                    input = "";
                Double answerScore = current.answerScore(input);
                questionUserScores.set(idx, answerScore);
                if (idx < questions.size() - 1)
                    idx++;
            }
        }
    }
    
    public void shuffle() {
        Collections.shuffle(questions);
    }

    private int getQuestionIdxById(String qId) {
        for (int i = 0; i < questions.size(); i++)
            if (questions.get(i).getId().equals(qId))
                return i;
        System.out.println("Invalid question ID.");
        return idx;
    }

    public String getId() {
        return id;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public int getIdx() {
        return idx;
    }

    public Double getScore() {
        return score;
    }

    public List<Double> getQuestionUserScores() {
        return questionUserScores;
    }
}
