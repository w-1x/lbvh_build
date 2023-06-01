package lbvh

import chisel3._
import chisel3.util._
import hardfloat._
import config.Configs._
import fifo._

class Not_merge_nodeIO extends Bundle {
  val input = new Bundle {
    val node = Input(new Node)
    val begin = Input(UInt(ADDR_WIDTH.W))
    val next_begin = Input(UInt(ADDR_WIDTH.W))
  }

  val output = new Bundle {
    val parent_node = Output(new Node)
    val first_node = Output(new Node)
    val second_node = Output(new Node)
  }
}

class Not_merge_node extends Module { // 组合逻辑
  val io = IO(new Not_merge_nodeIO)
//size_t unmerged_index = unmerged_begin + i - begin - merged_index[i];
  io.output.parent_node.node_id := io.input.next_begin + io.input.node.node_id - io.input.begin - io.input.node.merge_index
  io.output.parent_node.bbox := io.input.node.bbox
  io.output.parent_node.first_child_or_primitive := io.input.node.first_child_or_primitive
  io.output.parent_node.level := io.input.node.level
  io.output.parent_node.primitive_count := io.input.node.primitive_count
  io.output.parent_node.merge_index := 0.U
  io.output.parent_node.need_merge := 0.B

  io.output.first_node.node_id := Integer.MAX_VALUE.U
  io.output.first_node.bbox := io.input.node.bbox
  io.output.first_node.first_child_or_primitive := 0.U
  io.output.first_node.level := 0.U
  io.output.first_node.merge_index := 0.U
  io.output.first_node.need_merge := 0.B
  io.output.first_node.primitive_count := 0.U

  io.output.second_node.node_id := Integer.MAX_VALUE.U
  io.output.second_node.bbox := io.input.node.bbox
  io.output.second_node.first_child_or_primitive := 0.U
  io.output.second_node.level := 0.U
  io.output.second_node.merge_index := 0.U
  io.output.second_node.need_merge := 0.B
  io.output.second_node.primitive_count := 0.U
}
