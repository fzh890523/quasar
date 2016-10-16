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

package ygg.table

import ygg._, common._, json._
import scalaz._, Scalaz._

class PlayTable extends ColumnarTableModule {
  type TableCompanion = ColumnarTableCompanion

  def fromSlices(slices: NeedSlices, size: TableSize): Table = new Table(slices, size)

  object Table extends ColumnarTableCompanion
  class Table(slices: NeedSlices, size: TableSize) extends ColumnarTable(slices, size) with NoLoadOrSortTable {
    override def toString = jvalueStream mkString "\n"
  }

  private def makeSlice(sampleData: Stream[JValue], sliceSize: Int): (Slice, Stream[JValue]) = {
    @tailrec def buildColArrays(from: Stream[JValue], into: ArrayColumnMap, sliceIndex: Int): (ArrayColumnMap, Int) = {
      from match {
        case jv #:: xs =>
          val refs = Slice.withIdsAndValues(jv, into, sliceIndex, sliceSize)
          buildColArrays(xs, refs, sliceIndex + 1)
        case _ =>
          (into, sliceIndex)
      }
    }

    val (prefix, suffix) = sampleData.splitAt(sliceSize)
    val (refs, size)     = buildColArrays(prefix.toStream, Map(), 0)
    val slice            = Slice(size, refs)

    slice -> suffix
  }

  // production-path code uses fromRValues, but all the tests use fromJson
  // this will need to be changed when our tests support non-json such as CDate and CPeriod
  def fromJson0(values: Stream[JValue], sliceSize: Int): Table = Table(
    StreamT.unfoldM(values)(events => Need(events.nonEmpty option makeSlice(events.toStream, sliceSize))),
    ExactSize(values.length)
  )

  def fromJson(values: Seq[JValue], maxSliceSize: Option[Int]): Table =
    fromJson0(values.toStream, maxSliceSize getOrElse yggConfig.maxSliceSize)

  def toJson(dataset: Table): Need[Stream[JValue]]          = dataset.toJson.map(_.toStream)
  def toJsonSeq(table: Table): Seq[JValue]                  = toJson(table).copoint
  def fromJson(data: Seq[JValue]): Table                    = fromJson0(data.toStream, yggConfig.maxSliceSize)
}

object PlayTable extends PlayTable {
  def apply(json: String): Table = fromJson(JParser.parseManyFromString(json).fold(throw _, x => x))
  def apply(file: jFile): Table  = apply(file.slurpString)
}
