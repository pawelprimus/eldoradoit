package pl.prim.eldorado.model.stats.enums;

public enum Technology {
    ADMIN("13"), // 13
    ALL("All"), // -
    ANALYTICS("17"), // 17
    AI("25"), // 25
    C("9"), // 9
    DEVOPS("12"), // 12
    GAME("16"), // 16
    HTML("2"), // 2
    JAVASCRIPT("1"), // 1
    JAVA("6"), // 6
    MOBILE("10"), // 10
    NET("7"), // 7
    PHP("3"), // 3
    PM("15"), // 15
    PYTHON("5"), // 5
    RUBY("4"), // 4
    SCALA("8"), // 8
    SECURITY("18"), // 18
    TESTING("11"), // 11
    UX_UI("14"); // 14

    private final String displayName;

    Technology(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
