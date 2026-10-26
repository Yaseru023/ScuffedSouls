package net.yaseruxd.scuffedsouls.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.yaseruxd.scuffedsouls.network.ClassSelectionPacket;
import net.yaseruxd.scuffedsouls.network.ModNetwork;
import net.yaseruxd.scuffedsouls.playerclass.PlayerClass;
import org.joml.Quaternionf;

public class ClassSelectionScreen extends Screen {

    // ── Layout constants ──────────────────────────────────────────────────
    private static final int BG_COLOR      = 0xFF171717; // charcoal background
    private static final int PANEL_COLOR   = 0xFF202020; // dark stone panels
    private static final int BORDER_COLOR  = 0xFF705735; // weathered bronze
    private static final int SCROLL_COLOR  = 0xFF29251F; // muted stone
    private static final int SEL_COLOR     = 0xFF49351F; // worn bronze highlight
    private static final int GOLD          = 0xFFE4B94F; // antique gold
    private static final int CREAM         = 0xFFD0C8B8; // aged parchment
    private static final int DIM           = 0xFF928B80; // muted gray text
    private static final int STAT_BAR_BG   = 0xFF171717;
    private static final int STAT_BAR_FILL = 0xFF96743B;
    private static final int MAX_STAT      = 16;


    // Stat icon colors (DS1-style colored squares as stand-ins)
    private static final int[] STAT_COLORS = {
            0xFFCC4444, // STR - red
            0xFF44CC44, // VIT - green
            0xFF4444CC, // END - blue
            0xFF44CCCC, // INT - cyan
            0xFFCC44CC, // MAG - purple
            0xFFCCCC44, // DEX - yellow
            0xFF44CCAA, // STA - teal
            0xFFCCAA44, // LCK - orange
    };

    private static final String[] STAT_LABELS = { "Strength","Vitality","Endurance","Intelligence","Magic","Dexterity","Stamina","Luck" };
    private static final String[] STAT_KEYS   = { "STR","VIT","END","INT","MAG","DEX","STA","LCK" };

    private final PlayerClass[] classes = PlayerClass.values();
    private int selectedIndex = 0;

    // Scroll panel item list — visible window into class list
    private static final int LIST_VISIBLE = 10;
    private int scrollOffset = 0;

    public ClassSelectionScreen() {
        super(Component.literal("Create Character"));
    }

    @Override public boolean isPauseScreen() { return false; }

