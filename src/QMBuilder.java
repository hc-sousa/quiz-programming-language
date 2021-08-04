
import java.util.List;
import java.util.Map;

public final class QMBuilder {
    protected String id;
    protected String description;
    protected String theme;
    protected Integer difficulty;
    protected Double score;
    protected List<Option> leftOptions, rightOptions;
    // protected Map<Option, Option> matches;
    protected Map<String, String> matchesById;
    protected List<Match> matchList;

    private QMBuilder() {
    }

    public static QMBuilder createQM() {
        return new QMBuilder();
    }

    public QMBuilder id(String id) {
        this.id = id;
        return this;
    }

    public QMBuilder description(String description) {
        this.description = description;
        return this;
    }

    public QMBuilder theme(String theme) {
        this.theme = theme;
        return this;
    }

    public QMBuilder difficulty(Integer difficulty) {
        this.difficulty = difficulty;
        return this;
    }

    public QMBuilder difficulty(Double difficulty) {
        this.difficulty = (int) difficulty.doubleValue();
        return this;
    }


    public QMBuilder score(Double score) {
        this.score = score;
        return this;
    }

    public QMBuilder score(Integer score) {
        this.score = (double) score.intValue();
        return this;
    }

    public QMBuilder leftOptions(List<Option> leftOptions) {
        this.leftOptions = leftOptions;
        return this;
    }

    public QMBuilder rightOptions(List<Option> rightOptions) {
        this.rightOptions = rightOptions;
        return this;
    }

    public QMBuilder matchesById(Map<String, String> matchesById) {
        this.matchesById = matchesById;
        return this;
    }

    public QMBuilder matchesById(List<Match> matchList) {
        this.matchList = matchList;
        return this;
    }

    public QM build() {
        QM qM = new QM();
        qM.setId(id);
        qM.setDescription(description);
        qM.setTheme(theme);
        qM.setDifficulty(difficulty);
        qM.setScore(score);
        qM.setLeftOptions(leftOptions);
        qM.setRightOptions(rightOptions);
        if (matchList != null)
            qM.setMatchesById(matchList);
        else if (matchesById != null)
            qM.setMatchesById(matchesById);
        return qM;
    }
}
