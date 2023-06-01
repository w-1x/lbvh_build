package lbvh

import chisel3._
import chisel3.util._
import hardfloat._
import config.Configs._

class CountIO extends Bundle {

  val input = new Bundle {
    val valid = Input(Bool())
  }

  val output = new Bundle {
    val id = Output(UInt(ADDR_WIDTH.W))
    val valid = Output(Bool())
  }
}

class Count(addn: Int) extends Module { // 单周期
  val io = IO(new CountIO)
  val clock_count_reg = RegInit(0.U(DATA_WIDTH.W))
  when(io.input.valid) {
    clock_count_reg := clock_count_reg + addn.U
  }

  io.output.id := clock_count_reg
  io.output.valid := io.input.valid
}