    @Override
    protected void init() {
        int w = this.width, h = this.height;

        // Confirm button at bottom center, DS1-style minimal label
        this.addRenderableWidget(Button.builder(
                        Component.literal("SELECT"),
                        btn -> confirmSelection())
                .pos(w / 2 - 40, h - 28).size(80, 16).build());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Arrow keys to navigate class list
        if (keyCode == 264) { // DOWN
            selectedIndex = Math.min(selectedIndex + 1, classes.length - 1);
            if (selectedIndex >= scrollOffset + LIST_VISIBLE) scrollOffset++;
            return true;
        }
        if (keyCode == 265) { // UP
            selectedIndex = Math.max(selectedIndex - 1, 0);
            if (selectedIndex < scrollOffset) scrollOffset--;
            return true;
        }
        if (keyCode == 257 || keyCode == 335) { // ENTER
            confirmSelection();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Click on class list entries
        int w = this.width, h = this.height;
        int listX = w / 2 - 70, listY = h / 2 - 100;
        int entryH = 14;

        for (int i = 0; i < LIST_VISIBLE; i++) {
            int idx = scrollOffset + i;
            if (idx >= classes.length) break;
            int ey = listY + i * entryH;
            if (mouseX >= listX && mouseX <= listX + 140 && mouseY >= ey && mouseY <= ey + entryH) {
                selectedIndex = idx;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (delta < 0 && scrollOffset < classes.length - LIST_VISIBLE) scrollOffset++;
        if (delta > 0 && scrollOffset > 0) scrollOffset--;
        return true;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        int w = this.width, h = this.height;

        // ── Full screen background ─────────────────────────────────────────
        g.fill(0, 0, w, h, BG_COLOR);

        // ── Title ─────────────────────────────────────────────────────────
        g.drawCenteredString(font, "Create Character", w / 2, 10, GOLD);
        g.drawCenteredString(font, "Select class", w / 2, 20, DIM);
        hLine(g, w / 2 - 100, w / 2 + 100, 30, BORDER_COLOR);

        // ── Left panel — stats ─────────────────────────────────────────────
        int statPanelX = 20, statPanelY = 40;
        int statPanelW = w / 4 - 10, statPanelH = h - 60;
        panel(g, statPanelX, statPanelY, statPanelW, statPanelH);

        PlayerClass cur = classes[selectedIndex];
        int[] statValues = { cur.vit, cur.mag, cur.end, cur.str, cur.dex, cur.sta, cur.intel, cur.lck };

        g.drawString(font, "§r" + cur.displayName.toUpperCase(), statPanelX + 6, statPanelY + 6, GOLD);
        hLine(g, statPanelX + 4, statPanelX + statPanelW - 4, statPanelY + 16, BORDER_COLOR);

        // Level placeholder
        g.drawString(font, "Level", statPanelX + 6, statPanelY + 22, DIM);
        g.drawString(font, "1", statPanelX + statPanelW - 14, statPanelY + 22, CREAM);

        hLine(g, statPanelX + 4, statPanelX + statPanelW - 4, statPanelY + 32, BORDER_COLOR);

        for (int i = 0; i < STAT_LABELS.length; i++) {
            int sy = statPanelY + 38 + i * 14;
            // Colored icon square
            g.fill(statPanelX + 6, sy + 1, statPanelX + 12, sy + 7, STAT_COLORS[i]);
            g.drawString(font, STAT_LABELS[i], statPanelX + 16, sy, DIM);
            g.drawString(font, String.valueOf(statValues[i]),
                    statPanelX + statPanelW - 14, sy, CREAM);
        }

        // ── Center panel — class scroll list ──────────────────────────────
        int listX = w / 2 - 75, listY = h / 2 - 90;
        int listW = 150, entryH = 14;
        int listH = LIST_VISIBLE * entryH + 8;
        panel(g, listX - 4, listY - 4, listW + 8, listH + 8);

        for (int i = 0; i < LIST_VISIBLE; i++) {
            int idx = scrollOffset + i;
            if (idx >= classes.length) break;
            int ey = listY + i * entryH;
            boolean selected = idx == selectedIndex;

            if (selected) {
                g.fill(listX, ey, listX + listW, ey + entryH, SEL_COLOR);
            }
            g.drawCenteredString(font, classes[idx].displayName,
                    listX + listW / 2, ey + 3,
                    selected ? GOLD : CREAM);
        }

        // ── Description box — below class list ────────────────────────────
        int descY = listY + listH + 12;
        int descH = 48;
        panel(g, listX - 4, descY, listW + 8, descH);
        drawWrapped(g, cur.lore, listX + 4, descY + 4, listW - 8, DIM);

        // ── Right panel — character model ──────────────────────────────────
        // Right panel — character model
        int modelPanelX = w - w / 4 - 20;
        int modelPanelY = 40;
        int modelPanelW = w / 4;
        int modelPanelH = h - 60;

        panel(g, modelPanelX, modelPanelY, modelPanelW, modelPanelH);

// Center the model horizontally inside its panel
        int modelX = modelPanelX + modelPanelW / 2;
        int modelY = h / 2 + 30;

        renderPlayerModelStatic(g, modelX, modelY, 45, cur);

// Starting items below model
        int itemsY = modelPanelY + modelPanelH - 44;
        hLine(g, modelPanelX + 4, modelPanelX + modelPanelW - 4,
                itemsY - 4, BORDER_COLOR);

        drawStartingItems(g, cur, modelPanelX + 6, itemsY, mouseX, mouseY);
        super.render(g, mouseX, mouseY, partialTick);
    }

    // ── Helpers ───────────────────────────────────────────────────────────


    private void panel(GuiGraphics g, int x, int y, int w, int h) {
        // Main stone surface
        g.fill(x, y, x + w, y + h, PANEL_COLOR);

        // Subtle inner shadow
        g.fill(x + 1, y + 1, x + w - 1, y + 2, 0xFF35312A);
        g.fill(x + 1, y + 1, x + 2, y + h - 1, 0xFF35312A);

        // Aged metal frame
        hLine(g, x, x + w, y, BORDER_COLOR);
        hLine(g, x, x + w, y + h - 1, BORDER_COLOR);
        vLine(g, x, y, y + h, BORDER_COLOR);
        vLine(g, x + w - 1, y, y + h, BORDER_COLOR);

        // Dark inner edge
        hLine(g, x + 2, x + w - 2, y + 2, 0xFF100F0D);
        vLine(g, x + 2, y + 2, y + h - 2, 0xFF100F0D);
    }


    private void hLine(GuiGraphics g, int x1, int x2, int y, int color) {
        g.fill(x1, y, x2, y + 1, color);
    }

    private void vLine(GuiGraphics g, int x, int y1, int y2, int color) {
        g.fill(x, y1, x + 1, y2, color);
    }

    private void drawWrapped(GuiGraphics g, String text, int x, int y, int maxW, int color) {
        var lines = font.split(Component.literal(text), maxW);
        int ly = y;
        for (var line : lines) { g.drawString(font, line, x, ly, color); ly += 10; }
    }

    private void drawStartingItems(GuiGraphics g, PlayerClass cur, int x, int y, int mouseX, int mouseY) {

        var items = cur.startingItems;
        int slotSize = 18, spacing = 2, perRow = 4;
        ItemStack hovered = ItemStack.EMPTY;
        int hx = 0, hy = 0;

        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (stack.isEmpty()) continue;
            int col = i % perRow, row = i / perRow;
            int sx = x + col * (slotSize + spacing);
            int sy = y + row * (slotSize + spacing);

            g.fill(sx, sy, sx + slotSize, sy + slotSize, 0xFF151515);
            g.fill(sx, sy, sx + slotSize, sy + 1, 0xFF625744);
            g.fill(sx, sy, sx + 1, sy + slotSize, 0xFF625744);

            int off = (slotSize - 16) / 2;
            g.renderItem(stack, sx + off, sy + off);
            g.renderItemDecorations(font, stack, sx + off, sy + off);

            if (mouseX >= sx && mouseX < sx + slotSize && mouseY >= sy && mouseY < sy + slotSize) {
                hovered = stack; hx = sx; hy = sy;
            }
        }
        if (!hovered.isEmpty()) g.renderTooltip(font, hovered, hx, hy + slotSize);
    }


    private void renderPlayerModelStatic(GuiGraphics g, int x, int y, int scale, PlayerClass cur) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        var inv = player.getInventory();
        ItemStack sh = inv.getArmor(3).copy(), sc = inv.getArmor(2).copy(),
                sl = inv.getArmor(1).copy(), sf = inv.getArmor(0).copy(),
                sm = player.getMainHandItem().copy();

        for (ItemStack stack : cur.startingItems) {
            if (stack.isEmpty()) continue;
            var slot = player.getEquipmentSlotForItem(stack);
            switch (slot) {
                case HEAD  -> inv.armor.set(3, stack.copy());
                case CHEST -> inv.armor.set(2, stack.copy());
                case LEGS  -> inv.armor.set(1, stack.copy());
                case FEET  -> inv.armor.set(0, stack.copy());
                default    -> {}
            }
        }
        for (ItemStack stack : cur.startingItems) {
            if (stack.isEmpty()) continue;
            var slot = player.getEquipmentSlotForItem(stack);
            if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) {
                inv.setItem(inv.selected, stack.copy());
                break;
            }
        }

        Quaternionf pose = new Quaternionf()
                .rotateX((float) Math.PI)
                .rotateY(0.0F);

        InventoryScreen.renderEntityInInventory(
                g, x, y, scale, pose, null, player
        );

        inv.armor.set(3, sh); inv.armor.set(2, sc);
        inv.armor.set(1, sl); inv.armor.set(0, sf);
        inv.setItem(inv.selected, sm);
    }

    private void confirmSelection() {
        ModNetwork.CHANNEL.sendToServer(new ClassSelectionPacket(classes[selectedIndex]));
        this.onClose();
    }

    @Override
    public void onClose() { Minecraft.getInstance().setScreen(null); }
}