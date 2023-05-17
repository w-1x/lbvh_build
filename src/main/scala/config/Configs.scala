package config

import chisel3._

object Configs {
  val InputFileName = "src/main/scala/data/1_2.txt"
  val DEPTH = 8
  val DATA_WIDTH = 32
  val ADDR_WIDTH = 32
  val Morton_WIDTH = 6
  val Morton_WIDTH_by_per_axis = 2
  val bucket_count = 16
  val node_count = 2 * DEPTH
  val begin = node_count - DEPTH
  val end = node_count

}
