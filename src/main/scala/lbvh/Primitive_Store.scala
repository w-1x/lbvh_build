package lbvh

import chisel3._
import chisel3.util._
import hardfloat._
import config.Configs._
import fifo._

class Primitive_StoreIO extends Bundle {
  val input = new Bundle {
    val morton_code = Input(UInt(Morton_WIDTH.W))
    val indice = Input(UInt(ADDR_WIDTH.W))
    val valid = Input(Bool())
  }

  val leaves = new Bundle {
    val node = Output(new Node)
    val valid = Output(Bool())
  }

  val triangle = Output(new Triangle)

}

class Primitive_Store extends Module {
  val io = IO(new Primitive_StoreIO)

  val clock_count_reg = RegInit(
    0.U(DATA_WIDTH.W)
  ) // 用时钟来表示图元进入的该模块的顺序，即排序后图元的顺序
  val tri_mem = Module(new ReadTriangle)
  val compute_local_bbox = Module(new Compute_local_bbox)
  val out_node_id = RegInit(0.U(ADDR_WIDTH.W))
  val validReg = RegInit(false.B)

  val mortonReg = RegInit(0.U(Morton_WIDTH.W))

  when(io.input.valid) {
    clock_count_reg := (clock_count_reg + 1.U) % DEPTH.U
    out_node_id := Begin.U + clock_count_reg
    validReg := true.B
  }
  tri_mem.io.input.id := io.input.indice
  compute_local_bbox.io.input := tri_mem.io.output

  io.leaves.node.node_id := out_node_id
  io.leaves.node.bbox := compute_local_bbox.io.output.bbox
  io.leaves.node.primitive_count := 1.U
  io.leaves.node.first_child_or_primitive := compute_local_bbox.io.output.id
  io.leaves.node.level := 0.U
  io.leaves.node.need_merge := false.B
  io.leaves.node.merge_index := 0.U
  io.leaves.valid := validReg

  io.triangle := tri_mem.io.output.triangle
}
