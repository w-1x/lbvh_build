package lbvh

import chisel3._
import chisel3.util._
import hardfloat._
import config.Configs._
import fifo._

class TOP extends Module {
  val io = IO(new Bundle {
    val input = Input(new Bundle {
      val valid = Bool()
    })

    val output = Output(new Bundle {
      val data = UInt(Morton_WIDTH.W)
      val tri_id = UInt(ADDR_WIDTH.W)
      val valid = Bool()
    })
  })

  val count = Module(new Count(1))
  val readTriangles = Module(new ReadTriangles)
  val compute_centres = Module(new Compute_centres)
  val compute_local_bbox = Module(new Compute_local_bbox)
  val compute_global_bbox = Module(new Compute_global_bbox)
  val compute_primitive_Morton = Module(new Compute_primitive_Morton)

  readTriangles.io.input_addr <> count.io.output
  compute_centres.io.input <> readTriangles.io.output
  compute_local_bbox.io.input <> readTriangles.io.output
  compute_primitive_Morton.io.input.centres <> compute_centres.io.output
  compute_primitive_Morton.io.input.global_bbox <> compute_global_bbox.io.output
  compute_global_bbox.io.input <> compute_local_bbox.io.output
  io.output <> compute_primitive_Morton.io.output

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
