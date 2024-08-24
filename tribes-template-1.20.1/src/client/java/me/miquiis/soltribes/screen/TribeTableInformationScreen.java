package me.thepond.soltribes.screen;

import me.thepond.soltribes.SOLTribes;
import me.thepond.soltribes.packets.UpdateTribeC2SPacket;
import me.thepond.soltribes.registry.ModPackets;
import me.thepond.soltribes.screenhandler.TribeTableInformationScreenHandlerBlockEntity;
import me.thepond.soltribes.tribe.TribeMember;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.text.SimpleDateFormat;
import java.util.*;

public class TribeTableInformationScreen extends TribeTableScreen<TribeTableInformationScreenHandlerBlockEntity> {

    private TextFieldWidget tribeNameEditBox;
    private TextFieldWidget tribeDescriptionEditBox;
    private TextFieldWidget tribeDiscordUrlEditBox;

    private TribeMemberList tribeMemberList;
    private boolean isDirty;
    private boolean loading;

    private boolean init;

    public TribeTableInformationScreen(TribeTableInformationScreenHandlerBlockEntity handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, Text.literal("INFORMATION"));
    }

    @Override
    protected void init() {
        super.init();

        this.init = true;

        SOLTribes.LOGGER.info("Initting Table Information Screen");

        this.isDirty = false;
        this.loading = this.handler.blockEntity.getTribe().isTemp();

        this.tribeNameEditBox = new TextFieldWidget(textRenderer, this.width / 2 - TRIBE_TABLE_WIDTH / 2 + TRIBE_TABLE_PADDING, this.height / 2 - 50 + 26, 90, 15, Text.of(""));
        this.tribeNameEditBox.setMaxLength(32);
        this.tribeNameEditBox.setText(this.handler.blockEntity.getTribe().getTribeName());
        this.tribeNameEditBox.setChangedListener(s -> {
            this.handler.blockEntity.getTribe().setTribeName(s);
            this.isDirty = true;
        });
        this.tribeNameEditBox.setDrawsBackground(false);
        this.addSelectableChild(this.tribeNameEditBox);

        this.tribeDescriptionEditBox = new TextFieldWidget(textRenderer, this.width / 2 - TRIBE_TABLE_WIDTH / 2 + TRIBE_TABLE_PADDING, this.height / 2 - 50 + 54, 90, 15, Text.of(""));
        this.tribeDescriptionEditBox.setMaxLength(128);
        this.tribeDescriptionEditBox.setText(this.handler.blockEntity.getTribe().getTribeDescription());
        this.tribeDescriptionEditBox.setChangedListener(s -> {
            this.handler.blockEntity.getTribe().setTribeDescription(s);
            this.isDirty = true;
        });
        this.tribeDescriptionEditBox.setDrawsBackground(false);
        this.addSelectableChild(this.tribeDescriptionEditBox);

        this.tribeDiscordUrlEditBox = new TextFieldWidget(textRenderer, this.width / 2 - TRIBE_TABLE_WIDTH / 2 + TRIBE_TABLE_PADDING, this.height / 2 - 50 + 82, 90, 15, Text.of(""));
        this.tribeDiscordUrlEditBox.setMaxLength(128);
        this.tribeDiscordUrlEditBox.setText(this.handler.blockEntity.getTribe().getTribeDiscordUrl());
        this.tribeDiscordUrlEditBox.setChangedListener(s -> {
            this.handler.blockEntity.getTribe().setTribeDiscordUrl(s);
            this.isDirty = true;
        });
        this.tribeDiscordUrlEditBox.setDrawsBackground(false);
        this.addSelectableChild(this.tribeDiscordUrlEditBox);

        this.tribeMemberList = new TribeMemberList(this.width / 2 + 25, this.height / 2 - 50 + 25, 90, 70, Text.of(""), textRenderer);
        this.addSelectableChild(this.tribeMemberList);

        this.tribeNameEditBox.setEditable(!loading);
        this.tribeDescriptionEditBox.setEditable(!loading);
        this.tribeDiscordUrlEditBox.setEditable(!loading);
    }

    @Override
    protected void save() {
        super.save();
        if (isDirty) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeEnumConstant(UpdateTribeC2SPacket.UpdateType.INFORMATION);
            buf.writeUuid(this.handler.blockEntity.getTribe().getTribeId());
            buf.writeString(this.tribeNameEditBox.getText());
            buf.writeString(this.tribeDescriptionEditBox.getText());
            buf.writeString(this.tribeDiscordUrlEditBox.getText());
            ClientPlayNetworking.send(ModPackets.UPDATE_TRIBE, buf);
            this.isDirty = false;
        }
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        if (!init) {
            return;
        }
        try {
            this.tribeNameEditBox.tick();
            this.tribeDescriptionEditBox.tick();
            this.tribeDiscordUrlEditBox.tick();

            boolean wasLoading = this.loading;

            this.loading = this.handler.blockEntity.getTribe().isTemp();

            if (wasLoading != this.loading) {
                this.tribeNameEditBox.setText(this.handler.blockEntity.getTribe().getTribeName());
                this.tribeDescriptionEditBox.setText(this.handler.blockEntity.getTribe().getTribeDescription());
                this.tribeDiscordUrlEditBox.setText(this.handler.blockEntity.getTribe().getTribeDiscordUrl());
            }

            this.tribeNameEditBox.setEditable(!loading);
            this.tribeDescriptionEditBox.setEditable(!loading);
            this.tribeDiscordUrlEditBox.setEditable(!loading);
        } catch (Exception e) {
            SOLTribes.LOGGER.error("Error trying to tick information screen: " + e.getMessage());
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.close();
        }
        return this.tribeNameEditBox.keyPressed(keyCode, scanCode, modifiers) || this.tribeNameEditBox.isActive() || this.tribeDescriptionEditBox.keyPressed(keyCode, scanCode, modifiers) || this.tribeDescriptionEditBox.isActive() || this.tribeDiscordUrlEditBox.keyPressed(keyCode, scanCode, modifiers) || this.tribeDiscordUrlEditBox.isActive() || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!init) return;
        if (loading) {
            super.renderBackground(context);
            context.drawText(textRenderer, "Loading...", this.width / 2 - textRenderer.getWidth("Loading...") / 2, this.height / 2 - textRenderer.fontHeight / 2, 0xFFFFFF, false);
            return;
        }
        super.drawTribeBackground(context, mouseX, mouseY);
        context.drawText(textRenderer, "Tribe Name", this.width / 2 - TRIBE_TABLE_WIDTH / 2 + TRIBE_TABLE_PADDING, this.height / 2 - 50 + 20 - textRenderer.fontHeight / 2 - 1, 0xFFFFFF, false);
        ScreenUtils.drawRect(context, this.width / 2 - TRIBE_TABLE_WIDTH / 2 + TRIBE_TABLE_PADDING - 2, this.height / 2 - 50 + 20 + 4, 92, 12, 0xFF939393);
        this.tribeNameEditBox.render(context, mouseX, mouseY, delta);
        context.drawText(textRenderer, "Tribe Description", this.width / 2 - TRIBE_TABLE_WIDTH / 2 + TRIBE_TABLE_PADDING, this.height / 2 - 50 + 48 - textRenderer.fontHeight / 2 - 1, 0xFFFFFF, false);
        ScreenUtils.drawRect(context, this.width / 2 - TRIBE_TABLE_WIDTH / 2 + TRIBE_TABLE_PADDING - 2, this.height / 2 - 50 + 48 + 4, 92, 12, 0xFF939393);
        this.tribeDescriptionEditBox.render(context, mouseX, mouseY, delta);
        context.drawText(textRenderer, "Discord Invite", this.width / 2 - TRIBE_TABLE_WIDTH / 2 + TRIBE_TABLE_PADDING, this.height / 2 - 50 + 76 - textRenderer.fontHeight / 2 - 1, 0xFFFFFF, false);
        ScreenUtils.drawRect(context, this.width / 2 - TRIBE_TABLE_WIDTH / 2 + TRIBE_TABLE_PADDING - 2, this.height / 2 - 50 + 76 + 4, 92, 12, 0xFF939393);
        this.tribeDiscordUrlEditBox.render(context, mouseX, mouseY, delta);

        ScreenUtils.drawCenteredRect(context, this.width / 2, this.height / 2 + 6, 25, 25, 0xFF939393);
        context.drawText(textRenderer, "BANNER", this.width / 2 - textRenderer.getWidth("BANNER") / 2, this.height / 2 - 20, 0xFFFFFF, false);

        context.drawText(textRenderer, "Tribe Members", this.width / 2 + 25, this.height / 2 - 50 + 20 - textRenderer.fontHeight / 2 - 1, 0xFFFFFF, false);
        ScreenUtils.drawRect(context, this.width / 2 + 25, this.height / 2 - 50 + 25, 90, 70, 0xFF939393);

        this.tribeMemberList.render(context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);

        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected int getTabIndex() {
        return 0;
    }

    private class TribeMemberList extends ScrollableWidget {

        private TextRenderer textRenderer;
        private Map<UUID, ItemStack> cachedPlayerHeads = new HashMap<>();
        private TribeMember hoveredTribeMember;

        public TribeMemberList(int i, int j, int k, int l, Text text, TextRenderer textRenderer) {
            super(i, j, k, l, text);
            this.textRenderer = textRenderer;
        }

        @Override
        protected int getContentsHeight() {
            List<TribeMember> tribeMembers = TribeTableInformationScreen.this.getTribeTableBlockEntity().getTribe().getTribeMembers();
            int rows = (int) Math.ceil(tribeMembers.size() / 3f);
            int height = rows * 30 + 3 - 16;
            return (int) Math.max(16, height);
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
//            super.drawBox(context, x, y, width, height);
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
            List<TribeMember> tribeMembers = TribeTableInformationScreen.this.getTribeTableBlockEntity().getTribe().getTribeMembers();
            TribeMember hoveredTribeMember = null;
            for (TribeMember tribeMember : tribeMembers) {
                ItemStack playerHead = this.createOrGetPlayerHead(tribeMember.getTribeMemberUUID(), tribeMember.getTribeMemberName());
                int x = this.getX() + 3 + (index % 3) * 30;
                int y = this.getY() + 3 + (index / 3) * 30;
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
}
