
import java.util.Collections;
import java.util.List;

public class QMC extends Question {
    public List<Option> options;
    public Option correctOption;
    public String correct;

    public QMC() {
        type = "qmc";
    }

    public void shuffle() {
        Collections.shuffle(options);
    }

    public void remove(Option option) {
        remove(option.getId());
    }

    public void remove(String optionId) {
        if (correctOption.getId().equals(optionId)) {
            System.out.println("Warning: removed correct option.");
            correctOption = null;
        }
        options.removeIf(option -> option.getId().equals(optionId));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(description).append("\n");
        for (Option opt : options)
            sb.append(opt.getId()).append(" - ").append(opt.getDescription()).append("\n");
        return sb.toString();
    }

    @Override
    public Double answerScore(String input) {
        setCorrectOptionById(correct);
        if (input.equals(""))
            return 0.0;
        // verify if user gave an option that actually exists
        boolean exists = false;
        for (Option option : options) {
            if (option.getId().equals(input)) {
                exists = true;
                break;
            }
        }
        if (!exists)
            return null;

        double wrongPenalty = 0; // this could be an attribute (unspecified in pdf)

        if (input.equals(correctOption.getId()))
            return score;
        return -wrongPenalty;
    }

    public String getDefinition(String asId, String varId) {
        StringBuilder sb = new StringBuilder("QMC ")
        .append(varId).append(" = QMCBuilder.createQMC()")
        .append("\n        .id(\"").append(asId).append("\")");
        if (description != null)
            sb.append("\n        .description(\"").append(description).append("\")");
        if (score != null)
            sb.append("\n        .score(").append(score).append(")");
        if (difficulty != null)
            sb.append("\n        .difficulty(").append(difficulty).append(")");
        if (options != null) {
            sb.append("\n        .options(Arrays.asList(");
            for (Option opt : options) {
                sb.append("\n                new Option(\"").append(opt.getId())
                .append("\", \"").append(opt.getDescription()).append("\"),");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("\n        ))");
        }
        if (correctOption != null)
            sb.append("\n        .correctOptionId(\"").append(correctOption.getId()).append("\")");
        sb.append("\n        .build();");
        return sb.toString();
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public Option getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(Option correctOption) {
        this.correctOption = correctOption;
    }

    public void setCorrectOptionById(String optId) {
        for (Option opt : options) {
            if (opt.getId().equals(optId)) {
                correctOption = opt;
                correct = optId;
                return;
            }
        }
        System.out.printf("QMC '%s': option '%s' not found.\n", id, optId);
    }
}
