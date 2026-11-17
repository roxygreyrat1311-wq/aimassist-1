package com.example.aimassist;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

/**
 * Aim assist (Fabric, Minecraft 1.21.11).
 *  R           = ON/OFF
 *  Right Shift = buka menu setting (FOV, smooth, range, target kepala/badan)
 */
public class AimAssistClient implements ClientModInitializer {

    private static KeyBinding toggleKey;
    private static KeyBinding menuKey;

    @Override
    public void onInitializeClient() {
        AimConfig.load();

        KeyBinding.Category category =
                KeyBinding.Category.create(Identifier.of("aimassist", "main"));

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.aimassist.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, category));
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.aimassist.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, category));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                AimConfig.enabled = !AimConfig.enabled;
                if (client.player != null) {
                    client.player.sendMessage(
                            Text.literal("AimAssist: " + (AimConfig.enabled ? "ON" : "OFF")), true);
                }
            }
            while (menuKey.wasPressed()) {
                client.setScreen(new AimSettingsScreen());
            }

            if (!AimConfig.enabled || client.player == null || client.world == null
                    || client.currentScreen != null) return;

            ClientPlayerEntity me = client.player;

            // Cuma jalan kalau lagi pegang item yang dipilih (default: Desert Eagle)
            if (AimConfig.onlyWithWeapon && !holdingWeapon(me)) return;

            Vec3d from = me.getEyePos();

            PlayerEntity best = null;
            float bestYaw = 0, bestPitch = 0;
            double bestAngle = AimConfig.fov;

            for (PlayerEntity p : client.world.getPlayers()) {
                if (p == me || p.isSpectator() || !p.isAlive()) continue;
                if (me.squaredDistanceTo(p) > AimConfig.range * AimConfig.range) continue;

                // Titik bidik: kepala (posisi mata) atau badan (tengah tinggi badan)
                Vec3d aim = AimConfig.targetHead
                        ? p.getEyePos()
                        : new Vec3d(p.getX(), p.getBodyY(0.5), p.getZ());

                double dx = aim.x - from.x;
                double dy = aim.y - from.y;
                double dz = aim.z - from.z;
                double horizontal = Math.sqrt(dx * dx + dz * dz);

                float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
                float pitch = (float) -Math.toDegrees(Math.atan2(dy, horizontal));

                // Jarak sudut dari crosshair ke target (FOV check)
                double dYaw = MathHelper.wrapDegrees(yaw - me.getYaw());
                double dPitch = pitch - me.getPitch();
                double angle = Math.sqrt(dYaw * dYaw + dPitch * dPitch);

                if (angle <= bestAngle) {
                    bestAngle = angle;
                    best = p;
                    bestYaw = yaw;
                    bestPitch = pitch;
                }
            }

            if (best == null) return; // nggak ada player di dalam FOV

            float s = (float) AimConfig.smooth;
            me.setYaw(me.getYaw() + MathHelper.wrapDegrees(bestYaw - me.getYaw()) * s);
            me.setPitch(MathHelper.clamp(me.getPitch() + (bestPitch - me.getPitch()) * s, -90f, 90f));
        });
    }

    /** Cek item di tangan utama: nama item harus mengandung AimConfig.weaponName. */
    private static boolean holdingWeapon(ClientPlayerEntity me) {
        ItemStack stack = me.getMainHandStack();
        if (stack.isEmpty()) return false;
        String name = stack.getName().getString().toLowerCase();
        return name.contains(AimConfig.weaponName.toLowerCase());
    }
}
