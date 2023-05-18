package lbvh

import chisel3._
import chisel3.util._
import hardfloat._
import config.Configs._
import fifo._

class Primitive_not_sort extends Module {
  val io = IO(new Bundle {
    val input = Input(new Bundle {
      val valid = Input(Bool())
      val tri_id = Input(UInt(ADDR_WIDTH.W))
      val bbox = Input(new BoundingBox)
      val morton_code = Input(UInt(Morton_WIDTH.W))
    })

    val output = Output(new Bundle {
      val before_sort_primitive = Output(new Before_sort_primitive)
    })
  })

  val tempMortonReg = Reg(UInt(Morton_WIDTH.W)) // 先缓存一下，tri_id 读内存还需要一个周期

  val tri_mem = Module(new ReadTriangles)
  val compute_local_bbox = Module(new Compute_local_bbox)

  tempMortonReg := io.input.morton_code
  tri_mem.io.input_addr := io.input.tri_id
  compute_local_bbox.io.input := tri_mem.io.output

  io.output.before_sort_primitive.bbox := compute_local_bbox.io.output.bbox
  io.output.before_sort_primitive.triangle.tri_id := compute_local_bbox.io.output.tri_id
  io.output.before_sort_primitive.triangle := tri_mem.io.output
  io.output.before_sort_primitive.morton_code := tempMortonReg
}
