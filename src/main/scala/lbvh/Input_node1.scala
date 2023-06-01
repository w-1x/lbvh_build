package lbvh

import chisel3._
import chisel3.util._
import hardfloat._
import config.Configs._
import fifo._

class Input_node1IO extends Bundle {
  val input1 = new Bundle {
    val node = Input(new Node)
    val valid = Input(Bool())
  }
  val input2 = new Bundle {
    val node = Input(new Out_Node)
    val valid = Input(Bool())
  }
  val output = new Bundle {
    val in_node = Output(new In_Node)
  }
}

class Input_node1 extends Module {
  val io = IO(new Input_node1IO)

  val beginReg = RegInit(Begin.U(ADDR_WIDTH.W))
  val endReg = RegInit(End.U(ADDR_WIDTH.W))
  val nodefifo = Module(new CombFifo(new Node, DEPTH))
  val next_beginReg = Reg(UInt(ADDR_WIDTH.W))
  val next_endReg = Reg(UInt(ADDR_WIDTH.W))
  val fifiout_ready = RegInit(false.B)
  val clockaddstart = RegInit(false.B)
  val out_validReg = RegInit(false.B)
  val size = RegInit(0.U(ADDR_WIDTH.W))

  val node_tempReg = Reg(Vec(2, new Node))
  // 当输入的node数量小于等于3时，队列内部有延迟，不能立刻输出，故使用寄存器保存

  val compute_next_begin_and_end = Module(new Compute_next_begin_and_end)

  when(io.input2.valid) { // 记录新的begin和end
    beginReg := io.input2.node.next_begin
    endReg := io.input2.node.next_end
  }
  compute_next_begin_and_end.io.input.begin := beginReg
  compute_next_begin_and_end.io.input.end := endReg

  when(io.input2.valid) {
    compute_next_begin_and_end.io.input.node := io.input2.node.node
    compute_next_begin_and_end.io.input.valid := io.input2.valid
  }.otherwise {
    nodefifo.io.enq.valid := io.input1.valid
    nodefifo.io.enq.bits := io.input1.node
    nodefifo.io.deq.ready := fifiout_ready
    compute_next_begin_and_end.io.input.node := io.input1.node
    compute_next_begin_and_end.io.input.valid := io.input1.valid
  }

  val clock_count_reg = RegInit(0.U(DATA_WIDTH.W)) // 时钟计数

  when(io.input1.valid || io.input2.valid) {
    clockaddstart := true.B
  }
  when(clockaddstart || io.input1.valid || io.input2.valid) {
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
    clockaddstart := false.B
    clock_count_reg := 0.U
  }

  when(
    io.input2.node.next_end - io.input2.node.next_begin === 2.U && endReg - beginReg === 2.U && io.input2.valid
  ) {
    node_tempReg(1) := io.input2.node.node
    io.output.in_node.node := node_tempReg(1)
  }.elsewhen(
    io.input2.node.next_end - io.input2.node.next_begin === 3.U && endReg - beginReg === 3.U
  ) {
    node_tempReg(0) := io.input2.node.node
    node_tempReg(1) := node_tempReg(0)
    io.output.in_node.node := node_tempReg(1)
  }.otherwise {
    nodefifo.io.enq.valid := io.input2.valid
    nodefifo.io.enq.bits := io.input2.node.node
    nodefifo.io.deq.ready := fifiout_ready
    io.output.in_node.node := nodefifo.io.deq.bits
  }

  io.output.in_node.begin := beginReg
  io.output.in_node.end := endReg
  io.output.in_node.next_begin := next_beginReg
  io.output.in_node.next_end := next_endReg
  io.output.in_node.valid := out_validReg
}
