package akka.projection.provider

import akka.NotUsed
import akka.persistence.query.scaladsl.EventsByTagQuery
import akka.persistence.query.scaladsl.CurrentEventsByTagQuery
import akka.persistence.query.{ NoOffset, Offset }
import akka.projection.scaladsl.SourceProvider
import akka.stream.scaladsl.Source

class AkkaPersistenceQuerySourceProvider {

  def eventsByTag[Event](
      eventsByTagQuery: EventsByTagQuery,
      tag: String): SourceProvider[Offset, TypedEventEnvelope[Event]] =
    new EventsByTagSourceProvider[Event](eventsByTagQuery, tag)

  def currentEventsByTag[Event](
      currentEventsByTagQuery: CurrentEventsByTagQuery,
      tag: String): SourceProvider[Offset, TypedEventEnvelope[Event]] =
    new CurrentEventsByTagSourceProvider[Event](currentEventsByTagQuery, tag)

  private abstract class AkkaPersistenceQuerySourceProvider[Event]
      extends SourceProvider[Offset, TypedEventEnvelope[Event]] {
    override def extractOffset(envelope: TypedEventEnvelope[Event]): Offset = envelope.offset
  }

  private class EventsByTagSourceProvider[Event](eventsByTagQuery: EventsByTagQuery, tag: String)
      extends AkkaPersistenceQuerySourceProvider[Event] {

    override def source(offsetOpt: Option[Offset]): Source[TypedEventEnvelope[Event], NotUsed] = {
      val offset = offsetOpt.getOrElse(NoOffset)
      eventsByTagQuery
        .eventsByTag(tag, offset)
        .map(env => TypedEventEnvelope(env))
    }

  }

  private class CurrentEventsByTagSourceProvider[Event](currentEventsByTagQuery: CurrentEventsByTagQuery, tag: String)
      extends AkkaPersistenceQuerySourceProvider[Event] {

    override def source(offsetOpt: Option[Offset]): Source[TypedEventEnvelope[Event], NotUsed] = {
      val offset = offsetOpt.getOrElse(NoOffset)
      currentEventsByTagQuery
        .currentEventsByTag(tag, offset)
        .map(env => TypedEventEnvelope(env))
    }

  }
}
