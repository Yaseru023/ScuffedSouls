package net.yaseruxd.scuffedsouls.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.yaseruxd.scuffedsouls.network.ClassSelectionPacket;
import net.yaseruxd.scuffedsouls.network.ModNetwork;
import net.yaseruxd.scuffedsouls.playerclass.PlayerClass;
import org.joml.Quaternionf;

import java.util.List;

public class ClassSelectionScreen extends Screen {

    private static final int MAX_STAT = 16;
    private static final int BAR_WIDTH = 100;
    private static final int BAR_HEIGHT = 6;

    private static final int SLOT_SIZE = 24;
    private static final int SLOT_SPACING = 4;
    private static final int SLOTS_PER_ROW = 6;

    private int selectedIndex = 0;
    private final PlayerClass[] classes = PlayerClass.values();

    public ClassSelectionScreen() {
        super(Component.literal("Choose Your Class"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Left arrow
        this.addRenderableWidget(Button.builder(
                        Component.literal("<"),
                        btn -> {
                            selectedIndex = (selectedIndex - 1 + classes.length) % classes.length;
                            rebuildWidgets();
                        })
                .pos(20, centerY - 20)
                .size(30, 40)
                .build()
        );

        // Right arrow
        this.addRenderableWidget(Button.builder(
                        Component.literal(">"),
                        btn -> {
                            selectedIndex = (selectedIndex + 1) % classes.length;
                            rebuildWidgets();
                        })
                .pos(this.width - 50, centerY - 20)
                .size(30, 40)
                .build()
        );

        // Confirm button
        this.addRenderableWidget(Button.builder(
                        Component.literal("— CHOOSE YOUR FATE —"),
                        btn -> confirmSelection())
                .pos(centerX - 80, this.height - 40)
                .size(160, 20)
                .build()
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Full screen dark overlay
        guiGraphics.fill(0, 0, this.width, this.height, 0xEE000000);

        PlayerClass current = classes[selectedIndex];
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Class name header (top center)
        guiGraphics.drawCenteredString(this.font,
                current.displayName.toUpperCase(),
                centerX, 20, 0xFFD700);

        // Divider line under title
        guiGraphics.fill(centerX - 100, 30, centerX + 100, 31, 0xFF8B6914);

        // Player model (left third)
        renderPlayerModel(guiGraphics, this.width / 4, centerY + 40, 50, mouseX, mouseY);

        // Lore text under player model
        drawWrappedString(guiGraphics, "§o" + current.lore,
                this.width / 4 - 60, centerY + 70, 120, 0x888888);

        // Stat bars (right side)
        int statX = centerX + 20;
        int statY = 80;
        int lineHeight = 16;

        guiGraphics.drawString(this.font, "§7— ATTRIBUTES —", statX, statY - 14, 0x8B6914);

        drawStatBar(guiGraphics, statX, statY,                  "STR", current.str);
        drawStatBar(guiGraphics, statX, statY + lineHeight,     "VIT", current.vit);
        drawStatBar(guiGraphics, statX, statY + lineHeight * 2, "END", current.end);
        drawStatBar(guiGraphics, statX, statY + lineHeight * 3, "INT", current.intel);
        drawStatBar(guiGraphics, statX, statY + lineHeight * 4, "MAG", current.mag);
        drawStatBar(guiGraphics, statX, statY + lineHeight * 5, "DEX", current.dex);
        drawStatBar(guiGraphics, statX, statY + lineHeight * 6, "STA", current.sta);
        drawStatBar(guiGraphics, statX, statY + lineHeight * 7, "LCK", current.lck);

        // Starting items
        int itemsY = statY + lineHeight * 8 + 16;
        guiGraphics.fill(statX, itemsY - 4, statX + 160, itemsY - 3, 0xFF8B6914);
        guiGraphics.drawString(this.font, "§6STARTING ITEMS", statX, itemsY, 0xFFD700);
        drawStartingItems(guiGraphics, current, statX, itemsY + 12, mouseX, mouseY);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void drawStatBar(GuiGraphics guiGraphics, int x, int y, String label, int value) {
        guiGraphics.drawString(this.font, label, x, y, 0xCCCCCC);

        int barX = x + 32;
        int filledWidth = (int) ((value / (float) MAX_STAT) * BAR_WIDTH);

        // Background
        guiGraphics.fill(barX, y + 1, barX + BAR_WIDTH, y + BAR_HEIGHT, 0xFF222222);
        // Fill
        guiGraphics.fill(barX, y + 1, barX + filledWidth, y + BAR_HEIGHT, 0xFFB8860B);
        // Value
        guiGraphics.drawString(this.font, String.valueOf(value),
                barX + BAR_WIDTH + 4, y, 0xFFD700);
    }

    private void drawStartingItems(GuiGraphics guiGraphics, PlayerClass current, int x, int y, int mouseX, int mouseY) {
        List<ItemStack> items = current.startingItems;
        ItemStack hovered = ItemStack.EMPTY;
        int hoveredSlotX = 0;
        int hoveredSlotY = 0;

        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (stack.isEmpty()) continue;

            int col = i % SLOTS_PER_ROW;
            int row = i / SLOTS_PER_ROW;
            int slotX = x + col * (SLOT_SIZE + SLOT_SPACING);
            int slotY = y + row * (SLOT_SIZE + SLOT_SPACING);

            // Slot background
            guiGraphics.fill(slotX, slotY, slotX + SLOT_SIZE, slotY + SLOT_SIZE, 0xFF2A2A2A);
            guiGraphics.fill(slotX, slotY, slotX + SLOT_SIZE, slotY + 1, 0xFF555555); // top border
            guiGraphics.fill(slotX, slotY, slotX + 1, slotY + SLOT_SIZE, 0xFF555555); // left border

            // Item icon (centered inside slot)
            int iconOffset = (SLOT_SIZE - 16) / 2;
            guiGraphics.renderItem(stack, slotX + iconOffset, slotY + iconOffset);
            guiGraphics.renderItemDecorations(this.font, stack, slotX + iconOffset, slotY + iconOffset);

            if (mouseX >= slotX && mouseX < slotX + SLOT_SIZE
                    && mouseY >= slotY && mouseY < slotY + SLOT_SIZE) {
                hovered = stack;
                hoveredSlotX = slotX;
                hoveredSlotY = slotY;
            }
        }

        if (!hovered.isEmpty()) {
            guiGraphics.renderTooltip(this.font, hovered, hoveredSlotX, hoveredSlotY + SLOT_SIZE);
        }
    }

    private void renderPlayerModel(GuiGraphics guiGraphics, int x, int y, int scale, int mouseX, int mouseY) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        PlayerClass current = classes[selectedIndex];
        Inventory inventory = player.getInventory();

        // Save armor
        ItemStack[] savedArmor = new ItemStack[4];
        savedArmor[0] = inventory.getArmor(0).copy();
        savedArmor[1] = inventory.getArmor(1).copy();
        savedArmor[2] = inventory.getArmor(2).copy();
        savedArmor[3] = inventory.getArmor(3).copy();

        // Save mainhand
        ItemStack savedMainhand = player.getMainHandItem().copy();

        // First item goes to mainhand preview
        if (!current.startingItems.isEmpty()) {
            inventory.setItem(inventory.selected, current.startingItems.get(0).copy());
        }

        // Apply starting gear to armor slots for preview
        for (ItemStack stack : current.startingItems) {
            if (stack.isEmpty()) continue;
            EquipmentSlot slot = Player.getEquipmentSlotForItem(stack);
            switch (slot) {
                case HEAD  -> inventory.armor.set(3, stack.copy());
                case CHEST -> inventory.armor.set(2, stack.copy());
                case LEGS  -> inventory.armor.set(1, stack.copy());
                case FEET  -> inventory.armor.set(0, stack.copy());
                default -> {}
            }
        }

        Quaternionf bodyRotation = new Quaternionf()
                .rotateY((float) Math.PI)
                .rotateZ((float) Math.PI);

        InventoryScreen.renderEntityInInventory(
                guiGraphics, x, y, scale, bodyRotation, null, player);

        // Restore armor
        inventory.armor.set(0, savedArmor[0]);
        inventory.armor.set(1, savedArmor[1]);
        inventory.armor.set(2, savedArmor[2]);
        inventory.armor.set(3, savedArmor[3]);

        // Restore mainhand
        inventory.setItem(inventory.selected, savedMainhand);
    }

    private void drawWrappedString(GuiGraphics guiGraphics, String text, int x, int y, int maxWidth, int color) {
        List<FormattedCharSequence> lines = this.font.split(Component.literal(text), maxWidth);
        int lineY = y;

        for (FormattedCharSequence line : lines) {
            guiGraphics.drawString(this.font, line, x, lineY, color, false);
            lineY += 10;
        }
    }

    private void confirmSelection() {
        ModNetwork.CHANNEL.sendToServer(new ClassSelectionPacket(this.classes[this.selectedIndex]));
        this.onClose();
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(null);
    }
}