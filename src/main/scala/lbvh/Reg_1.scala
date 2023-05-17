package lbvh

import chisel3._

class Reg_1 extends Module {
  val io = IO(new Bundle {
    val input = Input(UInt(32.W))

    val output = Output(UInt(32.W))
  })

  val tempReg = RegInit(0.U(32.W))

  tempReg := io.input
  io.output := tempReg
}

class Reg_input extends Module {
  val io = IO(new Bundle {
    val output = Output(UInt(32.W))
  })

  val countReg = RegInit(0.U(32.W))
  val reciveReg = RegInit(0.U(32.W))

  val reg = Module(new Reg_1)

  countReg := countReg + 1.U

  reg.io.input := countReg
  reciveReg := reg.io.output
  io.output := reciveReg
}
