package lbvh

import chisel3._
import hardfloat._
import config.Configs._

class Float_Vector_mul extends Module { // 单周期
  val io = IO(new Bundle {
    val input_vector1 = Input(new Point)
    val input_vector2 = Input(new Point)
    val out_vector = Output(new Point)
  })

  val Fmul_1 = Module(new Float_MUL)
  val Fmul_2 = Module(new Float_MUL)
  val Fmul_3 = Module(new Float_MUL)

  Fmul_1.io.a := io.input_vector1.x
  Fmul_1.io.b := io.input_vector2.x

  Fmul_2.io.a := io.input_vector1.y
  Fmul_2.io.b := io.input_vector2.y

  Fmul_3.io.a := io.input_vector1.z
  Fmul_3.io.b := io.input_vector2.z

  io.out_vector.x := Fmul_1.io.actual.out
  io.out_vector.y := Fmul_2.io.actual.out
  io.out_vector.z := Fmul_3.io.actual.out
}
