package lbvh

import chisel3._
import chisel3.util._
import hardfloat._
import config.Configs._

class Need_merge_notfirst_compute extends Module {
  val io = IO(new Bundle {
    val input_level_isub1 = Input(UInt(ADDR_WIDTH.W))
    val input_level_i = Input(UInt(ADDR_WIDTH.W))
    val input_level_iadd1 = Input(UInt(ADDR_WIDTH.W))
    val output_need_merge = Output(Bool())
  })

  io.output_need_merge := (io.input_level_isub1 <= io.input_level_i
    && io.input_level_i >= io.input_level_iadd1)
}
