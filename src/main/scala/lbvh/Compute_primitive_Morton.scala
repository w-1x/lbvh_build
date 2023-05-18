package lbvh

import chisel3._
import chisel3.util._
import hardfloat._
import config.Configs._
import fifo._

class Compute_primitive_MortonIO extends Bundle {
  val global_bbox_and_valid = new Bundle {
    val global_bbox = Input(new BoundingBox)
    val valid = Input(Bool())
  }
  val centres_and_valid = new Bundle {
    val centres = Input(new Point)
    val valid = Input(Bool())
  }

  val morton_code_and_tri_id_and_valid = Output(new Bundle {
    val morton_code = Output(UInt(Morton_WIDTH.W))
    val tri_id = Output(UInt(ADDR_WIDTH.W))
    val valid = Output(Bool())
  })
}

class Compute_primitive_Morton extends Module { // 第一个图元的morton码需要花费1 + depth + 2 +5 + 1 + 1

  // 实现(centres - bbox.min)* grid_dim(也就是一个方向的morton码的最大值)
  //  /bbox(max - min)
  // 这里将centres扩大了三倍，避免了除法操作，所以需要将bbox扩大三倍
  val io = IO(new Compute_primitive_MortonIO)

  val bbox_validReg = RegInit(0.U(ADDR_WIDTH.W))
  when(io.global_bbox_and_valid.valid) {
    bbox_validReg := bbox_validReg + 1.U
  }

  val outputReg = RegInit(0.U(ADDR_WIDTH.W))
  val centresFifo = Module(new CombFifo(new Point, DEPTH))

  centresFifo.io.enq.bits := io.centres_and_valid.centres
  centresFifo.io.enq.valid := io.centres_and_valid.valid

  when(bbox_validReg >= 4.U) { // 取倒数需要5个周期，减法需要一个周期，乘法也需要一个周期 4 = 5 + 1 - 1 - 1
    centresFifo.io.deq.ready := true.B
  }.otherwise {
    centresFifo.io.deq.ready := false.B
  }

  val grid_dim = Wire(new Point)
  grid_dim.x := 3.U
  grid_dim.y := 3.U
  grid_dim.z := 3.U

// bbox(max- min) 1个周期

  val Fadd_1 = Module(new Float_Vector_sub)
  val Fadd_2 = Module(new Float_Vector_sub)

  Fadd_1.io.input_vector1 := io.global_bbox_and_valid.global_bbox.maxPoint
  Fadd_1.io.input_vector2 := io.global_bbox_and_valid.global_bbox.minPoint

//centres - bbox.min //单周期
  Fadd_2.io.input_vector1 := centresFifo.io.deq.bits
  Fadd_2.io.input_vector2 := io.global_bbox_and_valid.global_bbox.minPoint

//  1/(bbox(max - min))
  val FInverter = Module(new Float_Vector_inverter) // 初始化需要5个周期
  FInverter.io.input_vector := Fadd_1.io.out_vector
//乘积求结果 第一次需要1个周期，后面需要两个周期
  val Fmul_1 = Module(new Float_Vector_mul)
  val Fmul_2 = Module(new Float_Vector_mul)
  Fmul_1.io.input_vector1 := Fadd_2.io.out_vector
  Fmul_1.io.input_vector2 := grid_dim

  Fmul_2.io.input_vector1 := Fmul_1.io.out_vector
  Fmul_2.io.input_vector2 := FInverter.io.out_vector

  val out_1 = Wire(Vec(Morton_WIDTH_by_per_axis, UInt(3.W)))

  for (i <- 0 until (Morton_WIDTH_by_per_axis)) {
    out_1(i) := Cat(
      Fmul_2.io.out_vector.z(Morton_WIDTH_by_per_axis - i - 1),
      Fmul_2.io.out_vector.y(Morton_WIDTH_by_per_axis - i - 1),
      Fmul_2.io.out_vector.x(Morton_WIDTH_by_per_axis - i - 1)
    )
  }

  io.morton_code_and_tri_id_and_valid.morton_code := Cat(out_1)

  when(centresFifo.io.deq.ready) { // output 等于多少就相当于ready之后过了多少个周期
    outputReg := outputReg + 1.U
  }

  when(outputReg >= 3.U) { // 3 = 队列输出之后数据进行一次加法，两次次乘法共需要三个周期
    io.morton_code_and_tri_id_and_valid.valid := true.B
  }.otherwise {
    io.morton_code_and_tri_id_and_valid.valid := false.B
  }

  io.morton_code_and_tri_id_and_valid.tri_id := (outputReg - 3.U) % DEPTH.U
}
