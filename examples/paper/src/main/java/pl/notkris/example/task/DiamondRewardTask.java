package pl.notkris.example.task;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pl.notkris.example.loader.TaskLoader;

@TaskLoader.RegisteredTask(
        async = false,
        period = 300 * 20L
)
public class DiamondRewardTask implements Runnable {

    @Override
    public void run() {
        ItemStack diamond = new ItemStack(Material.DIAMOND);

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.getInventory().addItem(diamond);
            player.sendMessage("You got a diamond!");
        });
    }
}