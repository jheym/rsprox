package net.rsprox.protocol.game.incoming.decoder.codec.events
import net.rsprot.buffer.JagByteBuf
import net.rsprot.protocol.ClientProt
import net.rsprox.protocol.ProxyMessageDecoder
import net.rsprox.protocol.game.incoming.decoder.prot.GameClientProt
import net.rsprox.protocol.game.incoming.model.events.EventCameraPosition
import net.rsprox.protocol.session.Session

public class EventCameraPositionDecoder : ProxyMessageDecoder<EventCameraPosition> {
    override val prot: ClientProt = GameClientProt.EVENT_CAMERA_POSITION

    override fun decode(
        buffer: JagByteBuf,
        session: Session,
    ): EventCameraPosition {
        val angleX = buffer.g2()
        val angleY = buffer.g2()
        return EventCameraPosition(
            angleX,
            angleY,
        )
    }
}
