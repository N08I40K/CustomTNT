package ru.n08i40k.customtnt.utils;

import org.slf4j.Logger;
import ru.n08i40k.customtnt.CustomTNTPlugin;
import ru.n08i40k.npluginlocale.Locale;

public class PluginUse {
    protected final CustomTNTPlugin plugin;
    protected final Locale locale;
    protected final Logger logger;

    public PluginUse() {
        plugin = CustomTNTPlugin.getInstance();
        locale = Locale.getInstance();
        logger = plugin.getSLF4JLogger();
    }
}
