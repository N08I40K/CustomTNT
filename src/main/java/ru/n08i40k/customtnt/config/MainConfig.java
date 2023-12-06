package ru.n08i40k.customtnt.config;

public class MainConfig {
    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public CustomTNTEntriesMap getTntEntries() {
        return tntEntries;
    }

    public void setTntEntries(CustomTNTEntriesMap tntEntries) {
        this.tntEntries = tntEntries;
    }

    public boolean isEnableCrafts() {
        return enableCrafts;
    }

    public void setEnableCrafts(boolean enableCrafts) {
        this.enableCrafts = enableCrafts;
    }

    private String lang;
    private CustomTNTEntriesMap tntEntries;
    private boolean enableCrafts;

    public MainConfig() {
        lang = "ru-RU";

        tntEntries = new CustomTNTEntriesMap();
        tntEntries.put(new CustomTNTEntry());

        enableCrafts = true;
    }
}
