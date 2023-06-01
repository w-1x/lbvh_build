package lbvh

import chisel3._
import chisel3.util._
import hardfloat._
import config.Configs._
import fifo._

class New_comput_need_and_mergeIO extends Bundle {
  val input = new Bundle {
    val parent_node = Input(new Out_Node)
    val valid = Input(Bool())
  }
  val output = new Bundle {
    val node = Output(new Out_Node)
    val valid = Output(Bool())
  }
}

class New_comput_need_and_merge extends Module {
  val io = IO(new New_comput_need_and_mergeIO)

  val nodefifo = Module(new CombFifo(new Node, DEPTH))
  val out_nodefifo = Module(new CombFifo(new Node, DEPTH))
  val beginReg = Reg(UInt(ADDR_WIDTH.W))
  val endReg = Reg(UInt(ADDR_WIDTH.W))
  val merge_count = Reg(UInt(ADDR_WIDTH.W))
  val last_node = RegInit(false.B)
  val compute_need_and_merge = Module(new Compute_need_and_merge1) // 5周期
  // 最后一次合并输出队列有问题，使用寄存器解决
  val out_nodeReg = Reg(Vec(3, new Node))

  val clock_count_reg = RegInit(0.U(DATA_WIDTH.W)) // 时钟计数

  val clock_count_reg2 = RegInit(0.U(DATA_WIDTH.W)) // 时钟计数
  when(nodefifo.io.deq.ready) { // 最后一次合并输出队列有问题，使用寄存器解决
    out_nodeReg(0) := nodefifo.io.deq.bits
  }
  when(clock_count_reg2 === 2.U) {
    out_nodeReg(1) := out_nodeReg(0)
  }
  when(clock_count_reg2 === 3.U) {
    out_nodeReg(2) := out_nodeReg(0)
  }

  nodefifo.io.enq.valid := io.input.valid
  nodefifo.io.enq.bits := io.input.parent_node.node
  nodefifo.io.deq.ready := last_node

  when(endReg - beginReg === 1.U) {
    last_node := false.B
  }.elsewhen(clock_count_reg === endReg - beginReg && endReg =/= 0.U) {
    last_node := true.B
    clock_count_reg2 := clock_count_reg2 + 1.U
  }.otherwise {
    last_node := false.B
  }

  when(clock_count_reg2 === endReg - beginReg + 5.U) {
    clock_count_reg := 0.U
    clock_count_reg2 := 0.U
  }

  when(clock_count_reg2 >= endReg - beginReg) {
    last_node := false.B
  }

  when(io.input.valid) {
    clock_count_reg := clock_count_reg + 1.U
    beginReg := io.input.parent_node.next_begin
    endReg := io.input.parent_node.next_end
  }
//最后一次合并
  when(endReg === 3.U && beginReg === 1.U) {
    when(clock_count_reg2 === 6.U) {
      io.output.node.node.bbox := out_nodeReg(1).bbox
      io.output.node.node.first_child_or_primitive := out_nodeReg(
        1
      ).first_child_or_primitive
      io.output.node.node.primitive_count := out_nodeReg(1).primitive_count
      io.output.node.node.node_id := out_nodeReg(1).node_id

    }.elsewhen(clock_count_reg2 === 7.U) {
      io.output.node.node.bbox := out_nodeReg(2).bbox
      io.output.node.node.first_child_or_primitive := out_nodeReg(
        2
      ).first_child_or_primitive
      io.output.node.node.primitive_count := out_nodeReg(2).primitive_count
      io.output.node.node.node_id := out_nodeReg(2).node_id
    }.otherwise {
      io.output.node.node.bbox := out_nodefifo.io.deq.bits.bbox
      io.output.node.node.first_child_or_primitive := out_nodefifo.io.deq.bits.first_child_or_primitive
      io.output.node.node.primitive_count := out_nodefifo.io.deq.bits.primitive_count
      io.output.node.node.node_id := out_nodefifo.io.deq.bits.node_id
    }
  }.otherwise {
    io.output.node.node.bbox := out_nodefifo.io.deq.bits.bbox
    io.output.node.node.first_child_or_primitive := out_nodefifo.io.deq.bits.first_child_or_primitive
    io.output.node.node.primitive_count := out_nodefifo.io.deq.bits.primitive_count
    io.output.node.node.node_id := out_nodefifo.io.deq.bits.node_id
  }
  compute_need_and_merge.io.input.level := nodefifo.io.deq.bits.level
  compute_need_and_merge.io.input.valid := last_node
  compute_need_and_merge.io.input.size := endReg - beginReg

  out_nodefifo.io.enq.valid := nodefifo.io.deq.ready
  out_nodefifo.io.enq.bits := nodefifo.io.deq.bits
  out_nodefifo.io.deq.ready := compute_need_and_merge.io.output.valid

  io.output.valid := out_nodefifo.io.deq.ready
  io.output.node.next_begin := beginReg
  io.output.node.next_end := endReg
  io.output.node.node.level := compute_need_and_merge.io.output.level
  io.output.node.node.merge_index := compute_need_and_merge.io.output.merge_index
  io.output.node.node.need_merge := compute_need_and_merge.io.output.need_merge

}
