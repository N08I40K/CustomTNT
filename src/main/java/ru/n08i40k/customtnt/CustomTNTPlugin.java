package ru.n08i40k.customtnt;

import com.google.common.base.Preconditions;
import lombok.Getter;
import meteordevelopment.orbit.IEventBus;
import org.bukkit.command.Command;
import org.bukkit.plugin.java.JavaPlugin;
import ru.n08i40k.customtnt.commands.MainCommand;
import ru.n08i40k.customtnt.config.CustomTNTEntry;
import ru.n08i40k.customtnt.config.CustomTNTEntryAccessor;
import ru.n08i40k.customtnt.config.MainConfig;
import ru.n08i40k.customtnt.config.ReplaceBlocksEntryAccessor;
import ru.n08i40k.customtnt.events.EventBusManager;
import ru.n08i40k.npluginapi.NPluginApi;
import ru.n08i40k.npluginapi.craft.NCraftRecipe;
import ru.n08i40k.npluginapi.plugin.NPlugin;
import ru.n08i40k.npluginconfig.Config;
import ru.n08i40k.npluginlocale.Locale;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class CustomTNTPlugin extends JavaPlugin {
    public static final String PLUGIN_NAME = "CustomTNT";
    public static final String PLUGIN_NAME_LOWER = "custom-tnt";

    @Getter
    private static CustomTNTPlugin instance;

    @Getter
    private Config<MainConfig> mainConfig;

    private final List<Command> commands = new ArrayList<>();

    @Getter
    private NPlugin nPlugin;

    @Override
    public void onLoad() {
        instance = this;

        IEventBus bus = EventBusManager.initEventBus();

        bus.subscribe(CustomTNTEntryAccessor.class);
        bus.subscribe(ReplaceBlocksEntryAccessor.class);

        nPlugin = NPluginApi.getInstance().getNPluginManager().registerNPlugin(this);
    }

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            if (!getDataFolder().mkdir()) {
                getSLF4JLogger().error("Cannot create plugin data dir!");
                disable();
            }
        }

        // Load configs

        Set<String> mainConfigTags = Set.of(
                CustomTNTEntry.class.getName(),
                NCraftRecipe.class.getName()
        );

        mainConfig = new Config<>(this, EventBusManager.getEventBus(), "main", MainConfig.class, mainConfigTags);
        Preconditions.checkState(mainConfig.isLoaded(),
                "Config is not loaded!");
        // Load locale file

        new Locale(this, EventBusManager.getEventBus(), mainConfig.getData().getLang());

        // Register events handler

        this.getServer().getPluginManager().registerEvents(new EventsListener(), this);
        // Register commands

        commands.add(new MainCommand());

        getServer().getCommandMap().registerAll(PLUGIN_NAME_LOWER, commands);
    }

    public MainConfig getMainConfigData() {
        return mainConfig.getData();
    }

    public void disable() {
        getPluginLoader().disablePlugin(this);
    }
}
