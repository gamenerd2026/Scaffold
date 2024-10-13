package com.mineplex.studio.scaffold;

import com.mineplex.studio.sdk.modules.MineplexModuleManager;
import com.mineplex.studio.sdk.modules.game.*;
import com.mineplex.studio.sdk.modules.game.helper.GameStateTracker;
import com.mineplex.studio.sdk.modules.game.helper.PlayerStateTracker;
import com.mineplex.studio.sdk.modules.game.mechanics.GameWorldSelectorMechanic;
import com.mineplex.studio.sdk.modules.game.mechanics.helper.GameStateListenerHelperMechanic;
import com.mineplex.studio.sdk.modules.game.mechanics.spectator.SpectatorLocationHandler;
import com.mineplex.studio.sdk.modules.game.mechanics.spectator.SpectatorMechanic;
import com.mineplex.studio.sdk.modules.game.mechanics.spectator.SpectatorStateHandler;
import com.mineplex.studio.sdk.modules.world.MineplexWorld;
import com.mineplex.studio.sdk.util.selector.BuiltInGameStateSelector;
import com.mineplex.studio.scaffold.Pre_StartPlayerJoinListener;
import com.mineplex.studio.scaffold.StartedPlayerJoinListener;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter

@RequiredArgsConstructor
@Slf4j
public class OneBlockWars implements SingleWorldMineplexGame {
    private final JavaPlugin plugin;
    private BukkitRunnable blockGiver; //test


    private final MineplexGameMechanicFactory gameMechanicFactory =
            MineplexModuleManager.getRegisteredModule(MineplexGameMechanicFactory.class);

    private GameStateListenerHelperMechanic<OneBlockWars> stateHelperMechanic;

    private SpectatorMechanic spectatorMechanic;

    private GameWorldSelectorMechanic gameWorldSelectorMechanic;

    @Delegate
    private final PlayerStateTracker playerStateTracker =
            new PlayerStateTracker(this, BuiltInPlayerState.SPECTATOR);

    @Delegate
    private final GameStateTracker gameStateTracker =
            new GameStateTracker(this, BuiltInGameState.PREPARING);

    private final int MIN_PLAYERS = 3;

    private final int MAX_PLAYERS = 4;
    private boolean isCountdownActive = false;
    private Map<Player, Location> playerStartLocations = new HashMap<>();

