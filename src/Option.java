
import java.util.Objects;

public class Option {
    public String id;
    public String description;

    public Option(String id, String description) {
        this.id = id;
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("%-5s%-100s", id, description);
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Option option = (Option) o;
        return Objects.equals(id, option.id) && Objects.equals(description, option.description);
    }
}
