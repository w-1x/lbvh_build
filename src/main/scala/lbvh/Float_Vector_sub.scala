package lbvh

import chisel3._
import hardfloat._
import config.Configs._
import chisel3.util._

class Float_Vector_sub extends Module { // 单周期
  val io = IO(new Bundle {
    val input_vector1 = Input(new Point)
    val input_vector2 = Input(new Point)
    val out_vector = Output(new Point)
  })

  val Fadd_1 = Module(new Float_Add)
  val Fadd_2 = Module(new Float_Add)
  val Fadd_3 = Module(new Float_Add)

  val sign1 = !io.input_vector2.x(31)
  val sub_1 = Cat(sign1, io.input_vector2.x(30, 0))

  val sign2 = !io.input_vector2.y(31)
  val sub_2 = Cat(sign2, io.input_vector2.y(30, 0))

  val sign3 = !io.input_vector2.z(31)
  val sub_3 = Cat(sign3, io.input_vector2.z(30, 0))

  Fadd_1.io.a := io.input_vector1.x
  Fadd_1.io.b := sub_1

  Fadd_2.io.a := io.input_vector1.y
  Fadd_2.io.b := sub_2

  Fadd_3.io.a := io.input_vector1.z
  Fadd_3.io.b := sub_3

  io.out_vector.x := Fadd_1.io.out
  io.out_vector.y := Fadd_2.io.out
  io.out_vector.z := Fadd_3.io.out
}
