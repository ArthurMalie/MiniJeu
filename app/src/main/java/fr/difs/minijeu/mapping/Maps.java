package fr.difs.minijeu.mapping;

public class Maps {

    private Map[] maps;

    public Maps(Map[] maps) {
        this.maps = maps;
    }

    public Map[] getMaps() {
        return maps;
    }

    public Map geetMap(int i) {
        return maps[i];
    }

}
