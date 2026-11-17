package com.example.aimassist;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.function.DoubleConsumer;

/** Menu setting. Buka dengan Right Shift. */
public class AimSettingsScreen extends Screen {

    public AimSettingsScreen() {
        super(Text.literal("AimAssist Settings"));
    }

    @Override
    protected void init() {
        int w = 200, h = 20, x = this.width / 2 - w / 2;
        int y = this.height / 2 - 95;

        addDrawableChild(ButtonWidget.builder(enabledText(), b -> {
            AimConfig.enabled = !AimConfig.enabled;
            b.setMessage(enabledText());
        }).dimensions(x, y, w, h).build());

        addDrawableChild(ButtonWidget.builder(targetText(), b -> {
            AimConfig.targetHead = !AimConfig.targetHead;
            b.setMessage(targetText());
        }).dimensions(x, y + 26, w, h).build());

        addDrawableChild(new ConfigSlider(x, y + 52, w, h, "FOV", 5, 90,
                AimConfig.fov, v -> AimConfig.fov = v));

        addDrawableChild(new ConfigSlider(x, y + 78, w, h, "Smooth", 0.05, 1.0,
                AimConfig.smooth, v -> AimConfig.smooth = v));

        addDrawableChild(new ConfigSlider(x, y + 104, w, h, "Range", 2, 6,
                AimConfig.range, v -> AimConfig.range = v));

        addDrawableChild(ButtonWidget.builder(weaponText(), b -> {
            AimConfig.onlyWithWeapon = !AimConfig.onlyWithWeapon;
            b.setMessage(weaponText());
        }).dimensions(x, y + 130, w, h).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Selesai"), b -> close())
                .dimensions(x, y + 164, w, h).build());
    }

    private static Text enabledText() {
        return Text.literal("AimAssist: " + (AimConfig.enabled ? "ON" : "OFF"));
    }

    private static Text weaponText() {
        return Text.literal("Cuma pegang " + AimConfig.weaponName + ": "
                + (AimConfig.onlyWithWeapon ? "ON" : "OFF"));
    }

    private static Text targetText() {
        return Text.literal("Target: " + (AimConfig.targetHead ? "Kepala" : "Badan"));
    }

    @Override
    public void close() {
        AimConfig.save();
        super.close();
    }

    /** Slider sederhana yang nulis langsung ke AimConfig. */
    private static class ConfigSlider extends SliderWidget {
        private final String label;
        private final double min, max;
        private final DoubleConsumer setter;

        ConfigSlider(int x, int y, int w, int h, String label,
                     double min, double max, double current, DoubleConsumer setter) {
            super(x, y, w, h, Text.empty(), (current - min) / (max - min));
            this.label = label;
            this.min = min;
            this.max = max;
            this.setter = setter;
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Text.literal(label + ": " + String.format("%.2f", min + value * (max - min))));
        }

        @Override
        protected void applyValue() {
            setter.accept(min + value * (max - min));
        }
    }
}
