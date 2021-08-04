
public abstract class Question {
    protected String id;
    protected String description;
    protected String theme;
    protected Integer difficulty;
    protected Double score;
    protected String type;

    abstract Double answerScore(String input);
    abstract void remove(String optionId);
    abstract void remove(Option option);
    abstract String getDefinition(String asId, String varId);

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
