package lbvh

import chisel3._
import chisel3.util._
import hardfloat._
import config.Configs._

class Compute_global_bboxIO extends Bundle {
  val input = new Bundle {
    val bbox = Input(new BoundingBox)
    val id = Input(UInt(ADDR_WIDTH.W))
  }

  val output = new Bundle {
    val global_bbox = Output(new BoundingBox)
    val valid = Output(Bool())
  }
}

class Compute_global_bbox extends Module { // DEPTH + 1 个周期
  val io = IO(new Compute_global_bboxIO)
  val clock_count_reg = RegInit(0.U(DATA_WIDTH.W))
  clock_count_reg := clock_count_reg + 1.U

  val tri_countReg1 = RegInit(false.B)
  val tri_countReg2 = RegInit(false.B)

  val temp_bigger_bbox = Reg(new BoundingBox)

  val compute_big_bbox = Module(new Compute_bigger_bbox)
  compute_big_bbox.io.input.bbox1 := io.input.bbox
  compute_big_bbox.io.input.bbox2 := temp_bigger_bbox

  when(clock_count_reg === 0.U) {
    temp_bigger_bbox := io.input.bbox
  }.otherwise {
    temp_bigger_bbox := compute_big_bbox.io.out_update_bbox
  }

  val mulity = Array.fill(6) {
    Module(new Float_MUL)
  }

  mulity(0).io.a := temp_bigger_bbox.maxPoint.x
  mulity(0).io.b := "h40400000".U // 3
  mulity(1).io.a := temp_bigger_bbox.minPoint.x
  mulity(1).io.b := "h40400000".U // 3

  mulity(2).io.a := temp_bigger_bbox.maxPoint.y
  mulity(2).io.b := "h40400000".U // 3
  mulity(3).io.a := temp_bigger_bbox.minPoint.y
  mulity(3).io.b := "h40400000".U // 3

  mulity(4).io.a := temp_bigger_bbox.maxPoint.z
  mulity(4).io.b := "h40400000".U // 3
  mulity(5).io.a := temp_bigger_bbox.minPoint.z
  mulity(5).io.b := "h40400000".U // 3

  io.output.global_bbox.maxPoint.x := mulity(0).io.actual.out
  io.output.global_bbox.maxPoint.y := mulity(2).io.actual.out
  io.output.global_bbox.maxPoint.z := mulity(4).io.actual.out

  io.output.global_bbox.minPoint.x := mulity(1).io.actual.out
  io.output.global_bbox.minPoint.y := mulity(3).io.actual.out
  io.output.global_bbox.minPoint.z := mulity(5).io.actual.out

  when(io.input.id === DEPTH.U - 1.U) { // 乘法需要一个周期 ,tri_countReg下周期才变化
    tri_countReg1 := true.B
  }
  tri_countReg2 := tri_countReg1

  io.output.valid := tri_countReg2
}
