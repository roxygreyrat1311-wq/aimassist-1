package com.example.aimassist;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Pengaturan mod, disimpan otomatis di config/aimassist.properties */
public class AimConfig {

    public static boolean enabled = false;
    public static boolean targetHead = true;   // true = kepala, false = badan
    public static double fov = 30.0;           // radius sekitar crosshair (derajat)
    public static double smooth = 0.25;        // 0.05 (halus) - 1.0 (snap)
    public static double range = 6.0;          // jarak maksimal (blok)
    public static boolean onlyWithWeapon = true;      // aktif cuma pas pegang item tertentu
    public static String weaponName = "Desert Eagle"; // nama item (case-insensitive, cukup sebagian)

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("aimassist.properties");
    }

    public static void load() {
        Path f = file();
        if (!Files.exists(f)) return;
        try (Reader r = Files.newBufferedReader(f)) {
            Properties p = new Properties();
            p.load(r);
            targetHead = Boolean.parseBoolean(p.getProperty("targetHead", "true"));
            fov = Double.parseDouble(p.getProperty("fov", "30"));
            smooth = Double.parseDouble(p.getProperty("smooth", "0.25"));
            range = Double.parseDouble(p.getProperty("range", "6"));
            onlyWithWeapon = Boolean.parseBoolean(p.getProperty("onlyWithWeapon", "true"));
            weaponName = p.getProperty("weaponName", "Desert Eagle");
        } catch (IOException | NumberFormatException ignored) {
        }
    }

    public static void save() {
        Properties p = new Properties();
        p.setProperty("targetHead", String.valueOf(targetHead));
        p.setProperty("fov", String.valueOf(fov));
        p.setProperty("smooth", String.valueOf(smooth));
        p.setProperty("range", String.valueOf(range));
        p.setProperty("onlyWithWeapon", String.valueOf(onlyWithWeapon));
        p.setProperty("weaponName", weaponName);
        try (Writer w = Files.newBufferedWriter(file())) {
            p.store(w, "AimAssist config");
        } catch (IOException ignored) {
        }
    }
}
