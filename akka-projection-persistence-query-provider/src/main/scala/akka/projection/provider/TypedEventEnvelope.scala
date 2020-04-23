package akka.projection.provider

import akka.persistence.query.{ EventEnvelope, Offset }

object TypedEventEnvelope {
  def apply[Event](eventEnvelope: EventEnvelope): TypedEventEnvelope[Event] = {
    new TypedEventEnvelope[Event](eventEnvelope)
  }
}

final class TypedEventEnvelope[Event] private[akka] (eventEnvelope: EventEnvelope) {

  val offset = eventEnvelope.offset
  val persistenceId = eventEnvelope.persistenceId
  val sequenceNr = eventEnvelope.sequenceNr
  val event = eventEnvelope.event.asInstanceOf[Event]
  val timestamp = eventEnvelope.timestamp

  override def equals(other: Any): Boolean =
    other match {
      case that: TypedEventEnvelope[_] => this.eventEnvelope == that.eventEnvelope
      case _ => false
    }

  override def hashCode(): Int = eventEnvelope.hashCode()

}