    ///Called when GameState is set to a state that isReady()
    private void onPreStart() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            setPlayerState(player, BuiltInPlayerState.ALIVE);
        }
        tryStart(false);
    }

    ///Called when GameState is set to a state that isInProgress()
    private void onStart() {
        ArrayList<Location> spawns = new ArrayList<>(getGameWorld().getDataPoints("SPAWN"));

        getPlayerStates().keySet().forEach(player -> {
            cleanupPlayer(player);

            Location spawn = getLocationAwayFromOtherLocations(spawns, getPlayerStates().keySet());
            adjustSpawnLocation(spawn, getWorldCenter());
            player.teleport(spawn, PlayerTeleportEvent.TeleportCause.PLUGIN);

            player.setGameMode(GameMode.ADVENTURE); // Set to ADVENTURE mode to prevent block breaking
            playerStartLocations.put(player, spawn.clone());
        });

        isCountdownActive = true;

        // Register the movement cancellation listener
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerMove(PlayerMoveEvent event) {
                if (isCountdownActive && playerStartLocations.containsKey(event.getPlayer())) {
                    Location from = event.getFrom();
                    Location to = event.getTo();
                    if (from.getX() != to.getX() || from.getZ() != to.getZ()) {
                        event.setTo(from);
                    }
                }
            }
        }, plugin);


        // Start countdown
        new BukkitRunnable() {
            int countdown = 5; // 5 second countdown

            @Override
            public void run() {
                if (countdown > 0) {
                    Bukkit.broadcastMessage("Game starts in " + countdown + " seconds!");
                    countdown--;
                } else {
                    Bukkit.broadcastMessage("Game start!");
                    isCountdownActive = false;
                    startGame();
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void startGame() {
        for (Player player : getPlayerStates().keySet()) {
            player.setGameMode(GameMode.SURVIVAL);
        }

        playerStartLocations.clear(); // Clear the stored locations

        // Activate AutoBlockGiver
        Bukkit.broadcastMessage("Starting BlockGiver");
        blockGiver = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.getInventory().addItem(new ItemStack(Material.WHITE_WOOL));
                }
            }
        };
        blockGiver.runTaskTimer(plugin, 0L, 20L);

        log.info("Game started");
    }

    private Location getLocationAwayFromOtherLocations(Iterable<Location> locations, Iterable<Player> players) {
        Location bestLocation = null;
        double bestDist = Double.MIN_VALUE;

        for (Location location : locations) {
            double closest = Double.MAX_VALUE;

            for (Player player : players) {
                if (!player.getWorld().equals(location.getWorld())) {
                    continue;
                }

                double distanceSquared = player.getLocation().distanceSquared(location);
                if (closest >= distanceSquared) {
                    closest = distanceSquared;
                }
            }

            if (closest > bestDist) {
                bestLocation = location;
                bestDist = closest;
            }
        }

        return bestLocation;
    }

    private Location getWorldCenter() {
        MineplexWorld world = getGameWorld();
        List<Location> locations = world.getDataPoints("CENTER");
        if (locations.isEmpty()) {
            return new Vector(0.5, 0, 0.5).toLocation(world.getMinecraftWorld());
        }
        return locations.getFirst();
    }

    private void adjustSpawnLocation(Location location, Location center) {
        location.add(0.5, 0, 0.5);

        location.setDirection(
                center.toVector().setY(0).subtract(
                        location.toVector().setY(0)).normalize());
    }

    //Called when GameState is set to a state that isEnded()
    private void onEnded() {
        MineplexModuleManager.getRegisteredModule(
                MineplexGameModule.class).startNextGame();

    }



    @Override
    public MineplexWorld getGameWorld() {
        return gameWorldSelectorMechanic.getSelectedGameWorld();
    }

    public List<Player> getAlivePlayers() {
        return getPlayerStates().entrySet().stream()
                .filter(entry -> entry.getValue().isAlive())
                .map(Map.Entry::getKey)
                .toList();
    }

    @Override
    public @NotNull String getName() {
        return "One Block Wars";
    }

    @Override
    public void setup() {
        log.info("attemped to setup");
        stateHelperMechanic =
                gameMechanicFactory.construct(GameStateListenerHelperMechanic.class);
        stateHelperMechanic
                .registerRunnable(this::onPreStart,
                        BuiltInGameStateSelector.ready())
                .registerSingleRunnable(this::onStart,
                        BuiltInGameStateSelector.inProgress())
                .registerSingleRunnable(this::onEnded,
                        BuiltInGameStateSelector.ended())
                .registerEventListener(
                        new com.mineplex.studio.scaffold.Pre_StartPlayerJoinListener(), //used to be (this)
                        BuiltInGameStateSelector.ready())
                .registerEventListener(
                        new PlayerDeathListener(this),
                        BuiltInGameStateSelector.inProgress())
                .registerEventListener(
                        new StartedPlayerJoinListener(), //used to be (this)
                        BuiltInGameStateSelector.inProgress())
                .registerEventListener(
                        new PlayerQuitListener(this),
                        BuiltInGameStateSelector.inProgress())
                .registerEventListener(
                        new PlayerStateChangeListener(this),
                        BuiltInGameStateSelector.inProgress());


        spectatorMechanic = gameMechanicFactory.construct(SpectatorMechanic.class);
        spectatorMechanic
                .getSettings()
                .setDeathOut(true)
                .setDropItemsOnDeath(true)
                .setDropItemsOnDisconnect(true);
        spectatorMechanic.setStateHandler(new SpectatorStateHandler() {
            @Override
            public void onSpectatorAdd(@NonNull Player player, boolean teleport, boolean out) {
                OneBlockWars.this.setPlayerState(player, BuiltInPlayerState.ELIMINATED);
            }

            @Override
            public void onPlayerRespawn(@NonNull Player player) {
                OneBlockWars.this.setPlayerState(player, BuiltInPlayerState.ALIVE);
            }
        });
        spectatorMechanic.setLocationHandler(new SpectatorLocationHandler() {
            @Override
            public Location getSpectatorLocation(@NonNull Player player) {
                return getWorldCenter().add(0.0, 10.0, 0.0);
            }

            @Override
            public boolean shouldTeleport(@NonNull Player player) {
                if(OneBlockWars.this.getGameState().isInProgress()) {
                    return false;
                }
                return true;
            }
        });
        spectatorMechanic.setup(this);

        gameWorldSelectorMechanic = this.gameMechanicFactory.construct(GameWorldSelectorMechanic.class);

        gameWorldSelectorMechanic.setFilter(name -> !"lobby".equalsIgnoreCase(name));
        gameWorldSelectorMechanic.setup(this);

        stateHelperMechanic.setup(this);

        setGameState(BuiltInGameState.PRE_START);
    }
    public void stopBlockGiver() {
        if (blockGiver != null) {
            blockGiver.cancel();
            Bukkit.broadcastMessage("Stopped BlockGiver");
        }
    }
    @Override
    public void teardown() {
        stateHelperMechanic.teardown();
        gameWorldSelectorMechanic.teardown();
        spectatorMechanic.teardown();
//        stopBlockGiver();

        for (Player player : getPlayerStates().keySet()) {
            cleanupPlayer(player);
        }
    }

    public void cleanupPlayer(Player player) {
        player.getInventory().clear();
        player.closeInventory(InventoryCloseEvent.Reason.DEATH);
        player.setExp(0);
        player.setFireTicks(0);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.clearActivePotionEffects();
        player.clearActiveItem();
        player.setFallDistance(0);
        player.setInvulnerable(false);
        stopBlockGiver();
    }

    public boolean tryStart(boolean force) {
        if(force || (getAlivePlayers().size() >= MIN_PLAYERS && getAlivePlayers().size() <= MAX_PLAYERS)) {
            Bukkit.getServer().sendMessage(
                    Component.text("Starting Game!")
                            .color(TextColor.color(38, 213, 47))
                            .decorate(TextDecoration.BOLD));

            setGameState(BuiltInGameState.STARTED);
            return true;
        }
        return false;

    }

    /**
     * Gets the {@link MineplexWorld} this {@link MineplexGame} is located within.
     *
     * @return the {@link MineplexWorld} this {@link MineplexGame} is located within
     */

}



//[01:55:06 ERROR]: [ModernPluginLoadingStrategy] Could not load plugin 'plugin.jar' in folder 'plugins'
//java.lang.ClassNotFoundException: com.mineplex.studio.scaffold.Scaffold
