package pl.utp.pss.model;

public enum Status {
    NOT_ACCEPTED ("Not accepted"),
    ACCEPTED ("Accepted"),
    REQUEST_FROM_NOT_ACCEPTED_TO_ACCEPTED("Request for acceptance"),
    REQUEST_FROM_ACCEPTED_TO_NOT_ACCEPTED("Request for not acceptance");

    private final String name;

    private Status(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
