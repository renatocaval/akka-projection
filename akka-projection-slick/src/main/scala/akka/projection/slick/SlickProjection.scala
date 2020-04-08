/*
 * Copyright (C) 2020 Lightbend Inc. <https://www.lightbend.com>
 */

package akka.projection.slick

import akka.Done
import akka.annotation.ApiMayChange
import akka.projection.Projection
import akka.projection.slick.internal.SlickProjectionImpl
import akka.stream.scaladsl.Source
import slick.basic.DatabaseConfig
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile

import scala.reflect.ClassTag

object SlickProjection {
  @ApiMayChange
  def transactional[Offset, StreamElement, P <: JdbcProfile: ClassTag](
      projectionId: String,
      sourceProvider: Option[Offset] => Source[StreamElement, _],
      offsetExtractor: StreamElement => Offset,
      databaseConfig: DatabaseConfig[P])(eventHandler: StreamElement => DBIO[Done]): Projection =
    new SlickProjectionImpl(projectionId, sourceProvider, offsetExtractor, databaseConfig, eventHandler)
}
