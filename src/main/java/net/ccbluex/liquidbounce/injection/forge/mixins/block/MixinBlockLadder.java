 
package net.ccbluex.liquidbounce.injection.forge.mixins.block;

import net.ccbluex.liquidbounce.CrossSine;
import net.ccbluex.liquidbounce.features.module.modules.other.ViaVersionFix;
import net.minecraft.block.BlockLadder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import java.util.Objects;

@Mixin(BlockLadder.class)
public class MixinBlockLadder {
    @ModifyConstant(method = "setBlockBoundsBasedOnState", constant = @Constant(floatValue = 0.125F))
    private float ViaVersion_LadderBB(float constant) {
        if (Objects.requireNonNull(CrossSine.moduleManager.getModule(ViaVersionFix.class)).getState())
            return 0.1875F;
        return 0.125F;
    }
}
