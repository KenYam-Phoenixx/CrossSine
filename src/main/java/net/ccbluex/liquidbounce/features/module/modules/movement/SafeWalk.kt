package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.value.BoolValue
import net.ccbluex.liquidbounce.features.value.IntegerValue
import net.minecraft.init.Blocks
import net.minecraft.item.ItemBlock
import net.minecraft.potion.Potion
import net.minecraft.util.BlockPos
import org.lwjgl.input.Keyboard

@ModuleInfo(name = "SafeWalk", category = ModuleCategory.MOVEMENT)
object SafeWalk : Module() {

    private val shiftValue = BoolValue("Shift", false)
    private val og = BoolValue("OnlyGround", false)
    private val onBlock = BoolValue("Block only", false).displayable { shiftValue.get() }
    private val noSpeedPotion = BoolValue("NoPotionSpeed", false).displayable { shiftValue.get() }
    private val onHoldShift = BoolValue("OnHoldShift", false).displayable { shiftValue.get() }
    val ShiftMax: IntegerValue = object : IntegerValue("Shift-Max", 0, 0, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val ShiftMin = ShiftMin.get()
            if (ShiftMin > newValue) {
                set(ShiftMin)
            }
        }
    }.displayable { shiftValue.get() } as IntegerValue
    private val ShiftMin: IntegerValue = object : IntegerValue("Shift-Min", 0, 0, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val ShiftMax = ShiftMax.get()
            if (ShiftMax < newValue) {
                set(ShiftMax)
            }
        }
    }.displayable { shiftValue.get() } as IntegerValue
    private val PitchLitmit = BoolValue("Pitch", false)
    private val PitchMax: IntegerValue = object : IntegerValue("Pitch-Max", 0, 0, 90) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val PitchMin = PitchMin.get()
            if (PitchMin > newValue) {
                set(PitchMin)
            }
        }
    }.displayable { PitchLitmit.get() } as IntegerValue
    private val PitchMin: IntegerValue = object : IntegerValue("Pitch-Min", 0, 0, 90) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val PitchMax = PitchMax.get()
            if (PitchMax < newValue) {
                set(PitchMax)
            }
        }
    }.displayable { PitchLitmit.get() } as IntegerValue

    @EventTarget
    fun onMove(event: MoveEvent) {
        if (shiftValue.get()) return
        if (!og.get() || mc.thePlayer.onGround) {
            event.isSafeWalk = !PitchLitmit.get() || mc.thePlayer.rotationPitch < PitchMax.get() && mc.thePlayer.rotationPitch > PitchMin.get()
        }
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (!shiftValue.get()) return
        if (mc.currentScreen == null) {
            if (mc.gameSettings.keyBindBack.isKeyDown) {
                if (!onBlock.get() || mc.thePlayer.heldItem.item is ItemBlock) {
                    if (!onHoldShift.get() || Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.keyCode)) {
                        if (!og.get() || mc.thePlayer.onGround) {
                            if (!noSpeedPotion.get() || !mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                                if (!PitchLitmit.get() || mc.thePlayer.rotationPitch < PitchMax.get() && mc.thePlayer.rotationPitch > PitchMin.get()) {
                                    mc.gameSettings.keyBindSneak.pressed = mc.theWorld.getBlockState(
                                        BlockPos(
                                            mc.thePlayer.posX + mc.thePlayer.motionX * getShift(),
                                            mc.thePlayer.posY - 1.0,
                                            mc.thePlayer.posZ + mc.thePlayer.motionZ * getShift()
                                        )
                                    ).block == Blocks.air
                                    return
                                }
                            }
                        }
                    }
                } else
                    mc.gameSettings.keyBindSneak.pressed = false
            }

            if (mc.thePlayer.moveForward > 0 && mc.thePlayer.isSneaking && !Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.keyCode)) {
                mc.gameSettings.keyBindSneak.pressed = false
            }
            if (onBlock.get() && mc.thePlayer.heldItem.item !is ItemBlock) {
                if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.keyCode)) {
                    mc.gameSettings.keyBindSneak.pressed = true
                }
            }
        }

    }

    private fun getShift(): Double {
        val fuckmin = ShiftMin.get() / 10
        val fuckmax = ShiftMax.get() / 10
        val min = Math.min(fuckmin, fuckmax).toDouble()
        val max = Math.max(fuckmin, fuckmax).toDouble()
        return Math.random() * (max - min) + min
    }

    override fun onDisable() {
        if (mc.thePlayer == null) {
            return
        }
    }

}
