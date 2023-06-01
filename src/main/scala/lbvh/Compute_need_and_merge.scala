package lbvh

import chisel3._
import chisel3.util._
import hardfloat._
import config.Configs._
import fifo._

class Compute_need_and_mergeIO extends Bundle {
  val input = new Bundle {
    val level = Input(UInt(ADDR_WIDTH.W))
    val valid = Input(Bool())
  }
  val output = new Bundle {
    val need_merge = Output(Bool())
    val merge_index = Output(UInt(ADDR_WIDTH.W))
    val level = Output(UInt(ADDR_WIDTH.W))
    val valid = Output(Bool())
  }
}

class Compute_need_and_merge extends Module { // 5个时钟周期 = 2+ 1 + 1 + 1
  val io = IO(new Compute_need_and_mergeIO)

  val level_temReg = Reg(Vec(5, UInt(ADDR_WIDTH.W)))
  val need_mergeReg = Reg(Vec(2, Bool()))
  val need_merge_first_compute = Module(new Need_merge_first_compute)
  val need_merge_notfirst_compute = Module(new Need_merge_notfirst_compute)
  val out_needmerge1 = WireInit(false.B)
  val out_valid = WireInit(false.B)
  val out_merge = RegInit(0.U(ADDR_WIDTH.W))
  val out_need_mergeReg = RegInit(false.B)
  val clockaddstartReg = RegInit(false.B)

  val clock_count_reg = RegInit(0.U(DATA_WIDTH.W)) // 时钟计数

  need_merge_first_compute.io.input_level_i := level_temReg(1)
  need_merge_first_compute.io.input_level_iadd1 := level_temReg(0)

  need_merge_notfirst_compute.io.input_level_isub1 := level_temReg(2)
  need_merge_notfirst_compute.io.input_level_i := level_temReg(1)
  need_merge_notfirst_compute.io.input_level_iadd1 := level_temReg(0)

  when(clockaddstartReg || io.input.valid) {
    clock_count_reg := clock_count_reg + 1.U
  }
  when(clock_count_reg >= 4.U + DEPTH.U) {
    clockaddstartReg := false.B
    clock_count_reg := 0.B
  }

  when(io.input.valid) { // 读入用于计算的level 数据 3个周期
    clockaddstartReg := true.B
    level_temReg(0) := io.input.level
  }.otherwise {
    level_temReg(0) := 0.U
  }
  for (i <- 0 until (4)) {
    level_temReg(i + 1) := level_temReg(i)
  }
  when(clock_count_reg === 2.U) { // mergeReg(0)在clock = 2时被赋值
    need_mergeReg(0) := need_merge_first_compute.io.output_need_merge
  }.elsewhen(clock_count_reg > 2.U && clock_count_reg <= DEPTH.U) {
    need_mergeReg(0) := need_merge_notfirst_compute.io.output_need_merge
  }.otherwise {
    need_mergeReg(0) := 0.U
  } // mergeReg(0)在clock = 3时被赋值
// 非首node时，mergeReg(1)在clock = 4时初始化，否则在clock= 3
  need_mergeReg(1) := need_mergeReg(0)
  when(clock_count_reg >= 4.U) {
    out_needmerge1 := (need_mergeReg(1) && !need_mergeReg(0))
    out_need_mergeReg := out_needmerge1
    out_merge := out_merge + out_needmerge1 // 更新needmerge和merge需要1个周期
  }
  when(clock_count_reg > 4.U && clock_count_reg <= 4.U + DEPTH.U) {
    out_valid := true.B
  }.otherwise {
    out_valid := false.B
  }

  io.output.need_merge := out_need_mergeReg
  io.output.merge_index := out_merge
  io.output.level := level_temReg(4)
  io.output.valid := out_valid
}
