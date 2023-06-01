package lbvh

import chisel3._
import chisel3.util._
import hardfloat._
import config.Configs._
import fifo._

class Input_nodeIO extends Bundle {
  val input1 = new Bundle {
    val node = Input(new Node)
    val valid = Input(Bool())
  }
  val output = new Bundle {
    val in_node = Output(new In_Node)
  }
}

class Input_node extends Module {
  val io = IO(new Input_nodeIO)

  val beginReg = RegInit(Begin.U(ADDR_WIDTH.W))
  val endReg = RegInit(End.U(ADDR_WIDTH.W))
  val nodefifo = Module(new CombFifo(new Node, DEPTH))
  val next_beginReg = Reg(UInt(ADDR_WIDTH.W))
  val next_endReg = Reg(UInt(ADDR_WIDTH.W))
  val fifiout_ready = RegInit(false.B)
  val clockaddstart = RegInit(false.B)
  val out_validReg = RegInit(false.B)

  val compute_next_begin_and_end = Module(new Compute_next_begin_and_end)

  compute_next_begin_and_end.io.input.begin := beginReg
  compute_next_begin_and_end.io.input.end := endReg
  compute_next_begin_and_end.io.input.node := io.input1.node
  compute_next_begin_and_end.io.input.valid := io.input1.valid

  nodefifo.io.enq.valid := io.input1.valid
  nodefifo.io.enq.bits := io.input1.node
  nodefifo.io.deq.ready := fifiout_ready

  val clock_count_reg = RegInit(0.U(DATA_WIDTH.W)) // 时钟计数

  when(io.input1.valid) {
    clockaddstart := true.B
  }
  when(clockaddstart || io.input1.valid) {
    clock_count_reg := clock_count_reg + 1.U
  }
  when(
    clock_count_reg >= endReg - beginReg - 2.U && clock_count_reg <= (endReg - beginReg) * 2.U - 3.U
  ) {
    out_validReg := true.B
    fifiout_ready := true.B
  }.otherwise {
    out_validReg := false.B
    fifiout_ready := false.B
  }

  when(clock_count_reg === endReg - beginReg - 2.U) { // 倒数第二个node的merge是有效的，寄存器变化需要一个周期
    next_beginReg := compute_next_begin_and_end.io.output.next_begin
    next_endReg := compute_next_begin_and_end.io.output.next_end
  }

  when(clock_count_reg === (endReg - beginReg) * 2.U - 2.U) {
    beginReg := next_beginReg
    endReg := next_endReg
    clockaddstart := false.B
    clock_count_reg := 0.U
  }

  io.output.in_node.node := nodefifo.io.deq.bits
  io.output.in_node.begin := beginReg
  io.output.in_node.end := endReg
  io.output.in_node.next_begin := next_beginReg
  io.output.in_node.next_end := next_endReg
  io.output.in_node.valid := out_validReg
}
