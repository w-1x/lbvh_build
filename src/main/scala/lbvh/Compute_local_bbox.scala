package lbvh

import chisel3._
import chisel3.util._
import hardfloat._
import config.Configs._

class Compute_local_bboxIO extends Bundle {
  val input = new Bundle {
    val triangle = Input(new Triangle)
  }

  val output = new Bundle {
    val bbox = Output(new BoundingBox)
    val id = Output(UInt(ADDR_WIDTH.W))
  }

}

class Compute_local_bbox extends Module { // 组合逻辑，不需要时延
  val io = IO(new Compute_local_bboxIO)

  val point_computer = Array.fill(3) {
    Module(new Compute_max_minpoint)
  }

  point_computer(0).io.input_vec.x := io.input.triangle.point_0.x
  point_computer(0).io.input_vec.y := io.input.triangle.point_1.x
  point_computer(0).io.input_vec.z := io.input.triangle.point_2.x

  point_computer(1).io.input_vec.x := io.input.triangle.point_0.y
  point_computer(1).io.input_vec.y := io.input.triangle.point_1.y
  point_computer(1).io.input_vec.z := io.input.triangle.point_2.y

  point_computer(2).io.input_vec.x := io.input.triangle.point_0.z
  point_computer(2).io.input_vec.y := io.input.triangle.point_1.z
  point_computer(2).io.input_vec.z := io.input.triangle.point_2.z

  io.output.bbox.maxPoint.x := point_computer(0).io.out_vec_max
  io.output.bbox.maxPoint.y := point_computer(1).io.out_vec_max
  io.output.bbox.maxPoint.z := point_computer(2).io.out_vec_max

  io.output.bbox.minPoint.x := point_computer(0).io.out_vec_min
  io.output.bbox.minPoint.y := point_computer(1).io.out_vec_min
  io.output.bbox.minPoint.z := point_computer(2).io.out_vec_min

  io.output.id := io.input.triangle.id
}
