package lbvh

import chisel3._
import chisel3.util._
import hardfloat._
import config.Configs._
import fifo._

class Input_node2IO extends Bundle {
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

class Input_node2 extends Module {
  val io = IO(new Input_node1IO)

  val beginReg = RegInit(Begin.U(ADDR_WIDTH.W))
  val endReg = RegInit(End.U(ADDR_WIDTH.W))
  val nodefifo = Module(new CombFifo(new Node, DEPTH))
  val next_beginReg = Reg(UInt(ADDR_WIDTH.W))
  val next_endReg = Reg(UInt(ADDR_WIDTH.W))

  val fifo_enq_valid = WireInit(false.B)
  val out_valid = WireInit(false.B)
  val out_validReg = RegInit(false.B)
  val size = WireInit(0.U(ADDR_WIDTH.W))
  val clockaddstart = RegInit(false.B)

  val node_valid = RegInit(false.B)
  val node_tempReg = Reg(new Node)
  val compute_next_begin_and_end = Module(new Compute_next_begin_and_end)
  val compute_finish = RegInit(false.B)

  // 当输入的node数量小于等于3时，队列内部有延迟，不能立刻输出，故使用寄存器队列保存
  val node_outReg = Reg(Vec(2, new Node))

  node_outReg(0) := node_tempReg
  node_outReg(1) := node_outReg(0)
  size := endReg - beginReg

  val clock_count_reg = RegInit(0.U(DATA_WIDTH.W)) // 时钟计数
  when(io.input1.valid || io.input2.valid) {
    clockaddstart := true.B
  }
  when(clockaddstart || io.input1.valid || io.input2.valid) {
    clock_count_reg := clock_count_reg + 1.U
  }

  node_valid := io.input1.valid || io.input2.valid
  // 读取node
  when(io.input1.valid) {
    beginReg := Begin.U
    endReg := End.U
    node_tempReg := io.input1.node
  }
  when(io.input2.valid) {
    beginReg := io.input2.node.next_begin
    endReg := io.input2.node.next_end
    node_tempReg := io.input2.node.node
  }

  nodefifo.io.enq.valid := node_valid
  nodefifo.io.enq.bits := node_tempReg
  compute_next_begin_and_end.io.input.begin := beginReg
  compute_next_begin_and_end.io.input.end := endReg
  compute_next_begin_and_end.io.input.node := node_tempReg
  compute_next_begin_and_end.io.input.valid := node_valid

  // 保存计算next_begin和end结果
  when(compute_next_begin_and_end.io.output.valid) {
    next_beginReg := compute_next_begin_and_end.io.output.next_begin
    next_endReg := compute_next_begin_and_end.io.output.next_end
    compute_finish := true.B
  }

  nodefifo.io.deq.ready := compute_finish

  when(size === 3.U) {
    out_valid := (clock_count_reg < 6.U && clock_count_reg >= 3.U)
    io.output.in_node.node := node_outReg(1)
  }.elsewhen(size === 2.U) {
    out_valid := (clock_count_reg < 4.U && clock_count_reg >= 2.U)
    io.output.in_node.node := node_outReg(0)
  }.otherwise {
    out_valid := (nodefifo.io.deq.valid && compute_finish)
    io.output.in_node.node := nodefifo.io.deq.bits
  }

  // 输出In_Node

  io.output.in_node.valid := out_valid

  io.output.in_node.begin := beginReg
  io.output.in_node.end := endReg
  io.output.in_node.next_begin := next_beginReg
  io.output.in_node.next_end := next_endReg

  // 输出完成后compute_finish信号

  out_validReg := out_valid
  when(out_validReg && !out_valid) {
    clock_count_reg := 0.B
    clockaddstart := false.B
    compute_finish := false.B
  }
}
