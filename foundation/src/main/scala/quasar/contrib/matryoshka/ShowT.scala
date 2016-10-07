/*
 * Copyright 2014–2016 SlamData Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package quasar.contrib.matryoshka

import quasar.Predef._

import matryoshka._
import scalaz._
import simulacrum.typeclass

@typeclass trait ShowT[T[_[_]]] {
  def show[F[_]](tf: T[F])(implicit del: Delay[Show, F]): Cord =
    Cord(shows(tf))
  def shows[F[_]](tf: T[F])(implicit del: Delay[Show, F]): String =
    show(tf).toString
  def showT[F[_]](delay: Delay[Show, F]): Show[T[F]] =
    Show.show[T[F]](show[F](_)(delay))
}

object ShowT {
  implicit val fix: ShowT[Fix] = new ShowT[Fix] {
    override def show[F[_]](tf: Fix[F])(implicit del: Delay[Show, F]): Cord =
      del(showT[F](del)).show(tf.unFix)
  }
}
