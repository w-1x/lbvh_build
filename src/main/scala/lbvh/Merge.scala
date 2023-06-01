package lbvh

import chisel3._
import chisel3.util._
import hardfloat._
import config.Configs._
import fifo._

class MergeIO extends Bundle {
  val input = new Bundle {
    val in_node = Input(new In_Node)
  }

  val output = new Bundle {
    val parent_node = Output(new Out_Node)
    val first_node = Output(new Node)
    val second_node = Output(new Node)
    val valid = Output(Bool())
  }
}

class Merge extends Module { // 1周期出一个结果
  val io = IO(new MergeIO)

  val merge_node = Module(new Merge_node)
  val not_merge_node = Module(new Not_merge_node)
  val temp_nodeReg = Reg(new In_Node)

  val clock_count_reg = RegInit(0.U(DATA_WIDTH.W)) // 时钟计数
  val clockaddstart = RegInit(false.B)
  val out_valid = WireInit(false.B)

  when(io.input.in_node.valid) {
    clockaddstart := true.B
  }
  when(clockaddstart || io.input.in_node.valid) {
    clock_count_reg := clock_count_reg + 1.U
  }
  when(
    clock_count_reg >= 1.U &&
      clock_count_reg <= temp_nodeReg.end - temp_nodeReg.begin
  ) {
    out_valid := true.B
  }.otherwise {
    out_valid := false.B
  }

  when(io.input.in_node.valid) { // 合并需要两个node，先缓存一个
    temp_nodeReg := io.input.in_node
  }

  merge_node.io.input.node1 := temp_nodeReg.node
  merge_node.io.input.node2 := io.input.in_node.node
  merge_node.io.input.begin := temp_nodeReg.begin
  merge_node.io.input.next_begin := temp_nodeReg.next_begin
  merge_node.io.input.next_end := temp_nodeReg.next_end

  not_merge_node.io.input.node := temp_nodeReg.node
  not_merge_node.io.input.begin := temp_nodeReg.begin
  not_merge_node.io.input.next_begin := temp_nodeReg.next_begin

  when(temp_nodeReg.node.need_merge) {
    io.output.parent_node.node := merge_node.io.output.parents_node
    io.output.first_node := merge_node.io.output.first_node
    io.output.second_node := merge_node.io.output.second_node
  }.otherwise {
    io.output.parent_node.node := not_merge_node.io.output.parent_node
    io.output.first_node := not_merge_node.io.output.first_node
    io.output.second_node := not_merge_node.io.output.second_node
  }

  io.output.parent_node.next_begin := io.input.in_node.next_begin
  io.output.parent_node.next_end := io.input.in_node.next_end

  io.output.valid := out_valid

}
