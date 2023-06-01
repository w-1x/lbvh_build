package lbvh

import chisel3._
import chisel3.util._
import hardfloat._
import config.Configs._
import fifo._

class Merge_nodeIO extends Bundle {
  val input = new Bundle {
    val begin = Input(UInt(ADDR_WIDTH.W))
    val next_begin = Input(UInt(ADDR_WIDTH.W))
    val next_end = Input(UInt(ADDR_WIDTH.W))
    val node1 = Input(new Node)
    val node2 = Input(new Node)
  }

  val output = new Bundle { // 组合逻辑
    val parents_node = Output(new Node)
    val first_node = Output(new Node)
    val second_node = Output(new Node)
  }
}

class Merge_node extends Module {
  val io = IO(new Merge_nodeIO)

  val merge2_bbox = Module(new Merge2_bbox)
//size_t unmerged_index = unmerged_begin + i + 1 - begin - merged_index[i]; ,merge_index 最小是1
  io.output.parents_node.node_id := io.input.next_begin + io.input.node1.node_id + 1.U - io.input.begin - io.input.node1.merge_index
  io.output.first_node.node_id := io.input.next_end + (io.input.node1.merge_index - 1.U) * 2.U
  io.output.second_node.node_id := io.output.first_node.node_id + 1.U

  merge2_bbox.io.input1_bbox := io.input.node1.bbox
  merge2_bbox.io.input2_bbox := io.input.node2.bbox
  io.output.parents_node.bbox := merge2_bbox.io.output_bbox
  io.output.first_node.bbox := io.input.node1.bbox
  io.output.second_node.bbox := io.input.node2.bbox

  io.output.parents_node.primitive_count := 0.U
  io.output.first_node.primitive_count := io.input.node1.primitive_count
  io.output.second_node.primitive_count := io.input.node2.primitive_count

  io.output.parents_node.first_child_or_primitive := io.output.first_node.node_id
  io.output.first_node.first_child_or_primitive := io.input.node1.first_child_or_primitive
  io.output.second_node.first_child_or_primitive := io.input.node2.first_child_or_primitive

  io.output.parents_node.level := io.input.node2.level
  io.output.first_node.level := 0.U
  io.output.second_node.level := 0.U

  io.output.parents_node.merge_index := 0.U
  io.output.parents_node.need_merge := 0.B

  io.output.first_node.merge_index := 0.U
  io.output.first_node.need_merge := 0.B
  io.output.second_node.merge_index := 0.U
  io.output.second_node.need_merge := 0.B

}
