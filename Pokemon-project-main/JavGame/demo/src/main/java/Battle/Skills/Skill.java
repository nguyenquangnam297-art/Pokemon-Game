package Battle.Skills;

public class Skill {
    private String name;
    private int basePower;
    private String type;

    public Skill(String name, int basePower, String type) {
        this.name = name;
        this.basePower = basePower;
        this.type = type;
    }

    public String getName() { return name; }
    public int getBasePower() { return basePower; }
    public String getType() { return type; }
}