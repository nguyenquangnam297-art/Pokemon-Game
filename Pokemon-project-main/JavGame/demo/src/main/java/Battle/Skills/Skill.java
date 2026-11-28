package Battle.Skills;

public class Skill {
    private String name;
    private int basePower;
    private String type;
    private boolean isRanged;

    public Skill(String name, int basePower, String type) {
        this(name, basePower, type, false);
    }
    public Skill(String name, int basePower, String type, boolean isRanged) {
        this.name = name;
        this.basePower = basePower;
        this.type = type;
        this.isRanged = isRanged;
    }

    public String getName() {
        return name;
    }

    public int getBasePower() {
        return basePower;
    }

    public String getType() {
        return type;
    }

    public boolean isRanged() {
        return isRanged;
    }
}