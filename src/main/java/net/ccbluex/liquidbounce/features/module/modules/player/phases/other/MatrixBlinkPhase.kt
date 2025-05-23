
package net.ccbluex.liquidbounce.features.module.modules.player.phases.other

import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.player.phases.PhaseMode
import net.ccbluex.liquidbounce.features.value.BoolValue
import net.ccbluex.liquidbounce.features.value.FloatValue
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.ccbluex.liquidbounce.utils.timer.tickTimer
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import kotlin.math.cos
import kotlin.math.sin

class MatrixBlinkPhase : PhaseMode("MatrixBlink") {
    private var matrixClip = false
    private var flagCount = 0
    private val tickTimer = tickTimer()
    private val timerValue = FloatValue("${valuePrefix}timer", 0.3f,0.1f,1f)
    private val clipDistValue = FloatValue("${valuePrefix}initialClipDistance", 0.1f, 0.03f, 0.3f)
    private val showFlagsValue = BoolValue("${valuePrefix}showFlag", true)
    override fun onEnable() {
        matrixClip = false
        tickTimer.reset()
        flagCount = 0
    }

    override fun onMove(event: MoveEvent) {
        if (flagCount > 5) return
        
        if (mc.thePlayer.isCollidedHorizontally) matrixClip = true

        if (matrixClip) {
            mc.timer.timerSpeed = timerValue.get()
            tickTimer.update()
            event.x = 0.0
            event.z = 0.0
            if (tickTimer.hasTimePassed(3)) {
                tickTimer.reset()
                matrixClip = false
            } else if (tickTimer.hasTimePassed(1)) {
                val offset = if (tickTimer.hasTimePassed(2)) 1.6 else clipDistValue.get().toDouble()
                val direction = MovementUtils.direction
                mc.thePlayer.setPosition(mc.thePlayer.posX + -sin(direction) * offset, mc.thePlayer.posY, mc.thePlayer.posZ + cos(direction) * offset)
            }
        } else {
            mc.timer.timerSpeed = 1f
            return
        }
    }
    override fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is S08PacketPlayerPosLook) {
            if (showFlagsValue.get()) ClientUtils.displayChatMessage("§7[§c§lPhase§7] §bFlag: §e§l${flagCount++}")
            if (flagCount < 4) {
                event.cancelEvent()
            }
            PacketUtils.sendPacketNoEvent(C03PacketPlayer.C06PacketPlayerPosLook(packet.x, packet.y, packet.z, packet.getYaw(), packet.getPitch(), true))
        }
    }
}
