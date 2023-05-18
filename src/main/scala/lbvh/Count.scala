package lbvh

import chisel3._
import chisel3.util._
import hardfloat._
import config.Configs._

class CountIO extends Bundle {
  val valid = Input(Bool())
  val id = Output(UInt(ADDR_WIDTH.W))
}

class Count(addn: Int) extends Module { // 单周期
  val io = IO(new CountIO)
  val clock_count_reg = RegInit(0.U(DATA_WIDTH.W))
  when(io.valid) {
    clock_count_reg := clock_count_reg + addn.U
  }

  io.id := clock_count_reg
}
