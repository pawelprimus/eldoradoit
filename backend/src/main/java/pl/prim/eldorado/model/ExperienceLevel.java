package pl.prim.eldorado.model;


public enum ExperienceLevel {
    ALL("All"),
    JUNIOR("junior"),
    MID("mid"),
    SENIOR("senior"),
    C_LEVEL("c-level");

    private final String displayName;

    ExperienceLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
