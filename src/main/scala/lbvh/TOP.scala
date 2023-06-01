package lbvh

import chisel3._
import chisel3.util._
import hardfloat._
import config.Configs._
import fifo._

class TOPIO extends Bundle {
  val input = new Bundle {
    val valid = Input(Bool())
  }

  val output1 = new Bundle {
    val node = Output(new Node)
    val valid = Output(Bool())
  }
  val output2 = new Bundle {
    val node = Output(new Node)
    val valid = Output(Bool())
  }
  val count = Output(UInt(ADDR_WIDTH.W))
}

class TOP extends Module {
  val io = IO(new TOPIO)

  val count = Module(new Count(1))
  val readTriangle = Module(new ReadTriangle)
  val compute_centres = Module(new Compute_centres)
  val compute_local_bbox = Module(new Compute_local_bbox)
  val compute_global_bbox = Module(new Compute_global_bbox)
  val compute_primitive_Morton = Module(new Compute_primitive_Morton)

  val sort_primitive_by_mortoncode = Module(new Sort_primitive_by_mortoncode)

  val create_leaves = Module(new Create_leaves)
  val compute_node_level = Module(new Compute_node_level)
  val compute_need_and_merge = Module(new Compute_need_and_merge)
  val init_node = Module(new Init_node)
  val input_node = Module(new Input_node2)
  val merge = Module(new Merge)
  val output_node = Module(new Output_node)
  val result = Module(new Result)
  val new_comput_need_and_merge = Module(new New_comput_need_and_merge)

  val clock_count_reg = RegInit(0.U(DATA_WIDTH.W)) // 时钟计数
  clock_count_reg := clock_count_reg + 1.U

  // count in
  count.io.input <> io.input

  // readTriangles in
  readTriangle.io.input <> count.io.output

  // compute_centres in
  compute_centres.io.input <> readTriangle.io.output

  // compute_local_bbox in
  compute_local_bbox.io.input <> readTriangle.io.output

  // compute_global_bbox in
  compute_global_bbox.io.input <> compute_local_bbox.io.output

  // compute_primitive_Morton in
  compute_primitive_Morton.io.input.centres_and_valid <> compute_centres.io.output
  compute_primitive_Morton.io.input.global_bbox_and_valid <> compute_global_bbox.io.output

  // sort_primitive_by_mortoncode in
  sort_primitive_by_mortoncode.io.input <> compute_primitive_Morton.io.output

  // primitive_store in
  create_leaves.io.input <> sort_primitive_by_mortoncode.io.output

  // compute_node_level in
  compute_node_level.io.input <> sort_primitive_by_mortoncode.io.output

  // compute_need_and_merge in
  compute_need_and_merge.io.input <> compute_node_level.io.output

  // init_node in
  init_node.io.input1 <> create_leaves.io.leaves
  init_node.io.input2 <> compute_need_and_merge.io.output

  // input_node in
  input_node.io.input1 <> init_node.io.output
  input_node.io.input2 <> new_comput_need_and_merge.io.output

  // merge in
  merge.io.input <> input_node.io.output

  // output_node in
  output_node.io.input <> merge.io.output

  // result in
  result.io.input2 <> output_node.io.output2
  result.io.input3 <> output_node.io.output3

  // new_comput_need_and_merge in
  new_comput_need_and_merge.io.input <> output_node.io.output1

  io.output1 <> result.io.output1
  io.output2 <> result.io.output2

  io.count <> clock_count_reg
}
