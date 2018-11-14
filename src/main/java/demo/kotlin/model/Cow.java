package demo.kotlin.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Cow {
    private String name;
    private LocalDateTime lastCalvingDate;
    private UUID id;

    public Cow() {}

    public Cow(String name, LocalDateTime lastCalvingDate, UUID id) {
        this.name = name;
        this.lastCalvingDate = lastCalvingDate;
        if (null != id) {
            this.id = id;
        } else {
            this.id = UUID.randomUUID();
        }
    }

    public Cow(String name, LocalDateTime lastCalvingDate) {
        this(name, lastCalvingDate, null);
    }

    public Cow(String name) {
        this(name, null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getLastCalvingDate() {
        return lastCalvingDate;
    }

    public void setLastCalvingDate(LocalDateTime lastCalvingDate) {
        this.lastCalvingDate = lastCalvingDate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cow cow = (Cow) o;

        if (name != null ? !name.equals(cow.name) : cow.name != null) return false;
        if (lastCalvingDate != null ? !lastCalvingDate.equals(cow.lastCalvingDate) : cow.lastCalvingDate != null)
            return false;
        return id != null ? id.equals(cow.id) : cow.id == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (lastCalvingDate != null ? lastCalvingDate.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
