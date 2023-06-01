package lbvh

import chisel3._
import chisel3.util._
import hardfloat._
import config.Configs._
import fifo._

class Compute_next_begin_and_endIO extends Bundle {
  val input = new Bundle {
    val begin = Input(UInt(ADDR_WIDTH.W))
    val end = Input(UInt(ADDR_WIDTH.W))
    val node = Input(new Node)
    val valid = Input(Bool())
  }

  val output = new Bundle {
    val next_begin = Output(UInt(ADDR_WIDTH.W))
    val next_end = Output(UInt(ADDR_WIDTH.W))
    val valid = Output(Bool())
  }

}

class Compute_next_begin_and_end extends Module {
  val io = IO(new Compute_next_begin_and_endIO)
  val merge_count = Wire(UInt(DATA_WIDTH.W))

  when(io.input.node.node_id === io.input.end - 2.U && io.input.valid) { // 需要end - 2 - begin + 1个周期
    merge_count := io.input.node.merge_index
    io.output.valid := 1.B

  }.otherwise {
    merge_count := 0.U
    io.output.valid := 0.B
  }

  io.output.next_begin := io.input.begin - merge_count
  io.output.next_end := io.input.end - 2.U * merge_count
}
