package hardfloat

import chisel3._

class Float_CMP_le extends Module { // 组合逻辑
  val io = IO(new Bundle {
    val a = Input(Bits(32.W))
    val b = Input(Bits(32.W))
    val out_bool = Output(Bool())

  })
  val fcmp = Module(new ValExec_CompareRecF32_le)
  fcmp.io.a := io.a
  fcmp.io.b := io.b
  fcmp.io.expected.out := 0.U(32.W)
  fcmp.io.expected.exceptionFlags := 0.U(5.W)

  io.out_bool := fcmp.io.actual.out

}
