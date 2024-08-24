package me.thepond.soltribes.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.thepond.soltribes.SOLTribes;
import me.thepond.soltribes.packets.UpdateTribeC2SPacket;
import me.thepond.soltribes.registry.ModPackets;
import me.thepond.soltribes.screenhandler.TribeTableEffectsScreenHandlerBlockEntity;
import me.thepond.soltribes.tribe.effects.ITribeEffect;
import me.thepond.soltribes.tribe.effects.TribeEffects;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class TribeTableEffectsScreen extends TribeTableScreen<TribeTableEffectsScreenHandlerBlockEntity> {

    private static final Identifier TRIANGULAR_PLACEHOLDER = new Identifier(SOLTribes.MOD_ID, "textures/gui/tribe_widgets/triangular_placeholder.png");

    private static final int ROWS = 3;
    private static final int COLUMNS = 4;
    private static final int EFFECTS_PER_SIDE = 2;

    private static final int SPACE_BETWEEN = 40;
    private static final int SPACE_FROM_CENTER = 20;

    private static final int SMALLER_TRIANGLE_SIZE = 31;
    private static final int BIGGER_TRIANGLE_SIZE = 61;
    private static final int ICON_SIZE = 15;
    private static final int BIG_ICON_SIZE = 30;

    private boolean isDirty = false;

    private boolean init;

    public TribeTableEffectsScreen(TribeTableEffectsScreenHandlerBlockEntity handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, Text.literal("SETTLEMENT EFFECTS"));
    }

    @Override
    protected void init() {
        super.init();
        this.init = true;
        int index = 0;
        for (ITribeEffect tribeEffect : TribeEffects.getTribeEffects()) {
            int x = getEffectPosition(index)[0];
            int y = getEffectPosition(index)[1];
            this.addSelectableChild(ButtonWidget.builder(Text.empty(), button -> onEffectPress(tribeEffect.getTribeEffectId(), button)).dimensions(
                    x + ICON_SIZE / 2 + 1, y + ICON_SIZE / 2 + 1,
                    ICON_SIZE, ICON_SIZE
            ).build());
            index++;
        }
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!init) return;
        super.drawTribeBackground(context, mouseX, mouseY);

        context.drawTexture(TRIANGULAR_PLACEHOLDER, this.width / 2 - BIGGER_TRIANGLE_SIZE / 2, this.height / 2 + 6 - BIGGER_TRIANGLE_SIZE / 2, 0, 0, BIGGER_TRIANGLE_SIZE, BIGGER_TRIANGLE_SIZE, BIGGER_TRIANGLE_SIZE, BIGGER_TRIANGLE_SIZE);

        if (getTribeTableBlockEntity().getTribe().getTribeActiveEffect() != null) {
            context.drawTexture(getTribeTableBlockEntity().getTribe().getTribeActiveEffect().getTribeEffectIcon(), this.width / 2 - BIG_ICON_SIZE / 2, this.height / 2 - BIG_ICON_SIZE / 4, 0, 0, BIG_ICON_SIZE, BIG_ICON_SIZE, BIG_ICON_SIZE, BIG_ICON_SIZE);
        }

        if (mouseX >= this.width / 2 - BIGGER_TRIANGLE_SIZE / 2 + 5 && mouseX <= this.width / 2 - BIGGER_TRIANGLE_SIZE / 2 + BIGGER_TRIANGLE_SIZE - 5 && mouseY >= this.height / 2 + 6 - BIGGER_TRIANGLE_SIZE / 2 + 5 && mouseY <= this.height / 2 + 6 - BIGGER_TRIANGLE_SIZE / 2 + BIGGER_TRIANGLE_SIZE - 5) {
            List<Text> tooltip = new ArrayList<>();
            if (getTribeTableBlockEntity().getTribe().getTribeActiveEffect() != null) {
                tooltip.add(Text.literal(getTribeTableBlockEntity().getTribe().getTribeActiveEffect().getTribeEffectName()));
            }
            context.drawTooltip(textRenderer, tooltip, mouseX, mouseY);
        }

        for (int i = 0; i < ROWS * COLUMNS; i++) {
            ITribeEffect tribeEffect = TribeEffects.getTribeEffects().size() > i ? TribeEffects.getTribeEffects().get(i) : null;
            int x = getEffectPosition(i)[0];
            int y = getEffectPosition(i)[1];

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();

            context.setShaderColor(1.0F, 1.0F, 1.0F, 0.6F);

            if (tribeEffect != null) {
                enableEffectTransparency(context, tribeEffect.getTribeEffectId());
            }

            context.drawTexture(TRIANGULAR_PLACEHOLDER, x, y, 0, 0, SMALLER_TRIANGLE_SIZE, SMALLER_TRIANGLE_SIZE, SMALLER_TRIANGLE_SIZE, SMALLER_TRIANGLE_SIZE);
            if (tribeEffect != null) {
                context.drawTexture(tribeEffect.getTribeEffectIcon(), x + SMALLER_TRIANGLE_SIZE / 2 - ICON_SIZE / 2, y + SMALLER_TRIANGLE_SIZE / 2 - ICON_SIZE / 2, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
            }
            context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();

            if (mouseX >= x + 5 && mouseX <= x - 5 + SMALLER_TRIANGLE_SIZE && mouseY >= y + 5 && mouseY <= y - 5 + SMALLER_TRIANGLE_SIZE) {
                List<Text> tooltip = new ArrayList<>();
                if (tribeEffect != null) {
                    tooltip.add(Text.literal(tribeEffect.getTribeEffectName()));
                }
                context.drawTooltip(textRenderer, tooltip, mouseX, mouseY);
            }
        }

        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void save() {
        super.save();
        if (isDirty) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeEnumConstant(UpdateTribeC2SPacket.UpdateType.EFFECT);
            buf.writeUuid(this.handler.blockEntity.getTribe().getTribeId());
            buf.writeInt(this.handler.blockEntity.getTribe().getTribeActiveEffect() != null ? this.handler.blockEntity.getTribe().getTribeActiveEffect().getTribeEffectId() : -1);
            ClientPlayNetworking.send(ModPackets.UPDATE_TRIBE, buf);
        }
    }

    private int[] getEffectPosition(int index) {
        boolean doubleSpaced = index / COLUMNS % 2 == 0;
        boolean negative = index / EFFECTS_PER_SIDE % 2 == 0;
        int count = index % EFFECTS_PER_SIDE + 1;
        int row = index / COLUMNS;
        int x = this.width / 2 + (negative ? -SPACE_BETWEEN * count : SPACE_BETWEEN * count) - (doubleSpaced ? negative ? -SPACE_BETWEEN / 2 : SPACE_BETWEEN / 2 : 0) - SMALLER_TRIANGLE_SIZE / 2 + (negative ? -SPACE_FROM_CENTER : SPACE_FROM_CENTER);
        int y = this.height / 2 + 6 - (-SPACE_BETWEEN / 2 + row * SPACE_BETWEEN / 2) - SMALLER_TRIANGLE_SIZE / 2;
        return new int[]{x, y};
    }

    private void onEffectPress(int effectIndex, ButtonWidget buttonWidget) {
        this.isDirty = true;
        if (getTribeTableBlockEntity().getTribe().getTribeActiveEffect() != null && getTribeTableBlockEntity().getTribe().getTribeActiveEffect().getTribeEffectId() == effectIndex) {
            getTribeTableBlockEntity().getTribe().setTribeActiveEffect(null);
            return;
        }
        getTribeTableBlockEntity().getTribe().setTribeActiveEffect(TribeEffects.getTribeEffectById(effectIndex));
    }

    private void enableEffectTransparency(DrawContext context, int effectIndex) {
        if (getTribeTableBlockEntity().getTribe().getTribeActiveEffect() != null && getTribeTableBlockEntity().getTribe().getTribeActiveEffect().getTribeEffectId() == effectIndex) {
            context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    protected int getTabIndex() {
        return 2;
    }

    @Override
    protected boolean shouldRenderHotbar() {
        return false;
    }
}
