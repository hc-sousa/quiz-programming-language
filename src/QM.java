
import java.util.*;

public class QM extends Question {
    public List<Option> leftOptions, rightOptions;
    public Map<Option, Option> matches = new HashMap<>(); // maps left options to right options
    public List<Match> match;

    public QM() {
        type = "qm";
    }

    public void shuffle() {
        Collections.shuffle(leftOptions);
        Collections.shuffle(rightOptions);
    }

    public void remove(Option option) {
        leftOptions.remove(option);
        rightOptions.remove(option);
        matches.remove(option);
        for (Option opt : matches.keySet())
            if (matches.get(opt).equals(option))
                matches.remove(opt);
    }

    public void remove(String optionId) {
        leftOptions.removeIf(option -> option.getId().equals(optionId));
        rightOptions.removeIf(option -> option.getId().equals(optionId));
        matches.remove(getOptionById(optionId));
        for (Option opt : matches.keySet())
            if (matches.get(opt).getId().equals(optionId))
                matches.remove(opt);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(description).append("\n");
        int leftSize = leftOptions.size(), rightSize = rightOptions.size();
        int minSize = Math.min(leftSize, rightSize);
        Option left, right;
        for (int i = 0; i < minSize; i++) {
            left = leftOptions.get(i);
            right = rightOptions.get(i);
            sb.append(String.format(
                    "%-5s%-100s%5s%-5s%-100s\n",
                    left.getId(), left.getDescription(), "",
                    right.getId(), right.getDescription()
            ));
        }
        if (leftSize > rightSize) {
            for (int i = minSize; i < leftSize; i++) {
                left = leftOptions.get(i);
                sb.append(String.format(
                        "%-5s%-100s%-110s\n",
                        left.getId(), left.getDescription(), ""
                ));
            }
        }
        else if (rightSize > leftSize) {
            for (int i = minSize; i < rightSize; i++) {
                right = rightOptions.get(i);
                sb.append(String.format(
                        "%-110s%-5s%-100s\n", "",
                        right.getId(), right.getDescription()
                ));
            }
        }
        return sb.toString();
    }

    @Override
    public Double answerScore(String input) {
        setMatchesById(match);
        if (input.equals(""))
            return 0.0;
        Double answerScore = 0.0;
        String[] inputMatches = input.split(",");
        String[] matchSplit;
        Option left, right;
        int correctMatches = 0;
        int wrongMatches = 0;
        if (input.indexOf("-") >= 0) {
            for (String match : inputMatches) {
                matchSplit = match.split("-");
                left = getOptionById(matchSplit[0]);
                right = getOptionById(matchSplit[1]);
                if (!matches.containsKey(left) || !matches.get(left).equals(right))
                    wrongMatches++;
                else
                    correctMatches++;
            }
        } else
            return 0.0;

        int qmMatches = matches.keySet().size();
        int blankMatches = qmMatches - correctMatches - wrongMatches;
        double wrongPenalty = 0; // this could be an attribute, unspecified in pdf
        Double matchScore = score / qmMatches; // score of a correct match
        answerScore += correctMatches * matchScore;
        answerScore -= wrongMatches * matchScore * wrongPenalty;
        boolean allowNegativeScores = true; // could be an attribute
        if (!allowNegativeScores)
            answerScore = Math.max(score, 0);
        return answerScore; // probably don't want negative scores
    }

    public String getDefinition(String asId, String varId) {
        StringBuilder sb = new StringBuilder("QM ")
        .append(varId).append(" = QMBuilder.createQM()")
        .append("\n        .id(\"").append(asId).append("\")");;
        if (description != null)
            sb.append("\n        .description(\"").append(description).append("\")");
        if (score != null)
            sb.append("\n        .score(").append(score).append(")");
        if (difficulty != null)
            sb.append("\n        .difficulty(").append(difficulty).append(")");
        if (leftOptions != null) {
            sb.append("\n        .leftOptions(Arrays.asList(");
            for (Option opt : leftOptions) {
                sb.append("\n                new Option(\"").append(opt.getId())
                .append("\", \"").append(opt.getDescription()).append("\"),");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("\n        ))");
        }
        if (rightOptions != null) {
            sb.append("\n        .rightOptions(Arrays.asList(");
            for (Option opt : rightOptions) {
                sb.append("\n                new Option(\"").append(opt.getId())
                .append("\", \"").append(opt.getDescription()).append("\"),");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("\n        ))");
        }
        if (matches != null) {
            sb.append("\n        .matchesById(Arrays.asList(");
            for (Option opt : matches.keySet()) {
                String optId = opt.getId();
                sb.append("\n                new Match(\"").append(optId)
                .append("\", \"").append(matches.get(getOptionById(optId)).getId()).append("\"),");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("\n        ))");
        }
        sb.append("\n        .build();");
        return sb.toString();
    }

    public List<Option> getLeftOptions() {
        return leftOptions;
    }

    public void setLeftOptions(List<Option> leftOptions) {
        this.leftOptions = leftOptions;
    }

    public List<Option> getRightOptions() {
        return rightOptions;
    }

    public void setRightOptions(List<Option> rightOptions) {
        this.rightOptions = rightOptions;
    }

    public Map<Option, Option> getMatches() {
        return matches;
    }

    public void setMatches(Map<Option, Option> matches) {
        this.matches = matches;
    }

    public void setMatchesById(List<Match> matchList) {
        for (Match m : matchList) {
            matches.put(getOptionById(m.getLeftOptionId()), getOptionById(m.getRightOptionId()));
        }
        match = matchList;
    }

    public void setMatchesById(Map<String, String> matchesId) {
        String rightId;
        for (String leftId : matchesId.keySet()) {
            rightId = matchesId.get(leftId);
            matches.put(getOptionById(leftId), getOptionById(rightId));
        }
    }

    private Option getOptionById(String id) {
        for (Option option : leftOptions)
            if (option.getId().equals(id))
                return option;
        for (Option option : rightOptions)
            if (option.getId().equals(id))
                return option;
        return null;
    }
}
