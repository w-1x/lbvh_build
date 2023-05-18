package lbvh

import chisel3._
import chisel3.util._
import hardfloat._
import config.Configs._
import fifo._

class TOPIO extends Bundle {
  val valid = Input(Bool())

  val morton_code_and_tri_id_and_valid = new Bundle {
    val morton_code = Output(UInt(Morton_WIDTH.W))
    val tri_id = Output(UInt(ADDR_WIDTH.W))
    val valid = Output(Bool())
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

  // count in
  count.io.valid <> io.valid

  // readTriangles in
  readTriangles.io.id <> count.io.id

  // compute_centres in
  compute_centres.io.triangle <> readTriangles.io.triangle

  // compute_local_bbox in
  compute_local_bbox.io.triangle <> readTriangles.io.triangle

  // compute_global_bbox in
  compute_global_bbox.io.bbox_and_tri_id <> compute_local_bbox.io.bbox_and_tri_id

  // compute_primitive_Morton in
  compute_primitive_Morton.io.centres_and_valid <> compute_centres.io.centres_and_valid
  compute_primitive_Morton.io.global_bbox_and_valid <> compute_global_bbox.io.global_bbox_and_valid

  io.morton_code_and_tri_id_and_valid <> compute_primitive_Morton.io.morton_code_and_tri_id_and_valid

  /*
  count.io.input_valid := io.input_valid
  readTriangles.io.input_addr := count.io.output
  compute_centres.io.input := readTriangles.io.output

  compute_local_bbox.io.input := readTriangles.io.output
  compute_global_bbox.io.input.tri_id := compute_local_bbox.io.output.tri_id
  compute_global_bbox.io.input.bbox := compute_local_bbox.io.output.bbox

  compute_primitive_Morton.io.input.centres.data := compute_centres.io.output
  compute_primitive_Morton.io.input.centres.valid := compute_centres.io.output.valid
  compute_primitive_Morton.io.input.global_bbox.data := compute_global_bbox.io.output.global_bbox
  compute_primitive_Morton.io.input.global_bbox.valid := compute_global_bbox.io.output.valid

  io.output := compute_primitive_Morton.io.output.morton_code
  io.output_valid := compute_primitive_Morton.io.output.valid
  io.output_tri_id := compute_primitive_Morton.io.output.tri_id
   */

}
