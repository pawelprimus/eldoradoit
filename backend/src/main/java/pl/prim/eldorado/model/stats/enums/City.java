package pl.prim.eldorado.model.stats.enums;

public enum City {
    ALL("All"),
    GDANSK("Gdańsk"),
    KRAKOW("Kraków"),
    LODZ("Łódź"),
    POZNAN("Poznań"),
    SLASK("Śląsk"),
    WARSAW("Warszawa"),
    WROCLAW("Wrocław");

    private final String displayName;

    City(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
