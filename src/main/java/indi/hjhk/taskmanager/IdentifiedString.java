package indi.hjhk.taskmanager;

public class IdentifiedString {
    public int id;
    public String str;

    public IdentifiedString(int id, String str) {
        this.id = id;
        this.str = str;
    }

    @Override
    public String toString() {
        return str;
    }
}
