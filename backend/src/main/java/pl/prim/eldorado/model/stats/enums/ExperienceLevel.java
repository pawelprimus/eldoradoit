package pl.prim.eldorado.model.stats.enums;


public enum ExperienceLevel {
    ALL("All"),
    JUNIOR("junior"),
    MID("mid"),
    SENIOR("senior"),
    C_LEVEL("c_level");

    private final String displayName;

    ExperienceLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
