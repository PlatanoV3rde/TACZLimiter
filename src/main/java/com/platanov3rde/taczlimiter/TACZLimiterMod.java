@Mod("taczlimiter")
public class TACZLimiterMod {

    private static List<String> disabledWorlds = new ArrayList<>();

    public TACZLimiterMod() {
        MinecraftForge.EVENT_BUS.register(this);
        loadConfig();
    }

    private void loadConfig() {
        File configFile = new File(FMLPaths.CONFIGDIR.get().toFile(), "tacz_limiter_config.yml");

        if (!configFile.exists()) {
            try (PrintWriter writer = new PrintWriter(configFile)) {
                writer.println("disabled_worlds:");
                writer.println("  - world");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (InputStream input = new FileInputStream(configFile)) {
            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(input);
            disabledWorlds = (List<String>) config.get("disabled_worlds");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        String worldName = player.level().dimension().location().getPath();

        if (disabledWorlds.contains(worldName)) {
            ItemStack heldItem = player.getMainHandItem();
            if (heldItem.getItem() instanceof FirearmItem) {
                player.sendSystemMessage(Component.literal("§cNo puedes usar armas en este mundo."));
                event.setCanceled(true);

                // Opcional: soltar arma en el suelo
                // player.drop(heldItem.copy(), false);
                // heldItem.shrink(1);
            }
        }
    }
}
