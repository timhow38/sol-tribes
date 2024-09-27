package me.thepond.soltribes.screen;

import me.thepond.soltribes.SOLTribes;
import me.thepond.soltribes.SOLTribesClient;
import me.thepond.soltribes.registry.ModPackets;
import me.thepond.soltribes.screen.widget.SimpleButtonWidget;
import me.thepond.soltribes.screen.widget.TribeInfoWidget;
import me.thepond.soltribes.tribe.Tribe;
import me.thepond.soltribes.tribe.TribeMember;
import me.thepond.soltribes.tribe.TribeSettlement;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TribeInformationScreen extends Screen {

    private TribeInfoWidget tribeInfoWidget;
    private SimpleButtonWidget leaveButton;
    private SimpleButtonWidget joinDiscordButton;

    private TribeMemberList tribeMemberList;
    private SettlementList settlementList;

    private boolean confirmLeave;

    public TribeInformationScreen() {
        super(Text.literal("Tribe Information Screen"));
    }

    @Override
    protected void init() {
        super.init();

        if (SOLTribesClient.getCurrentTribe() == null) {
            this.client.setScreen(null);
            return;
        }

        int backgroundWidth = 176;
        int backgroundHeight = 166;

        this.joinDiscordButton = new SimpleButtonWidget(this.width / 2 + backgroundWidth / 2 - 10 - 75, this.height / 2 - backgroundHeight / 2 - 14, 80, 15, Text.literal("Join Discord"), button -> {
            if (!SOLTribesClient.getCurrentTribe().getTribeDiscordUrl().isBlank()) {
                // Check if string is a valid URL
                URL url;
                try {
                    url = new URL(SOLTribesClient.getCurrentTribe().getTribeDiscordUrl());
                    url.toURI();
                    this.client.setScreen(new ConfirmLinkScreen(t -> {
                        if (t) {
                            Util.getOperatingSystem().open(url);
                        }
                        this.client.setScreen(this);
                    }, SOLTribesClient.getCurrentTribe().getTribeDiscordUrl(), false));
                } catch (Exception e) {
                    SOLTribes.LOGGER.error("Invalid URL: " + SOLTribesClient.getCurrentTribe().getTribeDiscordUrl());
                }
            }
        }, 0xFFe4e4e4, 0xFF323232);

        this.leaveButton = new SimpleButtonWidget(this.width / 2 + backgroundWidth / 2 - 10 - 75, this.height / 2 + backgroundHeight / 2 - 1, 80, 15, Text.literal("Leave"), button -> {
            if (confirmLeave) {
                confirmLeave = false;
                ClientPlayNetworking.send(ModPackets.LEAVE_TRIBE, PacketByteBufs.create());
                this.client.setScreen(null);
            } else {
                confirmLeave = true;
                this.leaveButton.setMessage(Text.literal("Are you sure?"));
            }
        }, 0xFF4c1516, 0xFFa90003);

        this.addSelectableChild(this.leaveButton);
        this.addSelectableChild(this.joinDiscordButton);

        int x = this.width / 2 - 176 / 2;
        int y = this.height / 2;
        tribeInfoWidget = new TribeInfoWidget(
                x - 30,
                y - 20,
                30,
                40,
                true
        );
        this.addSelectableChild(this.tribeInfoWidget);

        this.tribeMemberList = new TribeMemberList(this.width / 2 - backgroundWidth / 2 + 10, this.height / 2 - backgroundHeight / 2 + 50, backgroundWidth - 20, 45, Text.empty(), textRenderer);
        this.addDrawableChild(this.tribeMemberList);

        this.settlementList = new SettlementList(this.width / 2 - backgroundWidth / 2 + 10, this.height / 2 - backgroundHeight / 2 + 110, backgroundWidth - 20, 45, Text.empty(), textRenderer);
        this.addDrawableChild(this.settlementList);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        tribeInfoWidget.render(context, mouseX, mouseY, delta);
        leaveButton.render(context, mouseX, mouseY, delta);
        joinDiscordButton.render(context, mouseX, mouseY, delta);

        int backgroundWidth = 176;
        int backgroundHeight = 166;
        ScreenUtils.drawCenteredRect(context, this.width / 2, this.height / 2, backgroundWidth, backgroundHeight, 0xFFbcbcbe);

        super.render(context, mouseX, mouseY, delta);

        Tribe tribe = SOLTribesClient.getCurrentTribe();
        if (tribe == null) {
            return;
        }

        ScreenUtils.drawScaledText(context, textRenderer, "\u00A7l" + tribe.getTribeName(), this.width / 2 - backgroundWidth / 2 + 10, this.height / 2 - backgroundHeight / 2 + 10, 0xFFFFFF, false, 1.5f);
        Date date = tribe.getTribeMember(client.player.getUuid()).getTribeMemberJoinDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd, yyyy");
        context.drawText(textRenderer, "Joined On: " + dateFormat.format(date), this.width / 2 - backgroundWidth / 2 + 10, this.height / 2 - backgroundHeight / 2 + 25, 0xFFFFFF, false);

        context.drawText(textRenderer, "Tribe Members", this.width / 2 - backgroundWidth / 2 + 15, this.height / 2 - backgroundHeight / 2 + 40, 0xFFFFFF, false);
        context.drawText(textRenderer, "Settlement List", this.width / 2 - backgroundWidth / 2 + 15, this.height / 2 - backgroundHeight / 2 + 100, 0xFFFFFF, false);
    }

    @Override
    public void renderBackground(DrawContext context) {
        super.renderBackground(context);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private static class TribeMemberList extends ScrollableWidget {

        private final TextRenderer textRenderer;
        private final Map<UUID, ItemStack> cachedPlayerHeads = new HashMap<>();
        private TribeMember hoveredTribeMember;

        public TribeMemberList(int i, int j, int k, int l, Text text, TextRenderer textRenderer) {
            super(i, j, k, l, text);
            this.textRenderer = textRenderer;
        }

        @Override
        protected int getContentsHeight() {
            List<TribeMember> tribeMembers = SOLTribesClient.getCurrentTribe().getTribeMembers();
            int rows = (int) Math.ceil(tribeMembers.size() / (float)getHeadsPerRow());
            int height = rows * 30 + 3 - 16;
            return (int) Math.max(16, height);
        }

        private int getHeadsPerRow() {
            return 5;
        }

        @Override
        protected double getDeltaYPerScroll() {
            return 8;
        }

        @Override
        protected void drawBox(DrawContext context) {
            super.drawBox(context);
        }

        @Override
        protected void drawBox(DrawContext context, int x, int y, int width, int height) {
            int i = this.isFocused() ? -1 : -6250336;
            context.fill(x, y, x + width, y + height, i);
            context.fill(x + 1, y + 1, x + width - 1, y + height - 1, 0xFF939393);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            super.render(context, mouseX, mouseY, delta);
            if (isHovered() && hoveredTribeMember != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd, yyyy");
                context.drawTooltip(textRenderer, List.of(
                        Text.literal("\u00A7lMember: \u00A7r" + hoveredTribeMember.getTribeMemberName()),
                        Text.literal("\u00A7lJoined On: \u00A7r" + dateFormat.format(hoveredTribeMember.getTribeMemberJoinDate()))
                ), mouseX, mouseY);
            }
        }

        @Override
        protected void renderOverlay(DrawContext context) {
            if (this.overflows()) {
                int i = MathHelper.clamp((int)((float)(this.height * this.height) / (float)this.getContentsHeight() + 4), 32, this.height);
                int j = this.getX() + this.width - 6;
                int k = this.getX() + this.width - 6 + 5;
                int l = Math.max(this.getY(), (int)this.getScrollY() * (this.height - i) / this.getMaxScrollY() + this.getY());
                int m = l + i;
                context.fill(j, l + 2, k, m - 2, -8355712);
                context.fill(j, l + 2, k - 1, m - 1 - 2, -4144960);
            }
        }

        @Override
        public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            super.renderButton(context, mouseX, mouseY, delta);
        }

        @Override
        protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
            int index = 0;
            List<TribeMember> tribeMembers = SOLTribesClient.getCurrentTribe().getTribeMembers();
            TribeMember hoveredTribeMember = null;
            for (TribeMember tribeMember : tribeMembers) {
                ItemStack playerHead = this.createOrGetPlayerHead(tribeMember.getTribeMemberUUID(), tribeMember.getTribeMemberName());
                int x = this.getX() + getHeadsPerRow() + (index % getHeadsPerRow()) * 28;
                int y = this.getY() + getHeadsPerRow() + (index / getHeadsPerRow()) * 28;
                context.drawItem(playerHead, x, y);
                int newMouseY = (int) (mouseY + this.getScrollY());
                if (mouseX >= x && mouseX <= x + 16 && newMouseY >= y && newMouseY <= y + 16) {
                    hoveredTribeMember = tribeMember;
                }
                index++;
            }
            this.hoveredTribeMember = hoveredTribeMember;
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {

        }

        private ItemStack createOrGetPlayerHead(UUID uuid, String name) {
            if (this.cachedPlayerHeads.containsKey(uuid)) {
                return this.cachedPlayerHeads.get(uuid);
            }
            ItemStack playerHead = new ItemStack(Items.PLAYER_HEAD);
            playerHead.getOrCreateNbt().putString("SkullOwner", name);
            this.cachedPlayerHeads.put(uuid, playerHead);
            return playerHead;
        }
    }

    private static class SettlementList extends ScrollableWidget {

        private final TextRenderer textRenderer;

        public SettlementList(int i, int j, int k, int l, Text text, TextRenderer textRenderer) {
            super(i, j, k, l, text);
            this.textRenderer = textRenderer;
        }

        @Override
        protected int getContentsHeight() {
            List<TribeSettlement> tribeSettlements = new ArrayList<>(SOLTribesClient.getCurrentTribe().getTribeSettlements());
            int height = tribeSettlements.size() * 35 - 2;
            return (int) Math.max(35, height);
        }

        @Override
        protected double getDeltaYPerScroll() {
            return 8;
        }

        @Override
        protected void drawBox(DrawContext context) {
            super.drawBox(context);
        }

        @Override
        protected void drawBox(DrawContext context, int x, int y, int width, int height) {
            int i = this.isFocused() ? -1 : -6250336;
            context.fill(x, y, x + width, y + height, i);
            context.fill(x + 1, y + 1, x + width - 1, y + height - 1, 0xFF939393);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            super.render(context, mouseX, mouseY, delta);
        }

        @Override
        protected void renderOverlay(DrawContext context) {
            if (this.overflows()) {
                int i = MathHelper.clamp((int)((float)(this.height * this.height) / (float)this.getContentsHeight() + 4), 32, this.height);
                int j = this.getX() + this.width - 6;
                int k = this.getX() + this.width - 6 + 5;
                int l = Math.max(this.getY(), (int)this.getScrollY() * (this.height - i) / this.getMaxScrollY() + this.getY());
                int m = l + i;
                context.fill(j, l + 2, k, m - 2, -8355712);
                context.fill(j, l + 2, k - 1, m - 1 - 2, -4144960);
            }
        }

        @Override
        public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            super.renderButton(context, mouseX, mouseY, delta);
        }

        @Override
        protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
            List<TribeSettlement> tribeSettlements = new ArrayList<>(SOLTribesClient.getCurrentTribe().getTribeSettlements());
            for (TribeSettlement tribeSettlement : tribeSettlements) {
                int x = this.getX() + 3;
                int y = this.getY() + 3 + tribeSettlements.indexOf(tribeSettlement) * 35;
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd, yyyy");
                context.drawText(textRenderer, tribeSettlement.getTribeSettlementName(), x, y, 0xFFFFFF, false);
                ScreenUtils.drawRect(context, x, y + 10, 80, 1, 0xFFFFFFFF);
                ScreenUtils.drawScaledText(context, textRenderer, String.format("Coordinates: %s, %s, %s", tribeSettlement.getTribeSettlementPosition().getX(), tribeSettlement.getTribeSettlementPosition().getY(), tribeSettlement.getTribeSettlementPosition().getZ()), x, y + 15, 0xFFFFFF, false, 0.5f);
                ScreenUtils.drawScaledText(context, textRenderer, "Founded: " + dateFormat.format(tribeSettlement.getTribeSettlementCreationDate()), x, y + 22, 0xFFFFFF, false, 0.5f);
            }
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {

        }
    }
}
