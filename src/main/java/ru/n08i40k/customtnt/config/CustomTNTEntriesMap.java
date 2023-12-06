package ru.n08i40k.customtnt.config;

import java.util.HashMap;

public class CustomTNTEntriesMap extends HashMap<String, CustomTNTEntry> {
    public CustomTNTEntry put(CustomTNTEntry value) {
        return super.put(value.getName(), value);
    }
}
