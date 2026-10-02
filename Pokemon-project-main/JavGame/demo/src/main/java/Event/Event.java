package Event;

public class Event {
    private boolean active;
    private boolean completed;

    public Event() {
        active = false;
        completed = false;
    }
    public void start() {
        active = true;
        completed = false;
    }
    public void complete() {
        completed = true;
        active = false;
    }
    public boolean isActive() {
        return active;
    }
    public boolean isCompleted() {
        return completed;
    }
}
