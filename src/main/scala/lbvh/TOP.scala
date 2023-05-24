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

  val output = new Bundle {
    val node = Output(new Node)
    val valid = Output(Bool())
    //   val triangle = Output(new Triangle)
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
  val primitive_store = Module(new Primitive_Store)
  // val primitive_not_sort_copy = Module(new Primitive_not_sort_copy)

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
  primitive_store.io.input <> sort_primitive_by_mortoncode.io.output
  printf(
    "dut:clock = %d, indice = %d\n",
    clock_count_reg,
    primitive_store.io.input.indice
  )

  io.output <> primitive_store.io.leaves
  io.count <> clock_count_reg
}
