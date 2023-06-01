package lbvh

import chisel3._
import chisel3.util._
import hardfloat._
import config.Configs._
import fifo._

class Init_nodeIO extends Bundle {
  val input1 = new Bundle {
    val node = Input(new Node)
    val valid = Input(Bool())
  }
  val input2 = new Bundle {
    val need_merge = Input(Bool())
    val merge_index = Input(UInt(ADDR_WIDTH.W))
    val level = Input(UInt(ADDR_WIDTH.W))
    val valid = Input(Bool())
  }

  val output = new Bundle {
    val node = Output(new Node)
    val valid = Output(Bool())
  }
}

class Init_node extends Module {
  val io = IO(new Init_nodeIO)

  val leaves_fifo = Module(new CombFifo(new Node, 6)) // 6 = 5 + 2 -1,三个模块的周期差

  leaves_fifo.io.enq.valid := io.input1.valid
  leaves_fifo.io.enq.bits := io.input1.node
  leaves_fifo.io.deq.ready := io.input2.valid

  io.output.node.bbox := leaves_fifo.io.deq.bits.bbox
  io.output.node.first_child_or_primitive := leaves_fifo.io.deq.bits.first_child_or_primitive
  io.output.node.node_id := leaves_fifo.io.deq.bits.node_id
  io.output.node.primitive_count := leaves_fifo.io.deq.bits.primitive_count
  io.output.node.level := io.input2.level
  io.output.node.need_merge := io.input2.need_merge
  io.output.node.merge_index := io.input2.merge_index

  io.output.valid := io.input2.valid

}
