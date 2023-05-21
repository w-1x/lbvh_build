package lbvh

import chisel3._
import chisel3.util._
import hardfloat._
import config.Configs._
import fifo._

class Primitive_not_sortIO extends Bundle {
  val input = new Bundle {
    val morton_code = Input(UInt(Morton_WIDTH.W))
    val id = Input(UInt(ADDR_WIDTH.W))
    val valid = Input(Bool())
  }

  val output = new Bundle {
    val before_sort_primitive = Output(new Before_sort_primitive)
    val valid = Output(Bool())
  }
}

class Primitive_not_sort extends Module {
  val io = IO(new Primitive_not_sortIO)

  val tempMortonReg = Reg(UInt(Morton_WIDTH.W)) // 先缓存一下，id 读内存还需要一个周期
  val tri_mem = Module(new ReadTriangles)
  val compute_local_bbox = Module(new Compute_local_bbox)
  val validReg = RegInit(false.B)

  tempMortonReg := io.input.morton_code
  tri_mem.io.input.id := io.input.id
  compute_local_bbox.io.input := tri_mem.io.output

  io.output.before_sort_primitive.bbox := compute_local_bbox.io.output.bbox
  io.output.before_sort_primitive.triangle.id := compute_local_bbox.io.output.id
  io.output.before_sort_primitive.triangle := tri_mem.io.output.triangle
  io.output.before_sort_primitive.morton_code := tempMortonReg

  when(io.input.valid) {
    validReg := true.B
  }
  io.output.valid := validReg
}
