package lbvh

import chisel3._
import chisel3.util._
import hardfloat._
import config.Configs._

class Compute_centresIO extends Bundle {
  val input = Input(new Triangle)
  val output = Output(new Bundle {
    val centres = new Point
    val valid = Bool()
  })
}

class Compute_centres extends Module { // 2周期
  val io = IO(new Compute_centresIO)

  val float_adds = Array.fill(6) {
    Module(new Float_Add)
  }

  val stateReg = RegInit(false.B)

  val temp_input_Reg = Reg(Vec(3, UInt(DATA_WIDTH.W)))

  temp_input_Reg(0) := io.input.point_2.x
  temp_input_Reg(1) := io.input.point_2.y
  temp_input_Reg(2) := io.input.point_2.z

  float_adds(0).io.a := io.input.point_0.x
  float_adds(0).io.b := io.input.point_1.x

  float_adds(1).io.a := io.input.point_0.y
  float_adds(1).io.b := io.input.point_1.y

  float_adds(2).io.a := io.input.point_0.z
  float_adds(2).io.b := io.input.point_1.z

  float_adds(3).io.a := float_adds(0).io.out
  float_adds(3).io.b := temp_input_Reg(0)

  float_adds(4).io.a := float_adds(1).io.out
  float_adds(4).io.b := temp_input_Reg(1)

  float_adds(5).io.a := float_adds(2).io.out
  float_adds(5).io.b := temp_input_Reg(2)

  io.output.centres.x := float_adds(3).io.out
  io.output.centres.y := float_adds(4).io.out
  io.output.centres.z := float_adds(5).io.out

  when(io.input.tri_id === 1.U) { // stateReg 下个周期才会变true
    stateReg := true.B
  }

  io.output.valid := stateReg

}
