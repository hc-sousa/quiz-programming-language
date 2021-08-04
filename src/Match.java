
public class Match {
    public String leftOptionId;
    public String rightOptionId;

    public Match(String leftOptionId, String rightOptionId) {
        this.leftOptionId = leftOptionId;
        this.rightOptionId = rightOptionId;
    }

    @Override
    public String toString() {
        return String.format("%s : %s", leftOptionId, rightOptionId);
    }

    public String getLeftOptionId() {
        return leftOptionId;
    }

    public void setLeftOptionId(String leftOptionId) {
        this.leftOptionId = leftOptionId;
    }

    public String getRightOptionId() {
        return rightOptionId;
    }

    public void setRightOptionId(String rightOptionId) {
        this.rightOptionId = rightOptionId;
    }
}
