package config

import chisel3._

object Configs {
  val InputFileName = "src/main/scala/data/1_2.txt"
  val InputFileName_1 = "src/main/scala/data/1_1.txt"
  val DEPTH = 8
  val DATA_WIDTH = 32
  val ADDR_WIDTH = 32
  val Morton_WIDTH = 30
  val Morton_WIDTH_by_per_axis = 10
  val Bucket_count = 1 << Morton_WIDTH
  val Node_count = 2 * DEPTH - 1
  val Begin = Node_count - DEPTH
  val End = Node_count
  val MASK = (1 << 10) - 1
}
