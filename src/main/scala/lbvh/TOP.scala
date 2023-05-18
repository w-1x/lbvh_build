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
    val morton_code_and_tri_id_and_valid = new Bundle {
      val morton_code = Output(UInt(Morton_WIDTH.W))
      val tri_id = Output(UInt(ADDR_WIDTH.W))
      val valid = Output(Bool())
    }
  }
}

class TOP extends Module {
  val io = IO(new TOPIO)

  val count = Module(new Count(1))
  val readTriangles = Module(new ReadTriangles)
  val compute_centres = Module(new Compute_centres)
  val compute_local_bbox = Module(new Compute_local_bbox)
  val compute_global_bbox = Module(new Compute_global_bbox)
  val compute_primitive_Morton = Module(new Compute_primitive_Morton)

  val clock_count_reg = RegInit(0.U(DATA_WIDTH.W)) // 时钟计数
  clock_count_reg := clock_count_reg + 1.U

  // count in
  count.io.input <> io.input

  // readTriangles in
  readTriangles.io.input <> count.io.output

  // compute_centres in
  compute_centres.io.input <> readTriangles.io.output

  // compute_local_bbox in
  compute_local_bbox.io.input <> readTriangles.io.output

  // compute_global_bbox in
  compute_global_bbox.io.input <> compute_local_bbox.io.output

  // compute_primitive_Morton in
  compute_primitive_Morton.io.input.centres_and_valid <> compute_centres.io.output.centres_and_valid
  compute_primitive_Morton.io.input.global_bbox_and_valid <> compute_global_bbox.io.output.global_bbox_and_valid

  io.output <> compute_primitive_Morton.io.output
}
