package lbvh

import chisel3._
import hardfloat._
import config.Configs._

class Float_Vector_add extends Module { // 单周期
  val io = IO(new Bundle {
    val input_vector1 = Input(new Point)
    val input_vector2 = Input(new Point)
    val out_vector = Output(new Point)
  })

  val Fadd_1 = Module(new Float_Add)
  val Fadd_2 = Module(new Float_Add)
  val Fadd_3 = Module(new Float_Add)

  Fadd_1.io.a := io.input_vector1.x
  Fadd_1.io.b := io.input_vector2.x

  Fadd_2.io.a := io.input_vector1.y
  Fadd_2.io.b := io.input_vector2.y

  Fadd_3.io.a := io.input_vector1.z
  Fadd_3.io.b := io.input_vector2.z

  io.out_vector.x := Fadd_1.io.out
  io.out_vector.y := Fadd_2.io.out
  io.out_vector.z := Fadd_3.io.out
}
