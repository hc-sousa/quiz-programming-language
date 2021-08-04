
import java.util.List;

public final class QMCBuilder {
    protected String id;
    protected String description;
    protected String theme;
    protected Integer difficulty;
    protected Double score;
    protected List<Option> options;
    protected String correctOptionId;

    private QMCBuilder() {
    }

    public static QMCBuilder createQMC() {
        return new QMCBuilder();
    }

    public QMCBuilder id(String id) {
        this.id = id;
        return this;
    }

    public QMCBuilder description(String description) {
        this.description = description;
        return this;
    }

    public QMCBuilder theme(String theme) {
        this.theme = theme;
        return this;
    }

    public QMCBuilder difficulty(Integer difficulty) {
        this.difficulty = difficulty;
        return this;
    }

    public QMCBuilder difficulty(Double difficulty) {
        this.difficulty = (int) difficulty.doubleValue();
        return this;
    }

    public QMCBuilder score(Double score) {
        this.score = score;
        return this;
    }

    public QMCBuilder score(Integer score) {
        this.score = (double) score.intValue();
        return this;
    }

    public QMCBuilder options(List<Option> options) {
        this.options = options;
        return this;
    }

    public QMCBuilder correctOptionId(String correctOptionId) {
        this.correctOptionId = correctOptionId;
        return this;
    }

    public QMC build() {
        QMC qmc = new QMC();
        qmc.setId(id);
        qmc.setDescription(description);
        qmc.setTheme(theme);
        qmc.setDifficulty(difficulty);
        qmc.setScore(score);
        qmc.setOptions(options);
        qmc.setCorrectOptionById(correctOptionId);
        return qmc;
    }
}
