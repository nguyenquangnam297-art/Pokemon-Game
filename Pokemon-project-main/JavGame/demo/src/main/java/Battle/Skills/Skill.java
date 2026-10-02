package Battle.Skills;

public class Skill {
    private String name;
    private int power;
    private int accuracy;
    private String description;

    public Skill(String name, int power, int accuracy, String description) {
        this.name = name;
        this.power = power;
        this.accuracy = accuracy;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public int getPower() {
        return power;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public String getDescription() {
        return description;
    }
}
