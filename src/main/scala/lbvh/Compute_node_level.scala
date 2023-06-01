package lbvh

import chisel3._
import chisel3.util._
import hardfloat._
import config.Configs._
import fifo._

class Compute_node_levelIO extends Bundle {
  val input = new Bundle {
    val morton_code = Input(UInt(Morton_WIDTH.W))
    val indice = Input(UInt(ADDR_WIDTH.W))
    val valid = Input(Bool())
  }
  val output = new Bundle {
    val level = Output(UInt(ADDR_WIDTH.W))
    val valid = Output(Bool())
  }
}

class Compute_node_level extends Module { // 2周期
  val io = IO(new Compute_node_levelIO)

  val morton_codeReg = Reg(Vec(2, UInt(Morton_WIDTH.W)))

  val input_find_zero = WireInit(0.U(ADDR_WIDTH.W))
  val output_find_zero = WireInit(Morton_WIDTH.U(ADDR_WIDTH.W))
  val outvalid = WireInit(false.B)
  val clock_count_reg = RegInit(0.U(DATA_WIDTH.W)) // 时钟计数
  val clockaddstart = RegInit(false.B)

  when(io.input.valid) {
    clockaddstart := true.B
    morton_codeReg(0) := io.input.morton_code
    morton_codeReg(1) := morton_codeReg(0)
  }
  when(clock_count_reg >= 1.U) {
    input_find_zero := morton_codeReg(0) ^ morton_codeReg(1)
    for (i <- 1 until (Morton_WIDTH + 1)) {
      when(
        input_find_zero >= (1.U << (Morton_WIDTH - i)) && input_find_zero < (1.U << (Morton_WIDTH - i + 1))
      ) {
        output_find_zero := i.U - 1.U
      }
    }
  }
  when(clockaddstart || io.input.valid) {
    clock_count_reg := clock_count_reg + 1.U
  }

  when(clock_count_reg >= 2.U && clock_count_reg < 2.U + DEPTH.U) {
    outvalid := true.B
  }.otherwise {
    outvalid := false.B
  }
  when(clock_count_reg >= DEPTH.U + 1.U) {
    output_find_zero := 0.U
  }

  io.output.level := output_find_zero
  io.output.valid := outvalid
}
